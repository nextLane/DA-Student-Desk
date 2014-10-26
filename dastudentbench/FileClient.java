/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dastudentbench;

/**
 *
 * @author infinite
 */
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;
 
public class FileClient {
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private ObjectInputStream inputStream = null;
    private ArrayList<Object[]> lst;
    private boolean isConnected = false;
   // private String sourceFilePath = "/home/infinite/LostPD/FileHistory/Aditi/INFINITE/Data/C/Users/Aditi/Desktop/10670275_10152695983380040_4450924185963884366_n (2014_09_20 21_01_07 UTC).jpg";
    private FileEvent fileEvent = null;
    private String destinationPath = "/home/infinite/Desktop/ClientUpload/";
    //private String destinationPath1="/tmp/fromServer"; 
    private File dstFile = null;
    private FileOutputStream fileOutputStream = null;
    public FileClient() {
    
        
        
    }
 
    /**
     * Connect with server code running in local host or in any other host
     */
    public void connect() {
        while (!isConnected) {
            try {
              
                    socket = new Socket("localHost", 4444);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                inputStream = new ObjectInputStream(socket.getInputStream());
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    public ArrayList<Object[]> fetchRecords() {
        try {
            outputStream.writeObject("2");
        } catch (IOException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
             lst=(ArrayList<Object[]>)inputStream.readObject();
        } catch (IOException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Object ol[];
        //DefaultTableModel tm= (DefaultTableModel)jTable1.getModel();  
        
        System.out.println(""+lst.size());
    return lst;    
    }    
        //Now writing the FileEvent object to socket
        
 public void receiveFile(String fn)
 {               
                          System.out.println("majhdar mei hun");      
            try {
                   outputStream.writeObject("3");
                   outputStream.writeObject(fn);
                   
                   System.out.println("locha e ulfat nahi huya");      
   
               } catch (IOException ex) {
                 //  System.out.println("locha e ulfat ho gy");      
   
                   Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
               }
               try {
                     System.out.println("locha e ulfat ho gy");      
   
                   fileEvent = (FileEvent) inputStream.readObject();
     System.out.println("locha e ulfat hone de re");   
                   System.out.println("locha e ulfat ho gy:"+fileEvent.getFilename());      
   
                   System.out.println("majhdar mei hun, :"+fileEvent.getFilename());      
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
                         System.out.println("File received : " + outputFile + " is successfully saved ");
                       }
               catch (IOException ex) {
             //        System.out.println("locha e ulfat ho gy");      
   
                   Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
               }             catch (ClassNotFoundException ex) {
                   Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
               }
                  
        }
    

    
    /**
     * Sending FileEvent object.
     */
    public void sendFile(String sourceFilePath) {
        System.out.println("o fenny re");
        try {
            outputStream.writeObject("1");
        } catch (IOException ex) {
            Logger.getLogger(FileClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        fileEvent = new FileEvent();
        String fileName = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1, sourceFilePath.length());
        String path = sourceFilePath.substring(0, sourceFilePath.lastIndexOf("/") + 1);
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
            outputStream.writeObject(fileEvent);
            System.out.println("Done...Going to exit");
            Thread.sleep(3000);
            //socket.close();
           // System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 
    }
 
  //  public static void main(String[] args) {
    //    FileClient client = new FileClient();
      // client.connect();
        //client.sendFile();
    //}
}

