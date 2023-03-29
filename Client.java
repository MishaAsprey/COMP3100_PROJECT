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
    
    private static String receiveMsg() {
        try {
            String receivedMsg = input.readLine();
            System.out.println(receivedMsg);
            return receivedMsg;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
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
            
            sendMsg("GETS All\n");
            String dataMsg = receiveMsg();
            sendMsg("OK\n");
            
            // create a server array
            
            String num = "";
            int counter = 0;
            for (int i = 0; i < dataMsg.length(); i++) {
                if (dataMsg.charAt(i) >= 48 && dataMsg.charAt(i) <= 57) {
                    num += dataMsg.charAt(i);
                } else if (dataMsg.charAt(i) == 32) {
                    counter++;
                    if (counter >= 2) { break; }
                }
            }
                
            int numOfServers = Integer.parseInt(num);
            
            String[] msgs = new String[numOfServers];
            String[] servers = new String[numOfServers];
            System.out.println(numOfServers);
            for (int i = 0; i < numOfServers; i++) {
                msgs[i] = input.readLine();
            }
            for (int i = 0; i < numOfServers; i++) {
                System.out.println(msgs[i]);
            }
            
            // end // create a server array
            
            sendMsg("OK\n");
//            int k = 0;
//            while (k <= 10) {//!receiveMsg().equals("NONE\n")) {
//                for (int i = numOfServers, j = 0; i >= 0; i--, j++) {
//                   sendMsg("REDY\n");
//                    sendMsg("SCHD " + j + " joon 0\n");
//                    receiveMsg();
//                    k++;
//                }   
//            }
            int j = 0;
            while (j <= 20) {//!receiveMsg().equals("NONE\n")) {
                for (int i = numOfServers; i >= 2; i--) {
                    sendMsg("REDY\n");
                    sendMsg("SCHD " + j + " joon 0\n");
                    if (receiveMsg().contains("JCPL")) { break; }
                    j++;
                }
                if (receiveMsg().contains("JCPL")) { break; }
            }
                
                
            
            //sendMsg("SCHD 0 xlarge 0\n");
            //receiveMsg();
            
            // Quit
            sendMsg("QUIT\n");
            
            output.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
