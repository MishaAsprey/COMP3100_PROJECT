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
    // numeric values of ASCII characters
    private enum ASCII {
        ZERO(48),
        NINE(57),
        SPACE(32);
        
        private final int value;
        
        ASCII(int value) {
            this.value = value;
        }
        
        public int getValue() { return value; }
    }
    
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
        int CPUcores = 0;
        for (int i = 0; i < servers.length; i++) {
            int currServerCores = getServerCPU(servers[i]);
            if (currServerCores > CPUcores) {
                CPUcores = currServerCores;
            }
        }
        String firstLargestType = "";
        for (int i = 0; i < servers.length; i++) {
            int currServerCores = getServerCPU(servers[i]);
            if (currServerCores == CPUcores) {
                firstLargestType = servers[i];
                break;
            }
        }
        System.out.println("CHECKPOINT");
        return getServerName(firstLargestType);
    }
    
    private static String getServerName(String serverStr) {
        String largestServer = "";
        for (int i = 0; i < serverStr.length(); i++) {
            if (serverStr.charAt(i) != ASCII.SPACE.getValue()) {
                largestServer += serverStr.charAt(i);
            } else {
                return largestServer;
            }
        }
        return "ERROR";
    }
    
    private static int getServerCPU(String serverStr) {
        String cores = "";
        int startRecording = 0;
        for (int i = 0; i < serverStr.length(); i++) {
            if (serverStr.charAt(i) == ASCII.SPACE.getValue()) {
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
    
    private static int findLastLargestServer(String[] servers, String serverType) {
        String lastServer = "";
        
        for (int i = servers.length-1; i >= 0; i--) {
            if (getServerName(servers[i]).equals(serverType)) {
                lastServer = servers[i];
                break;
            }
        }
    
    
        String serverID = "";
        for (int i = serverType.length() + 1; i <= lastServer.length(); i++) {
            if (lastServer.charAt(i) >= ASCII.ZERO.getValue() && lastServer.charAt(i) <= ASCII.NINE.getValue()) {
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
            if (jobStr.charAt(i) == ASCII.SPACE.getValue()) {
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
                if (dataMsg.charAt(i) >= ASCII.ZERO.getValue() && dataMsg.charAt(i) <= ASCII.NINE.getValue()) {
                    num += dataMsg.charAt(i);
                } else if (dataMsg.charAt(i) == ASCII.SPACE.getValue()) {
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
            
            String largestServer = findLargestServers(servers);
            System.out.println(largestServer + "\n");
            int lastServer = findLastLargestServer(servers, largestServer);
            System.out.println("C2\n");
            System.out.println(largestServer);
            System.out.println(lastServer);
            
            
            sendMsg("OK\n");
            System.out.println(receiveMsg());
            
            // schedule the first job
            
            sendMsg("SCHD " + findJobID(temp) + " " + largestServer + " " + 0 + "\n");
            receiveMsg();

            // main loop to schedule jobs until there are no jobs left

            int x = 1;
            if (x > lastServer) { x = 0; }
            String lastMsg = "";
            while (!temp.equals("NONE")) {
                sendMsg("REDY\n");
                temp = receiveMsg();
                if (temp.toLowerCase().contains(("JCPL").toLowerCase())) { continue; }
                if (temp.toLowerCase().contains(("NONE").toLowerCase())) { break; }
                int j = findJobID(temp);
                System.out.println(findJobID(temp));
                sendMsg("SCHD " + j + " " + largestServer + " " + x + "\n");
                lastMsg = receiveMsg();
                System.out.println(findJobID(lastMsg));
                x++;
                if (x > lastServer) { x = 0; }
            }
            
            // Quit
            sendMsg("QUIT\n");
            
            output.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
