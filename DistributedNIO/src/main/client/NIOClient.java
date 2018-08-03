package main.client;

import java.io.IOException;
import inputport.nio.manager.listeners.SocketChannelConnectListener;
import stringProcessors.HalloweenCommandProcessor;

public interface NIOClient extends 
		SocketChannelConnectListener{
	public void connectToServer(String aServerHost, int aServerPort);
	public void initialize(String aServerHost, int aServerPort);
	public void setLocalProcessing(boolean newValue);
	public void setAtomicBroadcast(boolean b);
	public void runExperiment();
	public HalloweenCommandProcessor getCommandProcessor();

}
