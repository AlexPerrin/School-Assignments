import java.io.*;
import java.net.*;
import java.util.Scanner;

// program/function name, a description, expected inputs, expected outputs/results, “called by whom,” and “will call what.”

/*
 * COMP489: Distributed Computing - Assignment 1: Proxy Server by Alex Perrin 
 * 
 * Class: Client
 * 
 * Description: A client that takes a URL from user input, sends a request to the proxy server,
 * and prints the response.
 * 
 * Expected inputs:
 * - Full URL from stdin, must be in the form http://localhost/testfile.html
 * 
 * Expected output:
 * - Forwarded HTTP response from the proxy.
 * 
 * Called by whom:
 * - Human actor using interactive shell.
 * 
 * will call what:
 * - HTTP request to Proxy server listening on socket localhost:8081
 */

public class Client {

    public static void main (String[] args) {

        // Initialize and close a scanner to receive user input.
        try (Scanner scanner = new Scanner(System.in);) {
            
            // Each iteration handles a url input.
            while (true) {

                // read url from scanner
                System.out.print("Enter URL: ");
                String scannerLine = scanner.nextLine();

                // Check if URL is empyty.
                if (scannerLine == null || scannerLine.isEmpty()) {
                    System.out.println("Error: URL cannot be empty.");
                    continue;
                }
                
                // Check if URL is malformed.
                try {
                    new URL(scannerLine);  
                } catch (MalformedURLException e) {
                    System.out.println("Error: Malformed URL.");
                    continue;
                }

                // Initialize and close a socket on the proxy server listening on localhost:8080
                try (Socket socket = new Socket("localhost", 8081);
                    // Initialize and close a BufferedReader on the socket input stream, assume the client will all data as text.
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    // Initialize and close a PrintWriter on the socket output stream, only need to send lines of text for HTTP requests.
                    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);) {
                    
                    // Send HTTP request to Proxy server
                    printWriter.println("GET " + scannerLine + " HTTP/1.1");

                    // Read the full HTTP response, bufferedReader will handle parsing the lines of the HTTP headers and data.
                    String responseLine;
                    while ((responseLine = bufferedReader.readLine()) != null) {
                        System.out.println(responseLine);
                    }

                // Catch IO exceptions from the Socket, BufferedReader, and PrintWriter.
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } 
        } 
    }
}