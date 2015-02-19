package echoclient;

import echoserver.EchoServer;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;

public class EchoClient extends Thread implements EchoListener
{
    
  Socket socket;
  private int port;
  private InetAddress serverAddress;
  private Scanner input;
  private PrintWriter output;
  List<EchoListener> listeners;
  
  public void connect(String address, int port, String navn) throws UnknownHostException, IOException
  {
      
    this.port = port;
    serverAddress = InetAddress.getByName(address);
    socket = new Socket(serverAddress, port);
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    listeners = new ArrayList();
      send("Online "+ navn);
  }
  
  public void registerEchoListener(EchoListener l){
  listeners.add(l);
  }
  
  public void unRegisterEchoListener(EchoListener l){
  listeners.remove(l);
  }
  
  public void notifyListeners(String msg){
  
      for (EchoListener listener : listeners) {
          listener.messageArrived(msg);
      }
  }
  
  
  public void send(String msg)
  {
    output.println(msg);
  }
  
  
  public void stopNew() throws IOException{
    output.println(ProtocolStrings.STOP);
  }
  
  /*public String receive()
  {
    String msg = input.nextLine();
    if(msg.equals(ProtocolStrings.STOP)){
      try {
        socket.close();
      } catch (IOException ex) {
        Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return msg;
  }*/
  
  
  public void run() {
    String msg = input.nextLine();
    while (!msg.equals(ProtocolStrings.STOP)) {
    notifyListeners(msg);
    msg = input.nextLine();
    }
    try {
    socket.close();
    } catch (IOException ex) {
    Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
    }
 }  
  
//  public static void main(String[] args)
//  {   
//    int port = 9090;
//    String ip = "localhost";
//    if(args.length == 2){
//      port = Integer.parseInt(args[0]);
//      ip = args[1];
//    }
//    try {
//      EchoClient tester = new EchoClient();
//
//      tester.connect(ip, port);
//      tester.registerEchoListener(tester);
//
//      System.out.println("Sending 'Hello world'");
//        tester.start();
//      tester.send("Hello World");
//      System.out.println("Waiting for a reply");
//
//      tester.stopNew();      
//      //System.in.read();      
//    } catch (UnknownHostException ex) {
//      Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
//    } catch (IOException ex) {
//      Logger.getLogger(EchoClient.class.getName()).log(Level.SEVERE, null, ex);
//    }
//  }
//

    @Override
    public void messageArrived(String data) {
        //System.out.println(data);
   
    }

   
    
    
    
    
}
