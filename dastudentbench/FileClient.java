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
 
public class FileClient {
    private Socket socket = null;
    private ObjectOutputStream outputStream = null;
    private boolean isConnected = false;
   // private String sourceFilePath = "/home/infinite/LostPD/FileHistory/Aditi/INFINITE/Data/C/Users/Aditi/Desktop/10670275_10152695983380040_4450924185963884366_n (2014_09_20 21_01_07 UTC).jpg";
    private FileEvent fileEvent = null;
    private String destinationPath = "/tmp/downloads/";
 
    public FileClient() {
 
    }
 
    /**
     * Connect with server code running in local host or in any other host
     */
    public void connect() {
        while (!isConnected) {
            try {
                socket = new Socket("localHost", 4445);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    /**
     * Sending FileEvent object.
     */
    public void sendFile(String sourceFilePath) {
        
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

