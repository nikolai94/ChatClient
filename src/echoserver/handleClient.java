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
public class handleClient extends Thread{
    
    Scanner input;
    PrintWriter writer;
    Socket socket;
    EchoServer echoS = new EchoServer();
    
    public handleClient(Socket socket) throws IOException {
    input = new Scanner(socket.getInputStream());
    writer = new PrintWriter(socket.getOutputStream(), true);
    this.socket = socket;
  }
    
    public void send(String msg)
    {
        writer.write(msg);
    }
    @Override
    public void run(){
    
String message = input.nextLine(); //IMPORTANT blocking call
    Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ",message));
    while (!message.equals(ProtocolStrings.STOP)) {
      writer.println(message.toUpperCase());
      Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ",message.toUpperCase()));
      message = input.nextLine(); //IMPORTANT blocking call
    }
    writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
        try {
            socket.close();
            echoS.removeHandler(this);
        } catch (IOException ex) {
            Logger.getLogger(handleClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
    }
    
    
}
