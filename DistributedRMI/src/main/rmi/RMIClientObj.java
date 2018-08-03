package main.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import util.interactiveMethodInvocation.IPCMechanism;

public interface RMIClientObj extends Remote{
	public void receiveMessage(String message) throws RemoteException;
//	public void setMessage(String mes) throws RemoteException;
//	public String getMessage() throws RemoteException;
	
	public void setAtomicBroadcast(boolean b) throws RemoteException;
	public boolean getAtomicBroadcast() throws RemoteException;
	public void setAtomic(boolean b) throws RemoteException;
	
//	public void setIPCProperty(IPCMechanism b) throws RemoteException;
	public void setIPC(IPCMechanism b) throws RemoteException;
//	public IPCMechanism getIPCProperty() throws RemoteException;
	
//	public void setIPCBroadcastProperty(boolean b) throws RemoteException;
//	public boolean getIPCBroadcastProperty() throws RemoteException;
	public void setIPCBroadcast(boolean b) throws RemoteException;
	
	//public void setBroadcast(boolean b) throws RemoteException;
	public boolean getBroadcastProperty() throws RemoteException;
	public void setBroadcastProperty(boolean b) throws RemoteException;
}
