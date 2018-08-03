package main.server;

import assignments.util.mainArgs.ServerPort;
import inputport.nio.manager.listeners.SocketChannelAcceptListener;
import inputport.nio.manager.listeners.SocketChannelReadListener;
import inputport.nio.manager.listeners.WriteBoundedBufferListener;
import main.rmi.RegistryServer;


public interface Server extends ServerPort, SocketChannelAcceptListener, RegistryServer {
	//public boolean atomicBroadcast = true;
	
	public void initializeNIO(int aServerPort);
	public void initializeRMI(int aRegistryPort, String aRegistryHost);
	public void initialize(int aServerPort, int aRegistryPort, String aRegistryHost);

	public void setAtomicBroadcast(boolean newValue);
	
	public boolean getAtomicBroadcast();
	
	public void setDelayTime(int t);
	public int getDelayTime();
}
