package main.rmi;

import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.rmi.Remote;
import java.rmi.RemoteException;

import consensus.ProposalFeedbackKind;
import inputport.nio.manager.NIOManagerFactory;
import main.client.Client;
import main.client.ClientSender;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ProposalAcceptRequestReceived;
import util.trace.port.consensus.ProposalAcceptedNotificationSent;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class ARMIClientObj implements RMIClientObj{
	public Client client;
	public String message;

	public ARMIClientObj(Client c){
		client = c;
	}
	
	public boolean getBroadcastBroadcastMode(){
		return ASimulationParametersBeanFactory.getSingleton().isBroadcastBroadcastMode();
	}
	
	public void setBroadcastBroadcastMode(boolean b) {
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
	
	public boolean receiveMessageRequest(String message) {
		ProposalAcceptRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message);
		ASimulationParametersBeanFactory.getSingleton().setCommand(null);
		ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message, ProposalFeedbackKind.SUCCESS);
		return true;
	}
	
	public void setIPCBroadcast(boolean b) {
		client.setIPCBroadcast(b);
	}
	
	public void setIPC(IPCMechanism newValue){
		System.out.println("IPC set to: " + newValue);
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newValue); // proposal received back
		ASimulationParametersBeanFactory.getSingleton().setIPCMechanism(newValue); // proposal state set
		ASimulationParametersBeanFactory.getSingleton().setWaitForIPCMechanismConsensus(false);
	}
	
	public boolean setIPCRequest(IPCMechanism newValue) {
		ProposalAcceptRequestReceived.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, message);
		if(!ASimulationParametersBeanFactory.getSingleton().getReject()) {
			ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, message, ProposalFeedbackKind.SUCCESS);
			ASimulationParametersBeanFactory.getSingleton().setWaitForIPCMechanismConsensus(true);
			return true;
		}
		else {
			ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, message, ProposalFeedbackKind.SERVICE_DENIAL);
			ASimulationParametersBeanFactory.getSingleton().setWaitForIPCMechanismConsensus(true);
			return false;
		}
	}
	
	public void setAtomic(boolean b) {
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, b); // proposal received back
		client.setAtomicBroadcast(b);
		ASimulationParametersBeanFactory.getSingleton().setWaitForBroadcastConsensus(false);
	}
	
	public boolean setAtomicRequest(boolean b) {
		ProposalAcceptRequestReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, message);
		if(!ASimulationParametersBeanFactory.getSingleton().getReject()) {
			ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, message, ProposalFeedbackKind.SUCCESS);
			ASimulationParametersBeanFactory.getSingleton().setWaitForBroadcastConsensus(true);
			return true;
		}
		else {
			ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, message, ProposalFeedbackKind.SERVICE_DENIAL);
			ASimulationParametersBeanFactory.getSingleton().setWaitForBroadcastConsensus(true);
			return false;
		}
		
	}
	
//	public void setAtomicBroadcast(boolean b){
//		ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(b);
//	}
	
	public boolean getAtomicBroadcast() {
		return ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast();
	}
}
