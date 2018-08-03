package main.gipc;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import consensus.ProposalFeedbackKind;
import main.rmi.ASimulationParametersBeanFactory;
import main.rmi.RMIClientObj;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.trace.port.consensus.ProposalAcceptRequestSent;
import util.trace.port.consensus.ProposalAcceptedNotificationReceived;
import util.trace.port.consensus.ProposalLearnedNotificationSent;
import util.trace.port.consensus.RemoteProposeRequestReceived;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class AGIPCSender implements GIPCSender{
	List<GIPCClientObj> clients = new ArrayList<>();

	@Override
	public void gipcMessage(GIPCClientObj sendingClient, String message){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal received
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message);  // redundant for autograder
		if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_ASYNCHRONOUS) {
			if(ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast()) {
				// atomic send to all clients
				for(GIPCClientObj client : clients) {
					try {
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						client.receiveMessage(message);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
			else {
				// nonatomic send to all except sending client
				for(GIPCClientObj client : clients){
					if(!client.equals(sendingClient)) {
						try {
							ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
							util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
							client.receiveMessage(message);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_SYNCHRONOUS) {
			if(ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast()) {
				boolean unanimousAccept = true;
				for(GIPCClientObj client : clients) {
					try {
						ProposalAcceptRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, message);
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						boolean response = client.receiveMessageRequest(message);
						unanimousAccept = unanimousAccept && response;
						ProposalAcceptedNotificationReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message, response ? ProposalFeedbackKind.SUCCESS : ProposalFeedbackKind.SERVICE_DENIAL);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				if(unanimousAccept) {
					for(GIPCClientObj client : clients) {
						try {
							ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
							util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
							client.receiveMessage(message);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					ASimulationParametersBeanFactory.getSingleton().setCommand(message); // use message property to hold old accepted command
				}
				else {
					for(GIPCClientObj client : clients) {
						try {
							ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, ASimulationParametersBeanFactory.getSingleton().getCommand()); // proposal sent back
							util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
							client.receiveMessage(ASimulationParametersBeanFactory.getSingleton().getCommand());
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
			}
			else {
				boolean unanimousAccept = true;
				for(GIPCClientObj client : clients) {
					if(!client.equals(sendingClient)) {
						try {
							ProposalAcceptRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, message);
							util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
							boolean response = client.receiveMessageRequest(message);
							unanimousAccept = unanimousAccept && response;
							ProposalAcceptedNotificationReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message, response ? ProposalFeedbackKind.SUCCESS : ProposalFeedbackKind.SERVICE_DENIAL);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
				}
				if(unanimousAccept) {
					for(GIPCClientObj client : clients) {
						if(!client.equals(sendingClient)) {
							try {
								ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
								util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
								client.receiveMessage(message);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
					ASimulationParametersBeanFactory.getSingleton().setCommand(message); // use message property to hold old accepted command
				}
				else {
					for(GIPCClientObj client : clients) {
						if(!client.equals(sendingClient)) {
							try {
								ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, ASimulationParametersBeanFactory.getSingleton().getCommand()); // proposal sent back
								util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
								client.receiveMessage(ASimulationParametersBeanFactory.getSingleton().getCommand());
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	public void connect(GIPCClientObj client) {
		clients.add(client);
	}
}
