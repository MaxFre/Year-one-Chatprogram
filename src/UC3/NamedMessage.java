package UC3;

import java.io.Serializable;
import javax.swing.Icon;

public class NamedMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5809556742429233439L;
	private Icon icon = null;
	private String text = "";
	private String name = null;
	private Message msg = null;
	private String time = "";

	public NamedMessage(String time, String name, String text) {
		this.time = time;
		this.name = name;
		this.text = text;
	}

	public NamedMessage(String time, String name, Icon icon) {
		this.time = time;
		this.name = name;
		this.icon = icon;
	}

	public NamedMessage(String time, String name, String text, Icon icon) {
		this.time = time;
		this.name = name;
		this.text = text;
		this.icon = icon;
	}

	public NamedMessage(String time, String name, Message msg) {
		this.time = time;
		this.name = name;
		this.msg = msg;
		this.text = msg.getText();
		this.icon = msg.getIcon();
	}

	public Icon getIcon() {
		return icon;
	}

	public String getText() {
		return text;
	}

	public String getName() {
		return name;
	}

	public Message getMsg() {
		return msg;
	}
	
	public String getTime() {
		return time;
	}
}
