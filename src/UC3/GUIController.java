package UC3;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;


public class GUIController implements Observer, WindowListener {

  private Client client;
  private boolean login = false;
  private ClientGUI gui;
  private String UserName = "@";
  private String str;
  private ImageIcon imageIcon = null;


  public GUIController(int port, String ip, Client client) {
    this.client = client;
    client.addObserver(this);
    gui = new ClientGUI(this);
    this.gui.setUserName(JOptionPane
        .showInputDialog("Enter your Username:" + "\n(Your Username cannot start with @"));
    if (gui.getUserName().isEmpty()) {
      this.gui.setUserName(JOptionPane
          .showInputDialog("Enter your Username:" + "\n(Your Username cannot start with @"));
    }
    client.login(gui.getUserName());
    gui.addWindowListener(this);


  }


  public void update(Observable o, Object obj) {

    String text;
    Icon icon;
    if (obj != null) {
      if (obj.equals("TACTICALDUCK!!!LOGINCHECK")) {
        login = true;
        if (login) {
          gui.getUserNamelbl().setText("    USER: " + gui.getUserName());
        }
      } else if (obj.equals("TACTICALDUCK!!!LOGINCHECKFAILED")) {
        login = false;
        this.UserName = JOptionPane.showInputDialog("UserName was taken or input was incorrect.\n"
            + "Enter your Username:" + "\n(Your Username cannot start with @");
        client.login(UserName);
      } else if (obj instanceof String) {
        gui.getTextArea().add(new JLabel((String) obj));
        gui.getTextArea().add(Box.createRigidArea(new Dimension(0, 3)));
        gui.revalidate();
        gui.getTextScrollbar().setValue(gui.getTextScrollbar().getMaximum());
      } else if (obj instanceof ArrayList<?>) {
        @SuppressWarnings("unchecked")
        ArrayList<String> userList = (ArrayList<String>) obj;
        gui.setUsersOnlinePanel(new JPanel());
        gui.getUsersOnlinePanel()
            .setLayout(new BoxLayout(gui.getUsersOnlinePanel(), BoxLayout.PAGE_AXIS));
        gui.getUsersOnlinePanel().setPreferredSize(new Dimension(75, 300));
        gui.getUsersOnlinePanel().revalidate();
        gui.getEastPanel().remove(gui.getEastScrollPane());
        gui.setEastScrollPane(new JScrollPane(gui.getUsersOnlinePanel()));
        gui.getEastScrollPane().revalidate();
        gui.getEastPanel().add(gui.getEastScrollPane());
        for (String ul : userList) {
          gui.getUsersOnlinePanel().add(new JLabel(ul));
          gui.getUsersOnlinePanel().add(Box.createRigidArea(new Dimension(0, 3)));
          gui.getEastScrollbar().setValue(gui.getEastScrollbar().getMaximum());
        }
        gui.revalidate();

      } else if (obj instanceof Message) {
        Message mess = (Message) obj;
        if (mess.getIcon() != null) {
          icon = mess.getIcon();
          gui.getTextArea().add(new JLabel(icon));
          gui.getTextArea().add(Box.createRigidArea(new Dimension(0, 3)));
          gui.revalidate();
          gui.getTextScrollbar().setValue(gui.getTextScrollbar().getMaximum());
        }
        if (!mess.getText().isEmpty()) {
          text = mess.getText();
          gui.getTextArea().add(new JLabel(text));
          gui.getTextArea().add(Box.createRigidArea(new Dimension(0, 3)));
          gui.revalidate();
          gui.getTextScrollbar().setValue(gui.getTextScrollbar().getMaximum());
        }
      } else if (obj instanceof NamedMessage) {
        JLabel icnlbl;
        NamedMessage mess = (NamedMessage) obj;
        if (mess.getIcon() != null) {
          icon = mess.getIcon();
          if (mess.getText().contains("Private")) {
            icnlbl = new JLabel(mess.getTime() + mess.getName() + "<Private>" + ">>");
          } else {
            icnlbl = new JLabel(mess.getTime() + mess.getName() + ">>");
          }
          icnlbl.setIcon(icon);
          icnlbl.setHorizontalTextPosition((SwingConstants.LEADING));
          gui.getTextArea().add(icnlbl);
          gui.getTextArea().add(Box.createRigidArea(new Dimension(0, 3)));
          gui.revalidate();
          gui.getTextScrollbar().setValue(gui.getTextScrollbar().getMaximum());
        }
        if (!mess.getText().isEmpty()) {
          text = mess.getText();
          gui.getTextArea().add(new JLabel(mess.getTime() + mess.getName() + ">> " + text));
          gui.getTextArea().add(Box.createRigidArea(new Dimension(0, 3)));
          gui.revalidate();
          gui.getTextScrollbar().setValue(gui.getTextScrollbar().getMaximum());
        }
      }


    }

  }


  public void diconnect() {

    try {
      client.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }


  public void send() {

    str = gui.getInputTextField().getText();
    imageIcon = gui.getImageIcon();
    if (imageIcon != null && !str.isEmpty()) {
      client.send(gui.getUserName(), str, imageIcon);
    } else if (imageIcon != null && str.isEmpty()) {
      client.send(gui.getUserName(), imageIcon);
    } else if (!str.isEmpty() && imageIcon == null) {
      client.send(gui.getUserName(), str);
    }
    gui.getInputTextField().selectAll(); // all text i "skriv"rutan
    gui.getInputTextField().setText("");
    gui.setImage();
    imageIcon = null;

  }


  @Override
  public void windowOpened(WindowEvent e) {

  }


  @Override
  public void windowClosing(WindowEvent e) {

    gui.dispose();

  }


  @Override
  public void windowClosed(WindowEvent e) {

    int confirm = 0;
    if (confirm == 0) {
      try {
        if (!client.isClosed()) {
          client.close();
        }
        gui.dispose();
      } catch (IOException e1) {
        e1.printStackTrace();
      }

    }

  }


  @Override
  public void windowIconified(WindowEvent e) {
  }


  @Override
  public void windowDeiconified(WindowEvent e) {

  }


  @Override
  public void windowActivated(WindowEvent e) {

  }


  @Override
  public void windowDeactivated(WindowEvent e) {

  }

}
