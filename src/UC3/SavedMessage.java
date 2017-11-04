package UC3;

import java.io.Serializable;
import javax.swing.Icon;

public class SavedMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5809556742429233439L;
	private Icon icon = null;
	private String text = "";
	private String name = null;
	private NamedMessage msg = null;

	public SavedMessage(String name, String text) {
		this.name = name;
		this.text = text;
	}

	public SavedMessage(String name, Icon icon) {
		this.name = name;
		this.icon = icon;
	}

	public SavedMessage(String name, String text, Icon icon) {
		this.name = name;
		this.text = text;
		this.icon = icon;
	}

	public SavedMessage(String name, NamedMessage msg) {
		this.name = name;
		this.msg = msg;
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

	public NamedMessage getMsg() {
		return msg;
	}
}
