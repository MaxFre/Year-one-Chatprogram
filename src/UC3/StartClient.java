package UC3;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;


public class StartClient {

  public static void main(String[] args)
      throws UnknownHostException, IOException, InterruptedException {

    // send Server ip adress
     String IPServer = JOptionPane.showInputDialog("Enter the Server ip adress");
    int port = 3040;
     try {
     IPServer = JOptionPane.showInputDialog("IP of the server you wish to connect to");
     } catch (NumberFormatException e) {
     IPServer = JOptionPane.showInputDialog("IP of the server you wish to connect to");
     }
     try {
     port = Integer.parseInt(JOptionPane.showInputDialog("What port is the server listening on?"));
     } catch (NumberFormatException e) {
     port = Integer.parseInt(JOptionPane.showInputDialog("What port is the server listening on?"));
     }
    new Client(IPServer, port);
  }
}
