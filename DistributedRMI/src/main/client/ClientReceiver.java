package main.client;

import inputport.nio.manager.listeners.SocketChannelReadListener;

public interface ClientReceiver extends SocketChannelReadListener {

	void setRecMessages(boolean b);
	public boolean getRecMessages();
	void setLocalExec(boolean b);
	boolean getLocalExec();
	void setRMI(boolean b);

}
