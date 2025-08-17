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

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import java.sql.*;
import java.util.*;

/**
 * The functionality of this code is the same as FileShareServer.java from Assignmnet 2
 * with revisions made to use stubs provided by JAX-WS instead of CORBA.
 */
// Define the web wervice name
@WebService(name = "FileShareService", 
            serviceName = "FileShareService",
            targetNamespace = "http://service/")
// Define the interface bindings for the web service
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class FileShareService {
    
    // Database connection parameters
    private String dbUrl = "jdbc:postgresql://localhost:5432/fileshare";
    private String dbUser = "postgres";
    private String dbPass = "postgres";

    /**
     * Registers a file for sharing in the P2P network. 
     * Define a 
     */
    @WebMethod(operationName = "registerFile")
    public boolean registerFile(
            @WebParam(name = "filename") String filename,
            @WebParam(name = "clientAddress") String clientAddress,
            @WebParam(name = "clientPort") int clientPort) {
        
        System.out.println("REGISTER REQUEST: " + filename + " from " + clientAddress + ":" + clientPort);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            String sql = "INSERT INTO files (file_name, client_address, client_port) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filename);
            stmt.setString(2, clientAddress);
            stmt.setInt(3, clientPort);
            int rowsAffected = stmt.executeUpdate();
            
            boolean success = rowsAffected > 0;
            if (success) {
                System.out.println("REGISTER SUCCESS: Registered " + filename + " from " + clientAddress + ":" + clientPort);
            } else {
                System.out.println("REGISTER FAILED: No rows affected for " + filename);
            }
            return success;
            
        } catch (SQLException e) {
            System.err.println("REGISTER FAILED: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Unregisters a file from the sharing network.
     */
    @WebMethod(operationName = "unregisterFile")
    public boolean unregisterFile(
            @WebParam(name = "filename") String filename,
            @WebParam(name = "clientAddress") String clientAddress,
            @WebParam(name = "clientPort") int clientPort) {
        
        System.out.println("UNREGISTER REQUEST: " + filename + " from " + clientAddress + ":" + clientPort);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            String sql = "DELETE FROM files WHERE file_name = ? AND client_address = ? AND client_port = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filename);
            stmt.setString(2, clientAddress);
            stmt.setInt(3, clientPort);
            int rowsAffected = stmt.executeUpdate();
            
            boolean success = rowsAffected > 0;
            System.out.println("UNREGISTER RESULT: Removed " + filename + " from " + clientAddress + ":" + clientPort + 
                             " (rows affected: " + rowsAffected + ")");
            return success;
            
        } catch (SQLException e) {
            System.err.println("UNREGISTER FAILED: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches for files matching a given query string.
     */
    @WebMethod(operationName = "searchFiles")
    public String searchFiles(@WebParam(name = "searchQuery") String searchQuery) {
        System.out.println("SEARCH REQUEST: " + searchQuery);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            String sql = "SELECT DISTINCT file_name FROM files WHERE file_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            
            List<String> filenames = new ArrayList<>();
            while (rs.next()) {
                filenames.add(rs.getString("file_name"));
            }
            
            String result = String.join("\n", filenames);
            System.out.println("SEARCH SUCCESS: Found " + filenames.size() + " files");
            return result;
            
        } catch (SQLException e) {
            System.err.println("SEARCH FAILED: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * Retrieves the network location of a file's owner.
     */
    @WebMethod(operationName = "getFileOwner")
    public String getFileOwner(@WebParam(name = "filename") String filename) {
        System.out.println("OWNER REQUEST: " + filename);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            String sql = "SELECT client_address, client_port FROM files WHERE file_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filename);
            ResultSet rs = stmt.executeQuery();
        
            if (rs.next()) {
                String ip = rs.getString("client_address");
                int port = rs.getInt("client_port");
                String result = ip + ":" + port;
                System.out.println("OWNER SUCCESS: " + filename + " -> " + result);
                return result;
            }
        
            System.out.println("OWNER FAILED: " + filename + " not found");
            return "";
        
        } catch (SQLException e) {
            System.err.println("OWNER FAILED: " + e.getMessage());
            return "";
        }
    }
}