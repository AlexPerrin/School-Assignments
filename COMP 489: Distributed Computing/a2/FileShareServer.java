/*
 * Program Name: FileShareServer.java
 * Description: CORBA-based P2P File-Sharing Application Server Component
 * 
 * This program implements the server side of a peer-to-peer file sharing system
 * using CORBA (Common Object Request Broker Architecture) for distributed communication.
 * The server maintains a PostgreSQL database registry of shared files and their
 * locations, allowing clients to register files, search for files, and discover
 * file owners for direct peer-to-peer transfers.
 * 
 * Expected Inputs:
 * - CORBA client requests for file registration, unregistration, search, and owner lookup
 * - Command line arguments for ORB initialization
 * 
 * Expected Outputs:
 * - CORBA responses containing file search results and owner information
 * - Console logging of all client operations
 * - Database updates for file registry management
 * 
 * Called by: FileShareClient instances via CORBA invocations
 * Will call: PostgreSQL database for persistent storage operations
 * 
 */

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.CosNaming.*;
import java.sql.*;
import java.util.*;

/**
 * Implementation class for the FileShare CORBA interface.
 * including registration, search, and ownership tracking through a PostgreSQL database.
 */
class FileShareImpl extends FileSharePOA {
    // Database connection parameters
    private String dbUrl = "jdbc:postgresql://localhost:5432/fileshare";
    private String dbUser = "postgres";
    private String dbPass = "postgres";

    /**
     * Registers a file for sharing in the P2P network.
     * Stores the file name along with the client's network address and port
     * in the database for other clients to discover.
     * 
     * @param filename The name of the file to register for sharing
     * @param clientAddress The IP address of the client hosting the file
     * @param clientPort The port number where the client's file server is listening
     */
    @Override
    public synchronized void registerFile(String filename, String clientAddress, int clientPort) {
        System.out.println("REGISTER REQUEST: " + filename + " from " + clientAddress + ":" + clientPort); 
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            // Insert file registration into database
            String sql = "INSERT INTO files (file_name, client_address, client_port) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filename);
            stmt.setString(2, clientAddress);
            stmt.setInt(3, clientPort);
            stmt.executeUpdate();
            
            System.out.println("REGISTER SUCCESS: Registered " + filename + " from " + clientAddress + ":" + clientPort);
        } catch (SQLException e) {
            System.err.println("REGISTER FAILED: " + e.getMessage());
        }
    }
    
    /**
     * Unregisters a file from the sharing network.
     * Removes the file entry from the database, making it unavailable for download.
     * 
     * @param filename The name of the file to unregister
     * @param clientAddress The IP address of the client that owns the file
     * @param clientPort The port number of the client's file server
     */
    @Override
    public synchronized void unregisterFile(String filename, String clientAddress, int clientPort) {
        System.out.println("UNREGISTER REQUEST: " + filename + " from " + clientAddress + ":" + clientPort);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            // Remove file registration from database
            String sql = "DELETE FROM files WHERE file_name = ? AND client_address = ? AND client_port = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filename);
            stmt.setString(2, clientAddress);
            stmt.setInt(3, clientPort);
            stmt.executeUpdate();
            
            System.out.println("UNREGISTER RESULT: Removed " + filename + " from " + clientAddress + ":" + clientPort);
        } catch (SQLException e) {
            System.err.println("UNREGISTER FAILED: " + e.getMessage());
        }
    }
    
    /**
     * Searches for files matching a given query string.
     * Performs a case-insensitive wildcard search on file names in the database.
     * 
     * @param searchQuery The search term to match against file names
     * @return A newline-separated string of matching file names, or empty string if none found
     */
    @Override
    public synchronized String searchFiles(String searchQuery) {
        System.out.println("SEARCH REQUEST: " + searchQuery);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            // Search for files with names containing the query string
            String sql = "SELECT DISTINCT file_name FROM files WHERE file_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + searchQuery + "%");
            ResultSet rs = stmt.executeQuery();
            
            // Collect all matching file names
            List<String> filenames = new ArrayList<>();
            while (rs.next()) {
                filenames.add(rs.getString("file_name"));
            }
            
            // Return results as newline-separated string
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
     * Used by clients to establish direct connections for file downloads.
     * 
     * @param filename The name of the file to locate
     * @return A string in format "ip:port" of the file owner, or empty string if not found
     */
    @Override
    public synchronized String getFileOwner(String filename) {
        System.out.println("OWNER REQUEST: " + filename);
        
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            // Query database for file owner's network location
            String sql = "SELECT client_address, client_port FROM files WHERE file_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, filename);
            ResultSet rs = stmt.executeQuery();
        
            if (rs.next()) {
                // Found the file - return owner's address and port
                String ip = rs.getString("client_address");
                int port = rs.getInt("client_port");
                String result = ip + ":" + port;
                System.out.println("OWNER SUCCESS: " + filename + " -> " + result);
                return result;
            }
        
            // File not found in database
            System.out.println("OWNER FAILED: " + filename + " not found");
            return "";
        
        } catch (SQLException e) {
            System.err.println("OWNER FAILED: " + e.getMessage());
            return "";
        }
    }
}

/**
 * Main server class that initializes the CORBA environment and serves client requests.
 * This class sets up the CORBA ORB, registers the FileShare service with the naming service,
 * and enters the main service loop to handle client requests.
 */
public class FileShareServer {
    /**
     * Main entry point for the FileShare server application.
     * Initializes CORBA infrastructure and starts serving client requests.
     * 
     * @param args Command line arguments passed to the CORBA ORB
     */
    public static void main(String[] args) {
        try {
            // Initialize the CORBA Object Request Broker
            ORB orb = ORB.init(args, null);
            
            // Get reference to the root POA (Portable Object Adapter) and activate it
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();
            
            // Create the servant object that implements the FileShare interface
            FileShareImpl servant = new FileShareImpl();
            
            // Convert servant to CORBA object reference
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(servant);
            FileShare server = FileShareHelper.narrow(ref);
            
            // Register the service with the CORBA naming service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            NameComponent path[] = ncRef.to_name("FileShareService");
            ncRef.rebind(path, server);
            
            System.out.println("FileShare Server ready and waiting...");

            // Enter the main service loop - blocks until ORB shutdown
            orb.run();
            
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}