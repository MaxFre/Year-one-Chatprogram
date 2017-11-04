package UC3;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class NServer {

  private ServerSocket serverSocket = null; // The server socket.
  private Socket clientSocket = null; // The client socket.
  private ArrayList<String> onlineUsers = new ArrayList<String>();
  private ArrayList<String> users = new ArrayList<String>();
  private ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
  private ArrayList<SavedMessage> msgs = new ArrayList<SavedMessage>();
  private Connection connection;
  private final static Logger requestLog = Logger.getLogger("requests");
  private final static Logger errorLog = Logger.getLogger("errors");
  private FileHandler requestFile = new FileHandler("./files/requestLog.log");
  private FileHandler errorFile = new FileHandler("./files/errorLog.log");


  public NServer(int port) throws IOException {

    requestFile.setFormatter(new SimpleFormatter()); // xml default
    requestLog.setUseParentHandlers(false); // not in console
    requestLog.addHandler(requestFile); // log to file
    errorLog.addHandler(errorFile); // log to file (and console)

    connection = new Connection(port);
    connection.start();
  }

  public class Connection extends Thread {

    private Connection(int port) {
      try {
        serverSocket = new ServerSocket(port);
        InetAddress ip = InetAddress.getLocalHost();
        System.out.println("Port number=" + port + "/n" + "IP= " + ip);
        requestLog.info(serverSocket + " Server running");
      } catch (IOException e) {
        System.out.println(e);
        errorLog.severe(e.toString());
      }
    }


    public void addUniqueUser(String name) {

      boolean userExists = false;
      for (String al : users) {
        if (al.equals(name)) {
          userExists = true;
          System.out.println("Checking if " + name + " userExists: " + userExists);
        }
      }
      if (userExists) {
        onlineUsers.add(name);
      } else {
        users.add(name);
        onlineUsers.add(name);
      }

    }


    public boolean checkUnique(String name) {

      boolean ifUnique = false;
      for (String al : onlineUsers) {
        if (al.equals(name)) {
          ifUnique = true;
        }
      }
      return ifUnique;
    }


    public Boolean checkOffline(String name) {

      boolean isOnline = false;
      for (String oul : onlineUsers) {
        if (oul.equals(name)) {
          isOnline = true;
        }
      }
      for (String ul : users) {
        if (ul.equals(name) && !isOnline) {
          return true;
        }
      }
      return false;
    }


    public synchronized ArrayList<String> getUserList() {

      ArrayList<String> uslist = new ArrayList<String>();
      for (String us : onlineUsers) {
        uslist.add(us);
      }
      return onlineUsers;
    }


    public synchronized void removeOnlineUser(String name) {

      for (Iterator<String> oul = onlineUsers.iterator(); oul.hasNext();) {
        String user = oul.next();
        if (user.equals(name)) {
          oul.remove();
        }

      }
    }


    public void saveMessages(String name, NamedMessage namedMessage) {

      msgs.add(new SavedMessage(name, namedMessage));
    }


    public ArrayList<NamedMessage> checkSavedMessages(String name) throws IOException {

      ArrayList<NamedMessage> savedMessages = new ArrayList<NamedMessage>();
      for (Iterator<SavedMessage> smsg = msgs.iterator(); smsg.hasNext();) {
        SavedMessage smess = smsg.next();
        if (smess.getName().equals(name)) {
          savedMessages.add(smess.getMsg());
          smsg.remove();
        }
      }
      return savedMessages;

    }


    public void run() {

      // Create a client socket for each connection and pass it to a new
      // client thread.

      while (true) {
        try {
          clientSocket = serverSocket.accept();

          if (threads.size() < 200) {
            threads.add(new ClientThread(clientSocket, threads, connection));
            requestLog.info("New clientThread " + clientSocket);
          }
          if (threads.size() >= 200) {
            clientSocket.close();
            errorLog.warning("Number of maximum clients reached");
          }

          ArrayList<ClientThread> toRemove = new ArrayList<ClientThread>();
          for (Iterator<ClientThread> ctr = threads.iterator(); ctr.hasNext();) {
            ClientThread ct = ctr.next();
            if (ct == null) {
              toRemove.add(ct);

            }
          }
          threads.removeAll(toRemove);
        } catch (IOException e) {
          System.out.println(e);
          errorLog.severe(e.toString());
        }
      }

    }

  }

}
