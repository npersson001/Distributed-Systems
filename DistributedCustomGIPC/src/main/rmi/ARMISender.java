package main.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import consensus.ProposalFeedbackKind;
import main.gipc.GIPCClientObj;
import main.rmi.RMIClientObj;
import main.server.Server;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ProposalAcceptRequestSent;
import util.trace.port.consensus.ProposalAcceptedNotificationReceived;
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

	public void rmiMessage(RMIClientObj sendingClient, String message){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal received
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.COMMAND, -1, message); // redundant for autograder
		if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_ASYNCHRONOUS) {
			try {
				// send to all clients for atomic broadcast
				if(ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast()
						&& sendingClient.getAtomicBroadcast()){
					for(RMIClientObj c : clients){
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						c.receiveMessage(message);
					}
				} 
				// send to all clients except sending client for non-atomic broadcast
				else { 
					for(RMIClientObj c : clients){
						if(!c.equals(sendingClient)) {
							ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.COMMAND, -1, message); // proposal sent back
							util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
							c.receiveMessage(message);
						}
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_SYNCHRONOUS) {
			if(ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast()) {
				boolean unanimousAccept = true;
				for(RMIClientObj client : clients) {
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
					for(RMIClientObj client : clients) {
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
					for(RMIClientObj client : clients) {
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
				for(RMIClientObj client : clients) {
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
					for(RMIClientObj client : clients) {
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
					for(RMIClientObj client : clients) {
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
	
	public void setBroadcastMode(boolean newVal){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal);
		if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_ASYNCHRONOUS) {
			server.setAtomicBroadcast(newVal);
			for(RMIClientObj c : clients){
				ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal); // proposal sent back
				util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
				try {
					c.setAtomic(newVal);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_SYNCHRONOUS) {
			boolean unanimousAccept = true;
			for(RMIClientObj client : clients) {
				try {
					ProposalAcceptRequestSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal);
					util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
					boolean response = client.setAtomicRequest(newVal);
					unanimousAccept = unanimousAccept && response;
					ProposalAcceptedNotificationReceived.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal, response ? ProposalFeedbackKind.SUCCESS : ProposalFeedbackKind.SERVICE_DENIAL);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			if(unanimousAccept) {
				for(RMIClientObj client : clients) {
					try {
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newVal); // proposal sent back
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						client.setAtomic(newVal);;
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				ASimulationParametersBeanFactory.getSingleton().setAtomic(newVal);; // use message property to hold old accepted command
			}
			else {
				for(RMIClientObj client : clients) {
					try {
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, ASimulationParametersBeanFactory.getSingleton().getCommand()); // proposal sent back
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						client.setAtomic(ASimulationParametersBeanFactory.getSingleton().getAtomic());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void setBroadcast(boolean newVal){
		for(RMIClientObj c : clients) {
			util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
			try {
				c.setBroadcastBroadcastMode(newVal);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setIPC(IPCMechanism newVal){
		RemoteProposeRequestReceived.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal); // proposal received
		if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_ASYNCHRONOUS) {
			for(RMIClientObj c : clients){
				ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal); // proposal sent back
				util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
				try {
					c.setIPC(newVal);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getConsensusAlgorithm() == ConsensusAlgorithm.CENTRALIZED_SYNCHRONOUS) {
			boolean unanimousAccept = true;
			for(RMIClientObj client : clients) {
				try {
					ProposalAcceptRequestSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal);
					util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
					boolean response = client.setIPCRequest(newVal);
					unanimousAccept = unanimousAccept && response;
					ProposalAcceptedNotificationReceived.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal, response ? ProposalFeedbackKind.SUCCESS : ProposalFeedbackKind.SERVICE_DENIAL);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			if(unanimousAccept) {
				for(RMIClientObj client : clients) {
					try {
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newVal); // proposal sent back
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						client.setIPC(newVal);;
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				ASimulationParametersBeanFactory.getSingleton().setIPC(newVal);; // use message property to hold old accepted command
			}
			else {
				for(RMIClientObj client : clients) {
					try {
						ProposalLearnedNotificationSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, ASimulationParametersBeanFactory.getSingleton().getCommand()); // proposal sent back
						util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
						client.setIPC(ASimulationParametersBeanFactory.getSingleton().getIPC());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void setIPCBroadcast(boolean newVal){
		for(RMIClientObj c : clients){
			util.misc.ThreadSupport.sleep(ASimulationParametersBeanFactory.getSingleton().getDelay());
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
