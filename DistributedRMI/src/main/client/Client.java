package main.client;

import java.io.IOException;
import inputport.nio.manager.listeners.SocketChannelConnectListener;
import main.rmi.RMIClientObj;
import main.rmi.RMISender;
import main.rmi.RegistryServer;
import stringProcessors.HalloweenCommandProcessor;

public interface Client extends 
		SocketChannelConnectListener, RegistryServer{
	public void connectToServer(String aServerHost, int aServerPort);
	public void initialize(String aServerHost, int aServerPort, String aRegistryHost, int aRegistryPort);
	public void initializeNIO(String aServerHost, int aServerPort);
	public void initializeRMI(String aRegistryHost, int aRegistryPort);
	public void setLocalProcessing(boolean newValue);
	public void setAtomicBroadcast(boolean b);
	public void runExperiment();
	public HalloweenCommandProcessor getCommandProcessor();
	public RMIClientObj getClientObj();
	public RMISender getRMISender();
	public void setRMISenderReceiver(boolean b);
	public boolean getBroadcastIPC();
	public void setIPCBroadcast(boolean b);
	public void setDelayTime(int t);
	public int getDelayTime();
}
