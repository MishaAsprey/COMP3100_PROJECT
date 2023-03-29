import java.io.*;
import java.net.*;

public class Client {
    // default ds-sim port
    final static int port = 50000;
    // declare a socket
    private static Socket socket;
    // declare input and output streams
    private static DataOutputStream output;
    private static BufferedReader input;
    
    private static void receiveMsg() {
        try {
            System.out.println(input.readLine());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private static void sendMsg(String msg) {
        try {
            output.write(msg.getBytes());
            output.flush();
        } catch (Exception e) {
            System.out.println(e);
        }
    }   
    
    public static void main(String argv[]) {
        try {
            // Initialise a socket
            socket = new Socket("localhost", Client.port);
            
            // Initialise input and output streams
            output = new DataOutputStream(socket.getOutputStream());
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // Handshake
            sendMsg("HELO\n");
            
            receiveMsg();
            
            sendMsg("AUTH user\n");
            
            receiveMsg();
            
            sendMsg("REDY\n");
            
            receiveMsg();
            
            // Quit
            sendMsg("QUIT\n");
            
            output.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
