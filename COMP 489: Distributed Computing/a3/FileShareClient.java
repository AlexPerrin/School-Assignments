
 /*
 * Program Name: FileShareClient.java
 * Description: JAX-WS Web Service Client for P2P File-Sharing Application
 *              This program implements a peer-to-peer file sharing client that can both
 *              share files with other peers and download files from them. It uses a 
 *              centralized web service for file registration and discovery, but performs
 *              actual file transfers directly between peers via TCP sockets.
 * 
 * Expected Inputs: 
 *   - User commands via console: register, unregister, search, download, quit
 *   - File requests from other peers via socket connections
 * 
 * Expected Outputs/Results:
 *   - Console messages showing operation results
 *   - File transfers to/from other peers
 *   - Registration/unregistration confirmations
 * 
 * Called by: Main method called by JVM at program startup
 * Will call: FileShareService web service methods, socket operations for peer communication
 * 
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;
import javax.xml.ws.Service;
import javax.xml.namespace.QName;
import service.FileShareService;

/*
 * The functionality of this code is the same as FileShareServer.java from Assignmnet 2.
 * The definitions of the functions remains the same, with revisions made to use stubs provided by JAX-WS instead of CORBA.
 */

// Main client class implementing both file sharing client and server functionality.

public class FileShareClient {

    // Network configuration
    private static int socketPort;
    private static FileShareService webService;
    private static final String CLIENT_IP = "127.0.0.1";
    private static final String WEB_SERVICE_URL = "http://localhost:8080/FileShareService/FileShareService";

    /**
     * Socket server implementation using Runnable interface for handling file requests from other clients.
     */
    private static class SocketServerRunnable implements Runnable {
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(0)) {
                socketPort = serverSocket.getLocalPort();
                System.out.println("Client socket server listening on port " + socketPort);
                
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new FileRequestHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                System.err.println("Socket server error: " + e.getMessage());
            }
        }
    }

    /**
     * Handles individual file requests from other clients.
     */
    private static class FileRequestHandler implements Runnable {
        private final Socket clientSocket;

        public FileRequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 OutputStream socketOut = clientSocket.getOutputStream()) {
                
                String filename = in.readLine();

                if (filename == null || filename.isEmpty()) {
                    out.println("ERROR");
                    throw new IllegalArgumentException("Filename cannot be null or empty - client sent invalid request");
                }

                File file = new File(filename);

                if (file.exists()) {
                    out.println("OK");
                    out.flush();
                        
                    try (FileInputStream fileIn = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileIn.read(buffer)) != -1) {
                            socketOut.write(buffer, 0, bytesRead);
                        }
                        socketOut.flush();
                        System.out.println("\nServed file: " + filename + " (" + file.length() + " bytes)");
                        System.out.print("> ");
                        System.out.flush();
                    }
                } else {
                    out.println("NOT_FOUND");
                    System.out.println("\nFile not found: " + filename);
                    System.out.print("> ");
                    System.out.flush();
                }

            } catch (Exception e) {
                System.err.println("\nERROR handling file request: " + e.getMessage());
                System.out.print("> ");
                System.out.flush();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Starts the socket server in a separate thread to handle incoming file requests.
     */
    private static void startSocketServer() {
        Thread socketServerThread = new Thread(new SocketServerRunnable());
        socketServerThread.setDaemon(true);
        socketServerThread.start();
    }

    /**
     * Initializes the JAX-WS web service client connection.
     */
    private static void initializeWebService() {
        try {
            URL wsdlURL = new URL(WEB_SERVICE_URL + "?wsdl");
            QName qname = new QName("http://service/", "FileShareService");
            Service service = Service.create(wsdlURL, qname);
            webService = service.getPort(FileShareService.class);
            System.out.println("Connected to web service at: " + WEB_SERVICE_URL);
        } catch (Exception e) {
            System.err.println("Failed to connect to web service: " + e.getMessage());
            System.err.println("Make sure the FileShareService is deployed and running on Wildfly");
            System.exit(1);
        }
    }

    /**
     * Downloads a file from another client in the P2P network.
     */
    private static void downloadFile(String filename, String outputFilename) {
        try {
            String owner = webService.getFileOwner(filename);
            if (owner == null || owner.isEmpty()) {
                System.out.println("File not found: " + filename);
                return;
            }
            
            String[] parts = owner.split(":");
            if (parts.length != 2) {
                System.out.println("Invalid owner format received: " + owner);
                return;
            }
            
            String ownerAddress = parts[0];
            int ownerPort = Integer.parseInt(parts[1]);
            
            try (Socket socket = new Socket(ownerAddress, ownerPort);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 FileOutputStream fileOut = new FileOutputStream(outputFilename)) {
                
                out.println(filename);
                
                String response = in.readLine();
                if (!"OK".equals(response)) {
                    System.out.println("File not available from peer: " + response);
                    return;
                }
                
                byte[] buffer = new byte[1024];
                int bytesRead;
                int totalBytesRead = 0;
                InputStream socketIn = socket.getInputStream();
                while ((bytesRead = socketIn.read(buffer)) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
                fileOut.flush();
                System.out.println("Downloaded: " + filename + " -> " + outputFilename + " (" + totalBytesRead + " bytes)");
                
            } catch (Exception e) {
                System.err.println("Peer connection error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Web service error: " + e.getMessage());
        }
    }

    /**
     * Main entry point for the FileShare client application.
     */
    public static void main(String[] args) {
        try {
            initializeWebService();
            startSocketServer();
            Thread.sleep(1000);

            Scanner scanner = new Scanner(System.in);
            System.out.println("\nCommands:");
            System.out.println("  register <filename> - Register a file for sharing");
            System.out.println("  unregister <filename> - Stop sharing a file");
            System.out.println("  search <query> - Search for files");
            System.out.println("  download <filename> [output_filename] - Download a file");
            System.out.println("  quit - Exit");

            while (true) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) continue;
                
                String[] parts = input.split(" ");
                String command = parts[0].toLowerCase();
                
                switch (command) {
                    case "register":
                        if (parts.length >= 2) {
                            String fileName = parts[1];
                            File file = new File(fileName);
                            if (file.exists()) {
                                try {
                                    boolean success = webService.registerFile(fileName, CLIENT_IP, socketPort);
                                    if (success) {
                                        System.out.println("Registered: " + fileName);
                                    } else {
                                        System.out.println("Failed to register: " + fileName);
                                    }
                                } catch (Exception e) {
                                    System.err.println("Web service error during registration: " + e.getMessage());
                                }
                            } else {
                                System.out.println("File not found: " + fileName);
                            }
                        } else {
                            System.out.println("Usage: register <filename>");
                        }
                        break;
                        
                    case "unregister":
                        if (parts.length >= 2) {
                            String fileName = parts[1];
                            try {
                                boolean success = webService.unregisterFile(fileName, CLIENT_IP, socketPort);
                                if (success) {
                                    System.out.println("Unregistered: " + fileName);
                                } else {
                                    System.out.println("Failed to unregister: " + fileName + " (may not have been registered)");
                                }
                            } catch (Exception e) {
                                System.err.println("Web service error during unregistration: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Usage: unregister <filename>");
                        }
                        break;
                        
                    case "search":
                        if (parts.length >= 2) {
                            String query = parts[1];
                            try {
                                String searchResults = webService.searchFiles(query);
                                if (searchResults == null || searchResults.isEmpty()) {
                                    System.out.println("No files found matching: " + query);
                                } else {
                                    System.out.println("Search results for '" + query + "':");
                                    String[] files = searchResults.split("\n");
                                    for (String file : files) {
                                        if (!file.trim().isEmpty()) {
                                            System.out.println("  " + file);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                System.err.println("Web service error during search: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Usage: search <query>");
                        }
                        break;
                        
                    case "download":
                        if (parts.length >= 2) {
                            String fileName = parts[1];
                            String outputFileName = fileName;
                            
                            if (parts.length >= 3) {
                                outputFileName = parts[2];
                            }
                            
                            downloadFile(fileName, outputFileName);
                        } else {
                            System.out.println("Usage: download <filename> [output_filename]");
                        }
                        break;
                        
                    case "quit":
                    case "exit":
                        System.out.println("Goodbye!");
                        scanner.close();
                        System.exit(0);
                        break;
                        
                    default:
                        System.out.println("Unknown command: " + command);
                        System.out.println("Type 'quit' to exit or use one of the available commands.");
                }
            }
            
        } catch (Exception e) {
            System.err.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}