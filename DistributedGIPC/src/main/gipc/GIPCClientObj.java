package main.gipc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GIPCClientObj extends Remote{
	public void receiveMessage(String message) throws RemoteException;
	public boolean receiveMessageRequest(String message) throws RemoteException;
}
