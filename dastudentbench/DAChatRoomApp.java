package dastudentbench;
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
/**
*
* @author infinite
*/
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class DAChatRoomApp implements Runnable {
// The client socket
private static Socket clientSocket = null;
// The output stream
static PrintStream os = null;
// The input stream
private static DataInputStream is = null;
private static String inputLine = null;
private static boolean closed = false;
 ChatRoom cr;
public void setIs(String st)
{
inputLine=st;
}
public DAChatRoomApp(ChatRoom tcr)
{int portNumber = 2222;
// The default host.
String host = "localhost";
/*
* Open a socket on a given host and port. Open input and output streams.
*/

        try {
            clientSocket = new Socket(host, portNumber);

            os = new PrintStream(clientSocket.getOutputStream());
         is = new DataInputStream(clientSocket.getInputStream());

             cr=tcr;
        } catch (IOException ex) {
            Logger.getLogger(DAChatRoomApp.class.getName()).log(Level.SEVERE, null, ex);
        }
}
public void in(ChatRoom tcr) {
//launch(DAChatRoomApp.class, args);
// The default port.
    cr=tcr;
int portNumber = 2222;
// The default host.
String host = "localhost";
/*
* Open a socket on a given host and port. Open input and output streams.
*/
try {
clientSocket = new Socket(host, portNumber);
//inputLine = cr.getInputText();
os = new PrintStream(clientSocket.getOutputStream());
is = new DataInputStream(clientSocket.getInputStream());
System.out.println("connected");
} catch (UnknownHostException e) {
System.err.println("Don't know about host " + host);
} catch (IOException e) {
System.err.println("Couldn't get I/O for the connection to the host "
+ host);
}
/*
* If everything has been initialized then we want to write some data to the
* socket we have opened a connection to on the port portNumber.
*/
String prev=null;
if (clientSocket != null && os != null && is != null) {
                /* Create a thread to read from the server. */
                new Thread(new DAChatRoomApp(cr)).start();
                System.out.println("thread started");


                /*
                * Close the output stream, close the input stream, close the socket.
                */
//                os.close();
  //              is.close();
    //            clientSocket.close();
            
} 
}
/*
* Create a thread to read from the server. (non-Javadoc)
*
* @see java.lang.Runnable#run()
*/
@SuppressWarnings("deprecation")
public void run() {
/*
* Keep on reading from the socket till we receive "Bye" from the
* server. Once we received that then we want to break.
*/
    System.out.println("haan haan run ho rela h");
String responseLine;
try {
while ((responseLine = is.readLine()) != null) {
    System.out.println("rp:"+responseLine);
    
cr.getTa().append(responseLine+"\n");
if (responseLine.indexOf("*** Bye") != -1)
break;
}
closed = true;
} catch (IOException e) {
System.err.println("IOException: " + e);
}
}
}