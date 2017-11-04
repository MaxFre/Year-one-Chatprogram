package UC3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import UC3.NServer.Connection;


public class ClientThread extends Thread {

  // permvariables
  private String name;
  private Connection server;
  private Socket clientSocket;
  private boolean login = false;
  private ObjectOutputStream oos;
  private ObjectInputStream ois;
  private ArrayList<ClientThread> threads;
  private ArrayList<String> uslist = new ArrayList<String>();
  private boolean keepRunning = true;
  private final static Logger clientRequestLog = Logger.getLogger("requests");
  private final static Logger clientErrorLog = Logger.getLogger("errors");
  private FileHandler clientRequestFile = new FileHandler("./files/clientRequestLog.log");
  private FileHandler clientErrorFile = new FileHandler("./files/clientErrorLog.log");


  public ClientThread(Socket clientSocket, ArrayList<ClientThread> threads2, Connection connection)
      throws IOException {
    this.server = connection;
    this.threads = threads2;
    this.clientSocket = clientSocket;

    clientRequestFile.setFormatter(new SimpleFormatter()); // xml default
    clientRequestLog.setUseParentHandlers(false); // not in console
    clientRequestLog.addHandler(clientRequestFile); // log to file
    clientErrorLog.addHandler(clientErrorFile); // log to file (and console)

    start();
  }


  public void run() {

    try {
      oos = new ObjectOutputStream(clientSocket.getOutputStream());
      ois = new ObjectInputStream(clientSocket.getInputStream());
      /* Try to log in */
      while (!login) {
        if (!clientSocket.isClosed()) {
          Object testobject = ois.readObject();
          if (testobject instanceof Message) {
            Message mess = (Message) testobject;
            if (mess.getText().indexOf('@') == -1 && !mess.getText().isEmpty()) {
              if (mess.getText().startsWith("login")) {
                name = mess.getText().substring(mess.getText().indexOf(' ') + 1);
              }

              if (!server.checkUnique(name)) {
                server.addUniqueUser(name);
                login = true;
                oos.writeObject(new Message("TACTICALDUCK!!!LOGINCHECK"));
                oos.flush();
              } else {
                oos.writeObject(new Message("TACTICALDUCK!!!LOGINCHECKFAILED"));
                oos.flush();
              }
            } else {
              oos.writeObject(new Message("TACTICALDUCK!!!LOGINCHECKFAILED"));
              oos.flush();
            }
          }
        }
      }

      /* Welcome the new the client. */
      oos.writeObject(new Message("WELCOMESEQUENCE!!!" + name));
      oos.flush();

      for (ClientThread ct : threads) {
        if (ct != null && ct != this && !ct.clientSocket.isClosed()) {
          ct.oos.writeObject(
              new Message("*** A new user " + name + " entered the chat room !!! ***"));
          ct.oos.flush();
        }
      }
      for (ClientThread ct : threads) {
        if (ct != null && !ct.clientSocket.isClosed()) {
          uslist = server.getUserList();

          ct.oos.writeObject(uslist);
          ct.oos.flush();
        }
      }

      /* write offlineMessages */
      ArrayList<NamedMessage> offlineMessages = server.checkSavedMessages(name);
      if (offlineMessages != null && offlineMessages.size() > 0 && !clientSocket.isClosed()) {
        oos.writeObject(new Message("You received some messages while offline:"));
        oos.flush();
        for (NamedMessage sm : offlineMessages) {
          oos.writeObject(sm);
          oos.flush();
        }
        oos.writeObject(new Message("End of OfflineMessages"));
        oos.flush();
      }

      /* While active */
      while (keepRunning) {
        try {
          Object obj = ois.readObject();
          Date date = new Date();
          String time = date.toString();
          String[] splitTime = time.split(" ");
          time = splitTime[3] + " - ";
          synchronized (this) {
            for (ClientThread ct : threads) {
              if (obj != null) {
                if (ct != null && ct.name != null) {
                  if (obj instanceof NamedMessage) {
                    if (((NamedMessage) obj).getIcon() == null) {
                      clientRequestLog.info(name + ">> " + ((NamedMessage) obj).getText());
                    } else if ((!((NamedMessage) obj).getText().isEmpty())
                        && ((NamedMessage) obj).getIcon() != null) {
                      clientRequestLog.info(name + ">> " + ((NamedMessage) obj).getText());
                      clientRequestLog.info(name + ">> " + "Info about ImageIcon: "
                          + ((NamedMessage) obj).getIcon().toString());
                    } else if (((NamedMessage) obj).getText().isEmpty()) {
                      clientRequestLog.info(name + ">> " + "Info about ImageIcon: "
                          + ((NamedMessage) obj).getIcon().toString());
                    }

                    if (((NamedMessage) obj).getText() != null) {
                      if (((NamedMessage) obj).getText().startsWith("Disconnect")) {
                        for (ClientThread ct3 : threads) {
                          if (ct3 != null && ct3 != this && !ct3.clientSocket.isClosed()) {
                            ct3.oos.writeObject(
                                new NamedMessage(time, "System", "User:" + name + " logged out"));
                          }
                        }
                        keepRunning = false;
                        break;

                      } else if (((NamedMessage) obj).getText().startsWith("@")) {
                        String line = ((NamedMessage) obj).getText();
                        String[] words2 = line.split("\\s", 2);
                        words2[0] = words2[0].substring(1);
                        String[] words = new String[2];
                        words[0] = words2[0];
                        if (words2.length < 2) {
                          words[1] = name + " skickade en bild till dig och endast dig";
                        } else {
                          words[1] = words2[1];
                        }
                        if (words.length > 1 && words[1] != null || !words[0].isEmpty()) {
                          if (ct != null && ct != this && ct.name.equals(words[0])
                              && !ct.clientSocket.isClosed()) {
                            System.out.println(name + "Sent a message to: " + words[0]);
                            /*
                             * // Insert ifOnlinecheck else save to // file to send when
                             * reconnected.
                             * 
                             */
                            ct.oos.writeObject(
                                new NamedMessage(time, "", "<Private>" + name + ">>" + words[1],
                                    ((NamedMessage) obj).getIcon()));
                            ct.oos.flush();
                            /*
                             * Echo this message to let the client know the private message was
                             * sent.
                             */
                            oos.writeObject(new NamedMessage(time, "",
                                "<Private>" + name + ">> " + words[0] + ">" + words[1],
                                ((NamedMessage) obj).getIcon()));
                            oos.flush();
                            break;

                          } else if (server.checkOffline(words[0]) && ct == this) {
                            server.saveMessages(words[0],
                                new NamedMessage("Received at: " + time, "",
                                    "<Private>" + name + ">>" + words[1],
                                    ((NamedMessage) obj).getIcon()));
                          }
                        }
                      } else {
                        if (ct != null && !ct.clientSocket.isClosed()) {
                          ct.oos.writeObject(obj);
                          ct.oos.flush();
                        }
                      }
                    }

                  } else if (obj instanceof Message) {
                    if (((Message) obj).getText() != null) {
                      if (((Message) obj).getText().startsWith("Disconnect")) {
                        for (ClientThread ct3 : threads) {
                          if (ct3 != null && ct3 != this && !ct3.clientSocket.isClosed()) {
                            ct3.oos.writeObject(
                                new NamedMessage(time, "System", "User:" + name + " logged out"));
                          }
                        }
                        keepRunning = false;
                        break;

                      } else if (((Message) obj).getText().startsWith("@")) {
                        String line = ((Message) obj).getText();
                        String[] words = line.split("\\s", 2);
                        if (words.length > 1 && words[1] != null) {
                          words[1] = words[1].trim();
                          for (ClientThread ct2 : threads) {
                            if (ct2 != null && ct2 != this && ct2.name != null
                                && ct2.name.equals(words[0]) && !ct2.clientSocket.isClosed()) {
                              /*
                               * // Insert ifOnlinecheck else save to // file to send when
                               * reconnected.
                               * 
                               */
                              ct2.oos.writeObject(
                                  new NamedMessage(time, name, "<Private>" + name + ">>" + words[1],
                                      ((NamedMessage) obj).getIcon()));
                              ct2.oos.flush();
                              /*
                               * Echo this message to let the client know the private message was
                               * sent.
                               */
                              ct.oos.writeObject(new NamedMessage(time, name,
                                  "<Private>" + name + ">> " + words[1],
                                  ((NamedMessage) obj).getIcon()));
                              ct.oos.flush();
                              break;
                            } else if (server.checkOffline(words[0]) && ct == this) {
                              server.saveMessages(words[0],
                                  new NamedMessage("Received at: " + time, "",
                                      "<Private>" + name + ">>" + words[1],
                                      ((NamedMessage) obj).getIcon()));
                            }
                          }
                        }
                      } else {
                        ct.oos.writeObject(obj);
                        ct.oos.flush();
                      }

                    }
                  }
                }

              }
            }
          }
        } finally {

        }

      }
      // remove user from OnlineList
      synchronized (this) {
        server.removeOnlineUser(name);
        uslist = new ArrayList<String>();
        for (ClientThread ct : threads) {
          if (ct != null && ct.name != null && ct.name != name) {
            uslist.add(ct.name);
          }
        }

        for (ClientThread ct : threads) {
          if (ct != null && !ct.clientSocket.isClosed()) {
            try {
              ct.oos.writeObject(uslist);
              ct.oos.flush();
            } catch (IOException e) {
              e.printStackTrace();
            }

          }
        }
      }

    } catch (

    IOException e) {
      e.printStackTrace();
      clientErrorLog.severe(e.toString());
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      clientErrorLog.severe(e.toString());
    } finally {
      /*
       * Close the output stream, close the input stream, close the socket.
       */
      try {
        keepRunning = false;
        oos.close();
        ois.close();
        clientSocket.close();
        for (ClientThread ct : threads) {
          if (ct == this) {
            ct = null;
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }
}
