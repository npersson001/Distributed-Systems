package main.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import util.interactiveMethodInvocation.IPCMechanism;

public interface RMISender extends Remote {
	public void rmiMessage(RMIClientObj client, String message) throws RemoteException;
	public void connect(RMIClientObj r) throws RemoteException;
	//public void setAtomic(boolean b) throws RemoteException;
	public void setBroadcastMode(boolean b) throws RemoteException;
	public void setIPCBroadcast(boolean b) throws RemoteException;
	public void setIPC(IPCMechanism newVal) throws RemoteException;
	public void setBroadcast(boolean b) throws RemoteException;
}
