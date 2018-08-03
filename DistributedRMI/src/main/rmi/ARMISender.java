package main.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import main.rmi.RMIClientObj;
import main.server.Server;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class ARMISender implements RMISender {
	public List<RMIClientObj> clients = new ArrayList<>();
	//public boolean atomic = true;
	public Server server;
	
	public ARMISender(Server s) {
		server = s;
		ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(true);
	}

	public void rmiMessage(RMIClientObj client, String message){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal received
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message);
		try {
			// send to all clients for atomic broadcast
			if(ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast()
					&& client.getAtomicBroadcast()){
				for(RMIClientObj c : clients){
					ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
					util.misc.ThreadSupport.sleep(server.getDelayTime());
					c.receiveMessage(message);
				}
			} 
			// send to all clients except sending client for non-atomic broadcast
			else { 
				for(RMIClientObj c : clients){
					if(!c.equals(client)) {
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
						util.misc.ThreadSupport.sleep(server.getDelayTime());
						c.receiveMessage(message);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void setBroadcastMode(boolean newVal){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal);
		server.setAtomicBroadcast(newVal);
		for(RMIClientObj c : clients){
			ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal); // proposal sent back
			util.misc.ThreadSupport.sleep(server.getDelayTime());
			try {
				c.setAtomic(newVal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setBroadcast(boolean newVal){
		for(RMIClientObj c : clients) {
			util.misc.ThreadSupport.sleep(server.getDelayTime());
			try {
				c.setBroadcastProperty(newVal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setIPC(IPCMechanism newVal){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal); // proposal received
		for(RMIClientObj c : clients){
			ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal); // proposal sent back
			util.misc.ThreadSupport.sleep(server.getDelayTime());
			try {
				c.setIPC(newVal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setIPCBroadcast(boolean newVal){
		for(RMIClientObj c : clients){
			util.misc.ThreadSupport.sleep(server.getDelayTime());
			try {
				c.setIPCBroadcast(newVal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	public void connect(RMIClientObj client){
		clients.add(client);
	}
}
