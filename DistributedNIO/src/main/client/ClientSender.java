package main.client;

import java.beans.PropertyChangeListener;

public interface ClientSender extends PropertyChangeListener{

	void setSendMessages(boolean b);
	boolean getSendMessages();
	void setLocalExec(boolean b);
	boolean getLocalExec();

}
