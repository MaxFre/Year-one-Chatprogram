package UC3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class ClientGUI extends JFrame implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 6477268062744939384L;
  private JPanel textArea;
  private JPanel usersOnlinePanel;
  private JPanel eastPanel;
  private JScrollPane textScrollPane;
  private JScrollPane eastScrollPane;
  private JScrollBar textScrollbar;
  private JScrollBar eastScrollbar;
  private JTextField inputTextField;
  private JButton btnBifoga = new JButton("Bifoga bild");
  private JButton btnSend = new JButton("Send");
  private JButton btnDisconnect = new JButton("Logga ut");
  private JLabel userNamelbl = new JLabel("    USER:");
  private JLabel onlineUserslbl = new JLabel(" Online Users:");
  private JLabel attachedFile = new JLabel("");
  private Box box = Box.createHorizontalBox();
  private JFileChooser chooser = new JFileChooser();
  private String UserName = "@";
  private File file;
  private Icon icon = null;
  private ImageIcon imageIcon = null;
  private GUIController guiController;


  public ClientGUI(GUIController guiController) {
    this.guiController = guiController;
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setPreferredSize(new Dimension(800, 400));
    pack();
    setLocationRelativeTo(null);
    setResizable(true);
    setVisible(true);
    buildGUI();

  }


  public void buildGUI() {

    setTextArea(new JPanel());
    getTextArea().setBackground(Color.WHITE);
    getTextArea().setLayout(new BoxLayout(getTextArea(), BoxLayout.PAGE_AXIS));
    setUsersOnlinePanel(new JPanel());
    getUsersOnlinePanel().setLayout(new BoxLayout(getUsersOnlinePanel(), BoxLayout.PAGE_AXIS));
    getUsersOnlinePanel().setPreferredSize(new Dimension(75, 300));
    onlineUserslbl.setPreferredSize(new Dimension(75, 30));
    getUserNamelbl().setPreferredSize(new Dimension(150, 30));
    pack();

    setEastPanel(new JPanel());
    setEastScrollPane(new JScrollPane(getUsersOnlinePanel()));
    textScrollPane = new JScrollPane(getTextArea());
    getEastPanel().setLayout(new BoxLayout(getEastPanel(), BoxLayout.PAGE_AXIS));
    getEastPanel().add(onlineUserslbl);
    getEastPanel().add(getEastScrollPane());
    setTextScrollbar(textScrollPane.getVerticalScrollBar());
    setEastScrollbar(getEastScrollPane().getVerticalScrollBar());

    add(textScrollPane, BorderLayout.CENTER);
    add(getEastPanel(), BorderLayout.EAST);
    add(attachedFile, BorderLayout.SOUTH);

    setInputTextField(new JTextField());
    box.add(getInputTextField());
    box.add(btnSend);
    box.add(btnBifoga);
    box.add(btnDisconnect);
    box.add(getUserNamelbl());
    add(box, BorderLayout.SOUTH);
    revalidate();

    btnBifoga.addActionListener(this);
    getInputTextField().addActionListener(this);
    btnSend.addActionListener(this);
    btnDisconnect.addActionListener(this);
  }


  public File bifoga() {

    FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & PNG Images", "jpg", "png");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showOpenDialog(null);

    if (returnVal == JFileChooser.APPROVE_OPTION) {
      System.out.println("You chose to open this file: " + chooser.getSelectedFile().getName());
      return chooser.getSelectedFile();
    }

    if (returnVal == JFileChooser.CANCEL_OPTION) {
      System.out.println("Cancelled");
    }
    return chooser.getCurrentDirectory();

  }


  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getSource() == btnDisconnect) {
      btnBifoga.setEnabled(false);
      btnSend.setEnabled(false);
      btnDisconnect.setEnabled(false);
      guiController.diconnect();
    } else if (e.getSource() == btnBifoga) {
      // Handle open button action.
      file = bifoga();
      if (file.getAbsolutePath() != null) {
        icon = new ImageIcon(file.getAbsolutePath());
        attachedFile.setText(file.getAbsolutePath());
        // resize ImageIcon
        Image image = ((ImageIcon) icon).getImage();
        Image newimg = image.getScaledInstance(120, 120, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);

      }
    } else if (e.getSource() == btnSend || e.getSource() == getInputTextField()) {
      guiController.send();
      attachedFile.setText("");
    }

  }

  public JPanel getUsersOnlinePanel() {

    return usersOnlinePanel;
  }


  public void setUsersOnlinePanel(JPanel usersOnlinePanel) {

    this.usersOnlinePanel = usersOnlinePanel;
  }


  public JLabel getUserNamelbl() {

    return userNamelbl;
  }


  public void setUserNamelbl(JLabel userNamelbl) {

    this.userNamelbl = userNamelbl;
  }


  public String getUserName() {

    return UserName;
  }


  public void setUserName(String userName) {

    UserName = userName;
  }


  public JScrollBar getTextScrollbar() {

    return textScrollbar;
  }


  public void setTextScrollbar(JScrollBar textScrollbar) {

    this.textScrollbar = textScrollbar;
  }


  public JPanel getEastPanel() {

    return eastPanel;
  }


  public void setEastPanel(JPanel eastPanel) {

    this.eastPanel = eastPanel;
  }


  public JScrollPane getEastScrollPane() {

    return eastScrollPane;
  }


  public void setEastScrollPane(JScrollPane eastScrollPane) {

    this.eastScrollPane = eastScrollPane;
  }


  public JScrollBar getEastScrollbar() {

    return eastScrollbar;
  }


  public void setEastScrollbar(JScrollBar eastScrollbar) {

    this.eastScrollbar = eastScrollbar;
  }


  public JPanel getTextArea() {

    return textArea;
  }


  public void setTextArea(JPanel textArea) {

    this.textArea = textArea;
  }


  public JTextField getInputTextField() {

    return inputTextField;
  }


  public void setInputTextField(JTextField inputTextField) {

    this.inputTextField = inputTextField;
  }


  public ImageIcon getImageIcon() {

    return imageIcon;

  }


  public void setImage() {

    this.imageIcon = null;
  }


}
