package main.client;

import java.beans.PropertyChangeListener;
import java.rmi.registry.Registry;

import main.rmi.RMIClientObj;
import main.rmi.RMISender;

public interface ClientSender extends PropertyChangeListener{

	void setSendMessages(boolean b);
	boolean getSendMessages();
	void setLocalExec(boolean b);
	boolean getLocalExec();
	public void setRMI(boolean b);
	public void setupRMI(RMISender s, RMIClientObj c);
}
