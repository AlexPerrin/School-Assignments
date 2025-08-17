import java.io.*;
import java.net.*;
import java.net.http.*;

/*
 * COMP489: Distributed Computing - Assignment 1
 * Alex Perrin
 * 
 * Class: Client
 * 
 * Description: A proxy server that forwards an HTTP request from Client to Webserver, and then forwards the response. If the HTTP request is to an external resource, Proxy will handle the HTTP request itself.
 * 
 * Expected inputs:
 * - HTTP request from input stream of socket listening on localhost:8081
 * 
 * Expected output:
 * - HTTP response to output stream of socket listening on localhost:8081
 * 
 * Called by whom:
 * - Client process.
 * 
 * will call what:
 * - ClientHandler threads.
 */

public class Proxy {

    public static void main(String[] args) {

        // Initialize and close serverSocket
        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            System.out.println("Proxy Server is listening on port " + serverSocket.getLocalPort());

            // Create a new thread for each socket connection.
            while (true) {
                Socket proxySocket = serverSocket.accept();
                System.out.println("Client connected");
                Thread thread = new Thread(new ClientHandler(proxySocket));
                thread.start();
            }

        // Catche and print IOException erros for ServerSocket.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
 * Class: ClientHandler
 * 
 * Description: Helper class for implenting multithreading, and HTTP request handling.
 * 
 * Expected inputs:
 * - HTTP request from input stream of socket listening on localhost:8081
 * 
 * Expected output:
 * - HTTP response to output stream of socket listening on localhost:8081
 * 
 * Called by whom:
 * - Proxy class.
 * 
 * will call what:
 * - WebServer process on localhost socket, or external webserver on socket over the internet.
 */
class ClientHandler implements Runnable {

    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        
        // Initialize and close socket, BufferedReader, and BufferedOutputStream with Client.
        try (Socket client = clientSocket;
            // BufferedReader for reading HTTP request as lines of text.
            BufferedReader clientReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            // BufferedOutputStream for handling HTTP responses as byte stream. Use byte stream over text stream because data may be text or image data.
            BufferedOutputStream clientOutput = new BufferedOutputStream(client.getOutputStream());) {

            // Read HTTP request line and print to console.
            String requestLine = clientReader.readLine();
            System.out.println(requestLine);
            
            // If the HTTP request is empty, or not a GET method, return a 400 Bad Request error.
            if (requestLine == null || requestLine.isEmpty() || !requestLine.startsWith("GET")) {
                clientOutput.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                clientOutput.flush();
            }

            // Extract the target from the GET request
            String target = requestLine.split(" ")[1];
            
            // Initialize variable for parsing the target using the URL class constructor.
            URL parsedURL;

            // Target is a relative path (eg /testfile.html, /testfile.jpg, /)
            if (target.startsWith("/")) {
                // Assume all relative URLs are directed to the local WebServer.
                // Append protocol as HTTP, and host as localhost to form full URL, or will throw MalformedURLException
                parsedURL = new URL("http://localhost/" + target);
            } 
            
            // Target is an absolute URL without protocol (eg localhost/testfile.html, google.com),
            else if (target.startsWith("localhost")) {
                // Append protocol as HTTP to form full URL, or will throw MalformedURLException
                parsedURL = new URL("http://" + target);
            } 

            else { 
                // Assume target is a full URL (eg http://localhost/testfile.html, http://google.com)
                parsedURL= new URL(target);
            }

            // If the target is localhost or 127.0.0.1, forward the request with WebServer.
            if (parsedURL.getHost().equals("localhost") || parsedURL.getHost().equals("127.0.0.1")) {
                System.out.println("Forwarding to WebServer");
                // Initialize and close a socket, BufferedWriter, and InputStream with WebServer
                try (Socket serverSocket = new Socket("localhost", 8080);
                    // BufferedReader for writing HTTP request as lines of text.
                    BufferedWriter serverWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
                    // BufferedOutputStream for handling HTTP responses as byte stream. Use byte stream over text stream because data may be text or image data.
                    InputStream serverInput = serverSocket.getInputStream();) {

                    // Send parsed request to the WebServer
                    // When using unbuffered streams, make sure to flush the writer.
                    serverWriter.write("GET " + parsedURL.getPath() + " HTTP/1.1\r\n");
                    serverWriter.flush();

                    // Forward response to Client
                    serverInput.transferTo(clientOutput);
                    clientOutput.flush();
                }

            } else {
                // If the target is not localhost or 127.0.0.1, handle the request to the external server.
                System.out.println("Forwarding to internet");
                
                // Use Java's built-in HTTP module to handle the requests. 
                try (HttpClient httpClient = HttpClient.newHttpClient();) {
                    
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(parsedURL.toURI())
                            .GET()
                            .build();
        
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    
                    // The Client output stream is byte stream, so convert the response text to bytes, and flush the stream when done. 
                    clientOutput.write(("HTTP/1.1 " + response.statusCode() + "\r\n").getBytes());
                    clientOutput.write((response.headers()+"\r\n").getBytes());
                    clientOutput.write(("\r\n").getBytes());
                    clientOutput.write((response.body()+"\r\n").getBytes());
                    clientOutput.flush();
                
                // Catch and print any exceptions with the HttpClient
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        // Catch and print any exceptions with the Client socket.
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
