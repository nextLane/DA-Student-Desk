/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dastudentbench;

/**
 *
 * @author infinite
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;

public class FileServer {
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private ObjectInputStream inputStream = null;
    private FileEvent fileEvent;
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;
 
    public FileServer() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FileServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
 
    /**
     * Accepts socket connection
     */
    public void doConnect() {
        try {
            serverSocket = new ServerSocket(4445);
            socket = serverSocket.accept();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    /**
     * Reading the FileEvent object and copying the file to disk.
     */
    public ResultSet getUploads()
    {
        String UID="root";
        String PWD="123";
        String DB="jdbc:mysql://localhost:3306/uploadFiles?user="+UID+"&password="+PWD;
        ResultSet rs = null;
        
        try {
            java.sql.Connection con = DriverManager.getConnection(DB);        
             java.sql.Statement stmt= con.createStatement();
             
             
            String query="SELECT * FROM UFiles ;";
           rs=stmt.executeQuery(query);
           con.close();
            
           
        }  
        catch (Exception e) {
            e.printStackTrace();
        }  
        return rs;
           
    }
    
    public void downloadFile() {
        String UID="root";
        String PWD="123";
        String DB="jdbc:mysql://localhost:3306/uploadFiles?user="+UID+"&password="+PWD;
        
        
        
        try {
            java.sql.Connection con = DriverManager.getConnection(DB);        
             java.sql.Statement stmt= con.createStatement();
             
             
            fileEvent = (FileEvent) inputStream.readObject();
            if (fileEvent.getStatus().equalsIgnoreCase("Error")) {
                System.out.println("Error occurred ..So exiting");
                System.exit(0);
            }
            String outputFile = fileEvent.getDestinationDirectory() + fileEvent.getFilename();
            if (!new File(fileEvent.getDestinationDirectory()).exists()) {
                new File(fileEvent.getDestinationDirectory()).mkdirs();
            }
            dstFile = new File(outputFile);
            fileOutputStream = new FileOutputStream(dstFile);
            fileOutputStream.write(fileEvent.getFileData());
            fileOutputStream.flush();
            fileOutputStream.close();
            System.out.println("Output file : " + outputFile + " is successfully saved ");
            Thread.sleep(3000);
             System.out.println(""+Calendar.getInstance().getTime());
            String query="INSERT INTO UFiles VALUES('"+fileEvent.getFilename()+"',"+fileEvent.getFileSize()+",'"+socket+"','"+Calendar.getInstance().getTime()+"');";
            stmt.executeUpdate(query);
            con.close();
            System.exit(0);
 
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    public static void main(String[] args) {
      FileServer server = new FileServer();
      server.doConnect();
      server.downloadFile();
    }
    }

