package UC3;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import javax.swing.Icon;

public class Client extends Observable implements Runnable {
	private String ip;
	private int port;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private Object obj;
	private String userName;
	private boolean keepGoing = true;

	public Client(String ip, int port) throws UnknownHostException, IOException, InterruptedException {
		this.ip = ip;
		this.port = port;

		socket = new Socket(ip, port);
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
		run();

	}
	


	public void run() {
		try {
			new GUIController(port, ip, Client.this);

			while (keepGoing) {
				if (keepGoing) {
					obj = ois.readObject();
				} else {
					break;
				}
				if (obj != null) {
					if (obj instanceof ArrayList<?>) {
						System.out.println("Client received userList");
						setChanged();
						notifyObservers(obj);
					} else if (obj instanceof NamedMessage) {
						setChanged();
						notifyObservers(obj);

					} else if (obj instanceof Message) {
						Message mess = (Message) obj;
						if (mess.getText().equals("TACTICALDUCK!!!LOGINCHECKFAILED")) {
							setChanged();
							notifyObservers("TACTICALDUCK!!!LOGINCHECKFAILED");

						} else if (mess.getText().equals("TACTICALDUCK!!!LOGINCHECK")) {
							setChanged();
							notifyObservers("TACTICALDUCK!!!LOGINCHECK");
						} else if (mess.getText()
								.contains("WELCOMESEQUENCE!!!" + userName.substring(userName.indexOf(" ") + 1))) {
							setChanged();
							notifyObservers("Welcome " + (userName.substring(userName.indexOf(" ") + 1)) + "! "
									+ "\nTo leave enter 'Disconnect' in a new line.");
						} else if (mess.getText().startsWith(userName + "zzrotportal")) {
						} else {
							setChanged();
							notifyObservers(mess);
						}
					}
				}

			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			try {
				System.out.println("hello?");
				oos.writeObject(new Message("Disconnect"));
				oos.writeObject(new Message("Disconnect"));
				oos.writeObject(new Message("Disconnect"));
				oos.flush();
			} catch (IOException e1) {
			}

		}
	}

	public void login(String userName) {
		this.userName = userName;
		try {
			oos.writeObject(new Message("login " + userName));
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void AnnounceLogin(String userName) {
		this.userName = userName;
		try {
			oos.writeObject(new Message(
					userName + "zzrotportal" + "*** A new user " + userName + " entered the chat room !!! ***"));
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String userName, String text) {
		this.userName = userName;
		Date date = new Date();
		String time = date.toString();
		String[] splitTime = time.split(" ");
		time = splitTime[3] + " - " ;
		try {
			oos.writeObject(new NamedMessage(time, userName, text));
			oos.flush();
		} catch (IOException ex) {
			setChanged();
			notifyObservers(ex);
		}
	}

	public void send(String userName, Icon icon) {
		this.userName = userName;
		Date date = new Date();
		String time = date.toString();
		String[] splitTime = time.split(" ");
		time = splitTime[3] + " - " ;
		try {
			oos.writeObject(new NamedMessage(time, userName, icon));
			oos.flush();
		} catch (IOException ex) {
			setChanged();
			notifyObservers(ex);
		}
	}

	public void send(String userName, String text, Icon icon) {
		this.userName = userName;
		Date date = new Date();
		String time = date.toString();
		String[] splitTime = time.split(" ");
		time = splitTime[3] + " - " ;
		try {
			oos.writeObject(new NamedMessage(time, userName, text, icon));
			oos.flush();
		} catch (IOException ex) {
			setChanged();
			notifyObservers(ex);
		}
	}

	public void close() throws IOException {
		keepGoing = false;
		oos.writeObject(new Message("Disconnect"));
		oos.flush();
	}

	public boolean isClosed() {
		if (keepGoing == false) {
			return true;
		} else
			return false;
	}

}
