package main.gipc;

import consensus.ProposalFeedbackKind;
import main.client.Client;
import main.rmi.ASimulationParametersBeanFactory;
import util.trace.port.consensus.ProposalAcceptRequestReceived;
import util.trace.port.consensus.ProposalAcceptedNotificationSent;
import util.trace.port.consensus.ProposalLearnedNotificationReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class AGIPCClientObj implements GIPCClientObj{
	Client client;
	
	public AGIPCClientObj(Client c) {
		client = c;
	}
	
	public void receiveMessage(String message) {
		System.out.println("Received GIPC: " + message);
		ProposalLearnedNotificationReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal received back
		ASimulationParametersBeanFactory.getSingleton().acceptSimulationCommand(message); // set state
		client.getCommandProcessor().processCommand(message);
	}
	
	public boolean receiveMessageRequest(String message) {
		ProposalAcceptRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message);
		ASimulationParametersBeanFactory.getSingleton().setCommand(null);
		
		if(!ASimulationParametersBeanFactory.getSingleton().getReject()) {
			ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message, ProposalFeedbackKind.SUCCESS);
			return true;
		}
		else {
			ProposalAcceptedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message, ProposalFeedbackKind.SERVICE_DENIAL);
			return false;
		}
	}
}
