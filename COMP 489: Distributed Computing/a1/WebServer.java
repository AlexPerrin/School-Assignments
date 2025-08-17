import java.io.*;
import java.net.*;

/*
 * COMP489: Distributed Computing - Assignment 1: Proxy Server by Alex Perrin 
 * 
 * Class: WebServer
 * 
 * Description: A basic Web server that returns a valid HTTP response on localhost:8080.
 * Responds to GET request for testfile.html and testfile.jpg with status and Content-Type.
 * If the requested file is not found returns 404 Not Found.
 * If the request is not a GET method returns 400 Bad Request.
 * 
 * Expected Inputs:
 * - From socket OutputStream
 *      GET /testfile.html HTTP/1.1
 *      GET /testfile.jpg HTTP/1.1 
 * 
 * Expected Output:
 * - On ServerSocket.accept()
 *      "Client connected" to STDOUT
 * - Each incoming request sent to the socket
 *      "GET /testfile.html HTTP/1.1" to STDOUT
 *      "GET /testfile.html HTTP/1.1" to STDOUT
 */

public class WebServer {
    public static void main(String[] args) throws IOException {
        
        // Initialize a socket on localhost:8080
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Web Server is listening on port " + serverSocket.getLocalPort());
        
        while(true) {
            
            // Create a socket when a client connects
            Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            
            // Initialize a Writer object to write a character stream to the socket output.
            // HTTP responses are text, so should be encoded in a character stream and not a byte stream.
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            
            // Initialize a Reader object to read a character stream from the socket input.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Read the request line from the Reader character stream.
            String requestLine = bufferedReader.readLine();
            // Print request to STDOUT
            System.out.println(requestLine); 
            
            // Check for Bad Request
            if (requestLine != null && requestLine.startsWith("GET")) {

                // Parse filename from request.
                String url = requestLine.split(" ")[1];

                //Manually parse the target for the filename

                //If the target is full URL, remove the protocol ie. http://
                if(url.contains("//")){
                    url = url.split("//")[1];
                }

                //If the target is a relative URL, remove the / from the filename ie. /testfile.html
                String filename;
                if(url.length() > 1) {
                    filename = url.split("/")[1];
                } else {
                    filename = url;
                }

                //Create a file object using the filename
                File file = new File(filename);
            
                // Check for file on disk.
                if (file.isFile()) {
                    
                    // Write an HTTP response to the socket.
                    // Status Line
                    printWriter.println("HTTP/1.1 200 OK");
                    System.out.println("HTTP/1.1 200 OK");

                    // Set Content-Type Headers based on file type.
                    if (filename.equals("testfile.html"))
                        printWriter.println("Content-Type: text/html");

                    if (filename.equals("testfile.jpg"))
                        printWriter.println("Content-Type: image/jpeg");
                
                    // Print an newline between the HTTP headers and body.
                    printWriter.println();
                    

                    // Initialize a Stream object to read the file on disk as a byte stream.
                    // We can use the transferTo method to just write the byte stream directly to the socket.
                    InputStream inputStream = new FileInputStream(file);
                    inputStream.transferTo(socket.getOutputStream());
                    
                    // Close any streams and sockets.
                    System.out.println("Client disconnected");
                    inputStream.close();
                    bufferedReader.close();
                    printWriter.close();
                    socket.close();
                
                } else {
                    
                    // If the file cannot be read from disk, return a 404 Not Found error.

                    printWriter.println("HTTP/1.1 404 Not Found");
                    System.out.println("HTTP/1.1 404 Not Found");
                    
                    // Close any streams and sockets.
                    printWriter.close();
                    bufferedReader.close();
                    socket.close();

                }
            
            } else {

                // If the request is empty, or is not a GET method, return a 400 Bad Request error.

                printWriter.println("HTTP/1.1 400 Bad Request");
                System.out.println("HTTP/1.1 400 Bad Request");
                
                // Close any streams and sockets.
                printWriter.close();
                bufferedReader.close();
                socket.close();

            }   
        }
    }
}