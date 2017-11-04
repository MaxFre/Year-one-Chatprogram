package UC3;

import java.io.Serializable;

import javax.swing.Icon;

public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7760495750667887068L;
	Icon icon = null;
	String text = "";

	public Message(String text) {
		this.text = text;
	}

	public Message(Icon icon) {
		this.icon = icon;
	}

	public Message(String text, Icon icon) {
		this.text = text;
		this.icon = icon;
	}

	public Icon getIcon() {
		return icon;
	}

	public String getText() {
		return text;
	}
	
	

}
