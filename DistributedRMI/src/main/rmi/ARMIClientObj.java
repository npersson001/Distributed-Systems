package main.rmi;

import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.rmi.RemoteException;

import inputport.nio.manager.NIOManagerFactory;
import main.client.Client;
import main.client.ClientSender;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class ARMIClientObj implements RMIClientObj{
	public Client client;
	public String message;
	//public boolean atomic; 
	//public boolean broadcast;
	//public boolean broadcastIPC;
	//public IPCMechanism ipc;

	public ARMIClientObj(Client c){
		client = c;
		//default to true
		//ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(true);
	}
	
	public boolean getBroadcastProperty(){
		return ASimulationParametersBeanFactory.getSingleton().isBroadcastBroadcastMode();
	}
	
	public void setBroadcastProperty(boolean b) {
		System.out.println("Setting Broadcast: " + b);
		ASimulationParametersBeanFactory.getSingleton().setBroadcastBroadcastMode(b);
	}
	
	public void receiveMessage(String message) {
		System.out.println("Received RMI: " + message);
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal received back
		//ASimulationParametersBeanFactory.getSingleton().simulationCommand(message);
		ASimulationParametersBeanFactory.getSingleton().acceptSimulationCommand(message); // set state
		client.getCommandProcessor().processCommand(message);
	}
//	
//	public void setIPCBroadcastProperty(boolean b) throws RemoteException{
//		broadcastIPC = b;
//	}
//	
//	public boolean getIPCBroadcastProperty() throws RemoteException{
//		return broadcastIPC;
//	}
	
	public void setIPCBroadcast(boolean b) {
		client.setIPCBroadcast(b);
	}
	
	public void setIPC(IPCMechanism newValue){
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newValue); // proposal received back
		ASimulationParametersBeanFactory.getSingleton().setIPCMechanism(newValue);
		if(ASimulationParametersBeanFactory.getSingleton().getIPCMechanism() == IPCMechanism.NIO) {
			client.setRMISenderReceiver(false);
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getIPCMechanism() == IPCMechanism.RMI) {
			client.setRMISenderReceiver(true);
		}
	}
	
//	public void setIPCProperty(IPCMechanism newValue) throws RemoteException{
//		ipc = newValue;
//	}
//	
//	public IPCMechanism getIPCProperty() throws RemoteException{
//		return ipc;
//	}
	
	public void setAtomic(boolean b) {
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, b); // proposal received back
		//ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(b);
		client.setAtomicBroadcast(b);
	}
	
	public void setAtomicBroadcast(boolean b){
		ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(b);
	}
	
	public boolean getAtomicBroadcast() {
		return ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast();
	}
	
//	public void setMessage(String mes) throws RemoteException{
//		message = mes;
//	}
//	
//	public String getMessage() throws RemoteException{
//		return message;
//	}
}
