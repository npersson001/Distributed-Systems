package main.server;

import assignments.util.mainArgs.ServerPort;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.WriteBoundedBufferListener;


public interface NIOServer extends ServerPort, SocketChannelAcceptListener {
	//public boolean atomicBroadcast = true;
	
	public void initialize(int aServerPort);

	public void setAtomicBroadcast(boolean newValue);
	
	public boolean getAtomicBroadcast();
}
