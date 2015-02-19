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
    String brugernavn;

    public HandleClient(Socket socket, EchoServer echoServer) throws IOException {
        echoS = echoServer;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.socket = socket;
    }

    public synchronized void send(String msg) {
        writer.println(msg);
    }
    public void split(String msg)
    {
        String[] beskeder = msg.split("#");
        System.out.println(" beskeden "+msg);
        String token = beskeder[0];
        System.out.println(token +" skal være == send");
        if(token.equals(ProtocolStrings.CONNECT))
        {
            echoS.addclient(beskeder[1], this);
            brugernavn = beskeder[1];
        }
        else if (token.equals(ProtocolStrings.SEND))
        {
            System.out.println("else");
            echoS.send(brugernavn, beskeder[1], beskeder[2]);
            
        }
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
            split(message);
           
            while (!message.equals(ProtocolStrings.STOP)) {
              
                message = input.nextLine(); //IMPORTANT blocking call
                split(message);
            }
          
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
