package main.client;

import java.beans.PropertyChangeListener;
import java.rmi.registry.Registry;

import main.gipc.GIPCClientObj;
import main.gipc.GIPCSender;
import main.rmi.RMIClientObj;
import main.rmi.RMISender;

public interface ClientSender extends PropertyChangeListener{

	void setSendMessages(boolean b);
	boolean getSendMessages();
	void setLocalExec(boolean b);
	boolean getLocalExec();
	public void setupRMI(RMISender rmiSender, RMIClientObj clientObj);
	public void setupGIPC(GIPCSender aGIPCSender, GIPCClientObj aGIPCClientObj);
}
