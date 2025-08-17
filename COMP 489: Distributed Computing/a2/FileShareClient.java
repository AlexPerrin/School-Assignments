/*
 * Program Name: FileShareClient.java
 * Description: CORBA-based P2P File-Sharing Application Client Component
 * 
 * This program implements the client side of a peer-to-peer file sharing system.
 * Each client acts as both a file consumer (downloading files from other clients)
 * and a file provider (serving files to other clients). The client uses CORBA
 * to communicate with a central registry server for file discovery, and direct
 * socket connections for actual file transfers between peers.
 * 
 * Expected Inputs:
 * - User commands via console: register, unregister, search, download, quit
 * - File requests from other clients via socket connections
 * - Command line arguments for CORBA ORB initialization
 * 
 * Expected Outputs:
 * - File registration confirmations and search results
 * - Downloaded files saved to local filesystem
 * - File content served to requesting peers
 * - User interface prompts and feedback messages
 * 
 * Called by: User interaction and other FileShareClient instances
 * Will call: FileShareServer via CORBA, and other clients via direct socket connections
 * 
 * Author: [Your Name]
 * Course: COMP 489 - Distributed Systems
 * Assignment: Assignment 2 - CORBA-based P2P File-Sharing Application
 * Date: [Current Date]
 */

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import java.net.*;
import java.io.*;
import java.util.Scanner;

/**
 * Main client class implementing both file sharing client and server functionality.
 * This class provides a command-line interface for users to interact with the P2P network
 * while simultaneously running a socket server to serve files to other clients.
 */
public class FileShareClient {

    // Network configuration
    private static int socketPort;  // Port where this client's socket server listens
    private static FileShare server;  // Reference to the CORBA file registry server
    private static final String CLIENT_IP = "127.0.0.1"; // Client always uses localhost

    /**
     * Socket server implementation using Runnable interface for handling file requests from other clients.
     * This allows the client to act as both a file requester and file provider in the P2P network.
     * Runs continuously in a separate thread to handle incoming file requests.
     */
    private static class SocketServerRunnable implements Runnable {
        /**
         * Main socket server loop that accepts connections and delegates file requests.
         * Creates a new thread for each incoming connection to handle concurrent requests.
         */
        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(0)) { // Use 0 for random available port
                socketPort = serverSocket.getLocalPort();
                System.out.println("Client socket server listening on port " + socketPort);
                
                // Continuously listen for incoming file requests from other clients
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    // Handle each file request in a separate thread for concurrent processing
                    new Thread(new FileRequestHandler(clientSocket)).start();
                }
            } catch (IOException e) {
                System.err.println("Socket server error: " + e.getMessage());
            }
        }
    }

    /**
     * Handles individual file requests from other clients.
     * Implements Runnable to allow concurrent handling of multiple file requests.
     * Validates requests, checks file availability, and streams file content to requesters.
     */
    private static class FileRequestHandler implements Runnable {
        private final Socket clientSocket;

        /**
         * Constructor for file request handler.
         * @param clientSocket The socket connection from the requesting client
         */
        public FileRequestHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        /**
         * Processes a single file request from another client.
         * Reads the requested filename, validates it exists, and streams the file content.
         */
        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 OutputStream socketOut = clientSocket.getOutputStream()) {
                
                // Read the requested filename from the requesting client
                String filename = in.readLine();

                // Validate the filename request
                if (filename == null || filename.isEmpty()) {
                    out.println("ERROR");
                    throw new IllegalArgumentException("Filename cannot be null or empty - client sent invalid request");
                }

                File file = new File(filename);

                // Check if the requested file exists locally
                if (file.exists()) {
                    // Send confirmation that file is available
                    out.println("OK");
                    out.flush(); // Ensure the OK response is sent immediately
                        
                    // Stream the file content to the requesting client
                    try (FileInputStream fileIn = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fileIn.read(buffer)) != -1) {
                            socketOut.write(buffer, 0, bytesRead);
                        }
                        socketOut.flush(); // Ensure all data is sent
                        System.out.println("\nServed file: " + filename + " (" + file.length() + " bytes)");
                        System.out.print("> "); // Reset the prompt
                        System.out.flush(); // Ensure prompt is displayed immediately
                    }
                } else {
                    // File not found, inform the requesting client
                    out.println("NOT_FOUND");
                    System.out.println("\nFile not found: " + filename);
                    System.out.print("> "); // Reset the prompt
                    System.out.flush(); // Ensure prompt is displayed immediately
                }

            } catch (Exception e) {
                System.err.println("\nERROR handling file request: " + e.getMessage());
                System.out.print("> "); // Reset the prompt
                System.out.flush(); // Ensure prompt is displayed immediately
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
     * This allows the client to act as a server for other clients wanting to download files.
     * The socket server runs as a daemon thread so it won't prevent application shutdown.
     */
    private static void startSocketServer() {
        Thread socketServerThread = new Thread(new SocketServerRunnable());
        socketServerThread.setDaemon(true); // Daemon thread will exit when main thread exits
        socketServerThread.start();
    }

    /**
     * Downloads a file from another client in the P2P network.
     * First queries the CORBA server to find the file owner, then establishes a direct socket connection
     * for the actual file transfer. This implements the P2P aspect of the system.
     * 
     * @param filename The name of the file to download from the remote client
     * @param outputFilename The local filename to save the downloaded file as
     */
    private static void downloadFile(String filename, String outputFilename) {
        // Query the CORBA server to find who owns the requested file
        String owner = server.getFileOwner(filename);
        if (owner.isEmpty()) {
            System.out.println("File not found: " + filename);
            return;
        }
        
        // Parse the owner's address and port
        String[] parts = owner.split(":");
        String ownerAddress = parts[0];
        int ownerPort = Integer.parseInt(parts[1]);
        
        // Establish direct socket connection with the file owner
        try (Socket socket = new Socket(ownerAddress, ownerPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             FileOutputStream fileOut = new FileOutputStream(outputFilename)) {
            
            // Request the specific file from the owner
            out.println(filename);
            
            // Wait for confirmation that the file is available
            String response = in.readLine();
            if (!"OK".equals(response)) {
                System.out.println("File not available from peer");
                return;
            }
            
            // Receive and save the file content
            byte[] buffer = new byte[1024];
            int bytesRead;
            int totalBytesRead = 0;
            InputStream socketIn = socket.getInputStream();
            while ((bytesRead = socketIn.read(buffer)) != -1) {
                fileOut.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
            }
            fileOut.flush(); // Ensure all data is written to disk
            System.out.println("Downloaded: " + filename + " -> " + outputFilename + " (" + totalBytesRead + " bytes)");
            
        } catch (Exception e) {
            System.err.println("Peer connection error: " + e.getMessage());
        }
    }

    /**
     * Main entry point for the FileShare client application.
     * Initializes CORBA connection, starts the socket server, and provides the user interface.
     * 
     * @param args Command line arguments passed to the CORBA ORB
     */
    public static void main(String[] args) {
        try {
            // Initialize CORBA ORB and connect to the naming service
            ORB orb = ORB.init(args, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            
            // Lookup the FileShare service from the naming service
            NameComponent path[] = ncRef.to_name("FileShareService");
            org.omg.CORBA.Object obj = ncRef.resolve(path);
            server = FileShareHelper.narrow(obj);

            // Start the socket server for handling file requests from other clients
            startSocketServer();

            // Wait a moment for socket server to initialize and get its port
            Thread.sleep(1000);

            // Main client interface for user interaction
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nCommands:");
            System.out.println("  register <filename> - Register a file for sharing");
            System.out.println("  unregister <filename> - Stop sharing a file");
            System.out.println("  search <query> - Search for files");
            System.out.println("  download <filename> [output_filename] - Download a file");
            System.out.println("  quit - Exit");

            // Command processing loop - handles user input and executes commands
            while (true) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();
                
                if (input.isEmpty()) continue;
                
                // Parse command and arguments
                String[] parts = input.split(" ");
                String command = parts[0].toLowerCase();
                
                // Execute the appropriate command based on user input
                switch (command) {
                    case "register":
                        if (parts.length >= 2) {
                            String fileName = parts[1];
                            File file = new File(fileName);
                            if (file.exists()) {
                                // Register the file with the CORBA server
                                server.registerFile(fileName, CLIENT_IP, socketPort);
                                System.out.println("Registered: " + fileName);
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
                            // Remove the file from the CORBA server registry
                            server.unregisterFile(fileName, CLIENT_IP, socketPort);
                            System.out.println("Unregistered: " + fileName);
                        } else {
                            System.out.println("Usage: unregister <filename>");
                        }
                        break;
                        
                    case "search":
                        if (parts.length >= 2) {
                            String query = parts[1];
                            // Search for files matching the query on the CORBA server
                            String searchResults = server.searchFiles(query);
                            if (searchResults.isEmpty()) {
                                System.out.println("No files found matching: " + query);
                            } else {
                                System.out.println("Search results for '" + query + "':");
                                String[] files = searchResults.split("\n");
                                for (String file : files) {
                                    System.out.println("  " + file);
                                }
                            }
                        } else {
                            System.out.println("Usage: search <query>");
                        }
                        break;
                        
                    case "download":
                        if (parts.length >= 2) {
                            String fileName = parts[1];
                            String outputFileName = fileName; // Default to same name
                            
                            // Check if user provided a custom output filename
                            if (parts.length >= 3) {
                                outputFileName = parts[2];
                            }
                            
                            // Download the file from another client
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