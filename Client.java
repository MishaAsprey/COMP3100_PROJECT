import java.io.*;
import java.net.*;

public class Client {
    final static int port = 50000;
    
    public static void main(String[] argv) {
        try {
            Socket socket = new Socket("localhost", Client.port);
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            output.write(("HELO\n").getBytes());
            output.flush();
            
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println(input.readLine());
            
            output.write(("AUTH user\n").getBytes());
            output.flush();
            
            System.out.println(input.readLine());
            
            output.write(("REDY user\n").getBytes());
            output.flush();
            
            System.out.println(input.readLine());
            
            output.write(("QUIT user\n").getBytes());
            output.flush();
            
            output.close();
            socket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
