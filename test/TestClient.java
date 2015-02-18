import echoclient.EchoClient;
import echoclient.EchoListener;
import echoserver.EchoServer;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestClient {
  
  public TestClient() {
  }
  
  @BeforeClass
  public static void setUpClass() {
    new Thread(new Runnable(){
      @Override
      public void run() {
        EchoServer.main(null);
          System.out.println("TEST IT STARTING THE SERVER");
          EchoServer.main(null);
      }
    }).start();
  }
  
  @AfterClass
  public static void tearDownClass() {
      System.out.println("TEST is stopping the server");
    EchoServer.stopServer(); // EchoServer.stop()
  }
  private CountDownLatch lock;
  private String tesresult;
  public void sendMessage() throws IOException,InterruptedException{
      lock = new CountDownLatch(1);
      tesresult = "";
      EchoClient tester = new EchoClient();
      tester.connect("localhost", 9090);
      tester.registerEchoListener(new EchoListener() {

          @Override
          public void messageArrived(String data) {
              tesresult = data;
              lock.countDown();
          }
      });
      tester.send("Hello");
      lock.await(1000,TimeUnit.MILLISECONDS);
      assertEquals("HELLO", tesresult);
      tester.stopNew();
  }
  
  @Before
  public void setUp() {
  }
  
  @Test
  public void send() throws IOException{
 
  }
  
}
