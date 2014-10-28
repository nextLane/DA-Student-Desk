/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fileserver;

/**
 *
 * @author infinite
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author infinite
 */

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Calendar;
import java.util.List;

public class FileServer {
    //private ServerSocket serverSocket = null;
    //private Socket socket = null;
    //private ObjectInputStream inputStream = null;
    //private FileEvent fileEvent;
    //private File dstFile = null;
    //private FileOutputStream fileOutputStream = null;
   
    
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
    /*public void doConnect() {
        try {
            serverSocket = new ServerSocket(4445);
            socket = serverSocket.accept();
            inputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 */
    /**
     * Reading the FileEvent object and copying the file to disk.
     */
    
    public static void main(String[] args) {
   
        ServerSocket serverSocket = null;
         Socket socket = null;
      Socket clientSocket = null;  
        
        try {
            serverSocket = new ServerSocket(4444);
            System.out.println("Server started.");
        } catch (Exception e) {
            System.err.println("Port already in use.");
            System.exit(1);
        }

        while (true) {
            try {
                System.out.println("Waiting for your connection dude!");
                clientSocket = serverSocket.accept();
                System.out.println("Accepted connection : " + clientSocket);

                Thread t = new Thread(new CLIENTConnection(clientSocket));

                t.start();

            } catch (Exception e) {
                System.err.println("Error in connection attempt.");
            }
            System.out.println("I am capable of accepting more connections.");
        }
        
        
        
        
     // server.downloadFile();
    }
    }

class CLIENTConnection implements Runnable {

    private Socket clientSocket;
    //private BufferedReader in = null;
    private ObjectInputStream in = null;
    private ObjectOutputStream os = null;
    private FileEvent fileEvent;
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;
    private String destinationPath="/home/infinite/Desktop/ServerDownload/";
    public CLIENTConnection(Socket client) {
        this.clientSocket = client;
    }

    @Override
    public void run() {
        
            System.out.println("kahin toh hogi woh");
        try {
            os = new ObjectOutputStream(clientSocket.getOutputStream());
            
            in = new ObjectInputStream(
            clientSocket.getInputStream());
            Object clientSelection = null;
            while(true)
            {
                System.out.println("Keep sending!");
                try {
                clientSelection = in.readObject();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                   if(clientSelection.equals("1")) {
                    
                        
                        downloadFile();
                   } else if(clientSelection.equals("2"))
                   {
                    try {
                        getUploads();
                        //sendFile((String)in.readObject());
                    
                    } 
                    catch (SQLException ex) {
                        Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        }
                  else if(clientSelection.equals("3"))
                   {
                    try {
                        System.out.println("yaha tk aa gya hun");
                        sendFile((String)in.readObject());
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   }
                   
         //          try {
           //     clientSelection = in.readObject();
            //} catch (ClassNotFoundException ex) {
              //  Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
            //}
                                   
            //   in.close();
              // clientSocket.close();
               //  break;
            
        }
        } 
        catch (IOException ex) {
            Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
        }    
}

    public void downloadFile() {
        String UID="root";
        String PWD="123";
        String DB="jdbc:mysql://localhost:3306/uploadFiles?user="+UID+"&password="+PWD;
        
        
        
        try {
            java.sql.Connection con = DriverManager.getConnection(DB);        
             java.sql.Statement stmt= con.createStatement();
             
             
            fileEvent = (FileEvent) in.readObject();
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
            String query="INSERT INTO UFiles VALUES('"+fileEvent.getFilename()+"',"+fileEvent.getFileSize()+",'"+clientSocket+"','"+Calendar.getInstance().getTime()+"');";
            stmt.executeUpdate(query);
            con.close();
            
        //    System.exit(0);
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
         return;
    }
 public void getUploads() throws SQLException
    {  
        String UID="root";
        String PWD="123";
        String DB="jdbc:mysql://localhost:3306/uploadFiles?user="+UID+"&password="+PWD;
        ResultSet rs = null;
        java.sql.Connection con = null;
        try {
            con = DriverManager.getConnection(DB);        
             java.sql.Statement stmt= con.createStatement();
             
             
            String query="SELECT * FROM UFiles ;";
           rs=stmt.executeQuery(query);
         //  con.close();
            
           
        }  
        catch (Exception e) {
            e.printStackTrace();
        }
        int p=0;
    
    
        
        List<Object[]> records=new ArrayList<Object[]>();
while(rs.next()){
    p++;
    int cols = rs.getMetaData().getColumnCount();
    Object[] arr = new Object[cols];
    for(int i=0; i<cols; i++){
      arr[i] = rs.getObject(i+1);
    }
    records.add(arr);
}
            try {
                os.writeObject(records);
            } catch (IOException ex) {
                Logger.getLogger(CLIENTConnection.class.getName()).log(Level.SEVERE, null, ex);
            }

        
   con.close();
          //System.exit(0);
          
            return;
    }
 
    public void sendFile(String fileName) {
           fileEvent = new FileEvent();
        //String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String sourceFilePath = "/home/infinite/Desktop/ClientUpload/"+fileName;
        fileEvent.setDestinationDirectory(destinationPath);
        fileEvent.setFilename(fileName);
        fileEvent.setSourceDirectory(sourceFilePath);
        File file = new File(sourceFilePath);
        if (file.isFile()) {
            try {
                DataInputStream diStream = new DataInputStream(new FileInputStream(file));
                long len = (int) file.length();
                byte[] fileBytes = new byte[(int) len];
                int read = 0;
                int numRead = 0;
                while (read < fileBytes.length && (numRead = diStream.read(fileBytes, read,
                        fileBytes.length - read)) >= 0) {
                    read = read + numRead;
                }
                fileEvent.setFileSize(len);
                fileEvent.setFileData(fileBytes);
                fileEvent.setStatus("Success");
            } catch (Exception e) {
                e.printStackTrace();
                fileEvent.setStatus("Error");
            }
        } else {
            System.out.println("path specified is not pointing to a file");
            fileEvent.setStatus("Error");
        }
        //Now writing the FileEvent object to socket
        try {
            
            os.writeObject(fileEvent);
            System.out.println("name:"+fileEvent.getFilename());
            
            System.out.println("Sent to Client...Going to exit");
            Thread.sleep(3000);
           // System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 
     return;
    }
}