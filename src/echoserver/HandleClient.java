    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

/**
 *
 * @author nikolai
 */
public class HandleClient extends Thread {

    Scanner input;
    PrintWriter writer;
    Socket socket;
    EchoServer echoS;

    public HandleClient(Socket socket, EchoServer echoServer) throws IOException {
        echoS = echoServer;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.socket = socket;
    }

    public void send(String msg) {
        writer.println(msg);
    }

    @Override
    public void run() {

        String message = input.nextLine(); //IMPORTANT blocking call
        String arr[] = message.split("#");
        String token = arr[0];
        if (!token.equals(ProtocolStrings.CONNECT) || arr.length != 2) {
            try {
                socket.close();
                echoS.removeHandler(this);
            } catch (IOException ex) {
                Logger.getLogger(HandleClient.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {

            echoS.addclient(arr[1], this);
            
            
            
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {
              echoS.send(message);
                writer.println(echoS.makeonlinemsg());
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
                message = input.nextLine(); //IMPORTANT blocking call
                writer.println(message);
            }
            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            try {
                socket.close();
                echoS.removeHandler(this);
            } catch (IOException ex) {
                Logger.getLogger(HandleClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
        }
    }

}
