import java.io.*;
import java.net.*;

public class Client {
    // identify current user
    final static String username = System.getProperty("user.name");
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
    
    private static String findLargestServers(String[] servers) {
        int EOA = servers.length-1; // End of Array
        String lastElement = servers[EOA];
        String largestServer = "";
        for (int i = 0; i < lastElement.length(); i++) {
            if (lastElement.charAt(i) != 32) {
                largestServer += lastElement.charAt(i);
            } else {
                return largestServer;
            }
        }
        return "ERROR";
    }
    // new
    private static int getServerCPU(String serverStr) {
        String cores = "";
        int startRecording = 0;
        for (int i = 0; i < serverStr.length(); i++) {
            if (serverStr.charAt(i) == 32) {
                startRecording++;
                if (startRecording >= 5) {
                    return Integer.parseInt(cores);
                }
                if (startRecording == 4) { i++; }
            }
            if (startRecording == 4) {
                cores += serverStr.charAt(i);
            }
        }
        return -1;
    }
    
    private static int findLastLargestServer(String lastServer, String serverType) {
        String serverID = "";
        for (int i = serverType.length() + 1; i <= lastServer.length(); i++) {
            if (lastServer.charAt(i) >= 48 && lastServer.charAt(i) <= 57) {
                serverID += lastServer.charAt(i);
            } else {
                return Integer.parseInt(serverID);
            }
        }
        return -1;
    }
    
    private static int findJobID(String jobStr) {
        String jobID = "";
        int counter = 0;
        
        for (int i = ("JOBN").length()+1; i < jobStr.length(); i++) {
            if (jobStr.charAt(i) == 32) {
                counter++;
                i++;
            }
            if (counter == 1) {
                jobID += jobStr.charAt(i);
            } else if (counter >= 2) {
                return Integer.parseInt(jobID);
            }
        }
        return -1;
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
            
            sendMsg("AUTH " + Client.username + "\n");
            
            receiveMsg();
            
            sendMsg("REDY\n");
            
            String temp = receiveMsg();
            
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
            
            String[] servers = new String[numOfServers];
            System.out.println(numOfServers);
            for (int i = 0; i < numOfServers; i++) {
                servers[i] = input.readLine();
            }
            for (int i = 0; i < numOfServers; i++) {
                System.out.println(servers[i]);
            }
            
            //
            for (int i = 0; i < servers.length; i++) {
                System.out.println(getServerCPU(servers[i]));
            }
            //
            
            //System.out.println(findLargestServers(servers));
            
            String largestServer = findLargestServers(servers);
            int lastServer = findLastLargestServer(servers[servers.length-1], largestServer);
            System.out.println(largestServer);
            System.out.println(lastServer);
            
            // end // create a server array
            
            sendMsg("OK\n");
            System.out.println(receiveMsg());
            
            sendMsg("SCHD " + findJobID(temp) + " " + largestServer + " " + 0 + "\n");
            receiveMsg();

            //int j = 0;
            int x = 1;
            if (x > lastServer) { x = 0; }
            String lastMsg = "";
            //String temp = "";
            while (!temp.equals("NONE")) {
                sendMsg("REDY\n");
                temp = receiveMsg();
                if (temp.toLowerCase().contains(("JCPL").toLowerCase())) { continue; }
                if (temp.toLowerCase().contains(("NONE").toLowerCase())) { break; }
                //System.out.println("TEST: " + temp);
                int j = findJobID(temp);
                System.out.println(findJobID(temp));
                sendMsg("SCHD " + j + " " + largestServer + " " + x + "\n");
                lastMsg = receiveMsg();
                System.out.println(findJobID(lastMsg));
                //j++;
                x++;
                if (x > lastServer) { x = 0; }
            }
            
            //while (!temp.equals("NONE")) {
             //   sendMsg("SCHD " + j + " " + largestServer + " " + x + "\n");
             //   receiveMsg();
              //  sendMsg("REDY\n");
              //  while 
            
                
            //sendMsg("REDY\n");
                
                
            
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
