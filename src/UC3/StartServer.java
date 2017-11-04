package UC3;

import java.io.IOException;

import javax.swing.JOptionPane;

public class StartServer {
	public static void main(String[] args) throws IOException {
		int port = 3040;
		try {
			port = Integer.parseInt(JOptionPane.showInputDialog("What port should the server listen on?(1-65536)"));
		} catch (NumberFormatException e) {
			port = Integer.parseInt(JOptionPane.showInputDialog("What port should the server listen on?(1-65536)"));
		}
		new NServer(port);
	}
}
