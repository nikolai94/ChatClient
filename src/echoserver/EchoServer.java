package echoserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import shared.ProtocolStrings;
import utils.Utils;


public class EchoServer {

  private static boolean keepRunning = true;
  private static ServerSocket serverSocket;
  private static final Properties properties = Utils.initProperties("server.properties");
  private Map<String, HandleClient> clients = new HashMap();

  public static void stopServer() {
    keepRunning = false;
  }
  
  public void  removeHandler(String brugernavn)
  {
      clients.remove(brugernavn);  /// HUSK AT HENT KEyen.
      sendonlinemsg();
  
  }
  public void addclient(String navn, HandleClient hc)
  {
      clients.put(navn, hc);
      sendonlinemsg();
      
  }
  public void sendonlinemsg()
  {
      String msg = ProtocolStrings.Online + "#";
      int antal = 0 ;
      for (String username : clients.keySet()) {
           antal++;
           if(antal == clients.size())
           {
               msg += username;
           }
           else
           {
             msg += username + ",";
           }
  
      }
      for(HandleClient hc : clients.values())
      {
          hc.send(msg);
      }
      
  }
//  public void send(String msg)
//  { 
//      System.out.println("In EchoServer.send()");
//      for (String username : clients.keySet())
//      {
//         clients.get(username).send(msg);
//          //clients.get(hc).send(msg);
//      }
//  }
  public void send(String afsender, String modtager,String besked)
  {
      if(modtager.equals("*"))
      {
            for (String username : clients.keySet())
            {
                   clients.get(username).send(ProtocolStrings.MESSAGE+"#"+afsender+"#"+besked);
            }
      }
      else{
          String[] userNames = modtager.split(",");
          
          for (int i = 0; i < userNames.length; i++) {
            
                for (String username : clients.keySet())
                {
                    //System.out.println("names: "+userNames[i]);
                    //System.out.println("username: "+username);
                       if(userNames[i].equalsIgnoreCase(username)){
                            clients.get(username).send(ProtocolStrings.MESSAGE+"#"+afsender+"#"+besked);
                       }
                }
            
          }
      }
      
      
  }
  private void runServer()
  {
    int port = Integer.parseInt(properties.getProperty("port"));
    String ip = properties.getProperty("serverIp");
    
    Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started. Listening on: "+port+", bound to: "+ip);
   
    try {
      serverSocket = new ServerSocket();
      serverSocket.bind(new InetSocketAddress(ip, port));
      do {
        Socket socket = serverSocket.accept(); //Important Blocking call
        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");        
        HandleClient hc =  new HandleClient(socket, this);
      //  clients.add(hc);
        hc.start();
      } while (keepRunning);
    } 
    catch (IOException ex) {    
      Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void main(String[] args) {
      
     try{
       String logFile = properties.getProperty("logFile"); 
      Utils.setLogFile(logFile,EchoServer.class.getName());
          new EchoServer().runServer();
     
     } 
     finally{
      Utils.closeLogger(EchoServer.class.getName());
     
     } 
     
    
  }
}
