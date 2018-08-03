package main.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import util.interactiveMethodInvocation.IPCMechanism;

public interface RMIClientObj extends Remote{
	public void receiveMessage(String message) throws RemoteException;
	public boolean receiveMessageRequest(String message) throws RemoteException;
	
	public void setAtomic(boolean b) throws RemoteException;
	public boolean setAtomicRequest(boolean b) throws RemoteException;
	
	public void setIPC(IPCMechanism b) throws RemoteException;
	public void setIPCBroadcast(boolean b) throws RemoteException;
	public boolean setIPCRequest(IPCMechanism b) throws RemoteException;
	
	public boolean getBroadcastBroadcastMode() throws RemoteException;
	public void setBroadcastBroadcastMode(boolean b) throws RemoteException;
	
	//public void setAtomicBroadcast(boolean b) throws RemoteException;
	public boolean getAtomicBroadcast() throws RemoteException;
}
