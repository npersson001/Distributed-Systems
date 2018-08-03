package main.gipc;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GIPCSender extends Remote{
	public void gipcMessage(GIPCClientObj gipcClient, String message) throws RemoteException;
	public void connect(GIPCClientObj client) throws RemoteException;
}
