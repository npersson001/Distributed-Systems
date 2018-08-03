package main.client;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import main.gipc.GIPCClientObj;
import main.gipc.GIPCSender;
import main.rmi.ASimulationParametersBeanFactory;
import main.rmi.RMIClientObj;
import main.rmi.RMISender;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ActionWhileEnablingProposalIsPending;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.consensus.communication.CommunicationStateNames;

import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Scanner;
/**
 * Listens to model changes and sends them to the connected server through the
 * NIO manager.
 * @author Dewan
 *
 */
public class AClientSender implements ClientSender{
	SocketChannel socketChannel;
	String clientName;
	boolean sendMessages;
	RMISender rmiSender;
	GIPCSender gipcSender;
	RMIClientObj rmiClientObj;
	GIPCClientObj gipcClientObj;
	Client client;
	
	public AClientSender(SocketChannel aSocketChannel, String aClientName, Client aClient) {
		socketChannel = aSocketChannel;	
		clientName = aClientName;
		sendMessages = true;
		client = aClient;
	}
	
	public void setupRMI(RMISender s, RMIClientObj c) {
		rmiSender = s;
		rmiClientObj = c;
	}
	
	public void setupGIPC(GIPCSender aGIPCSender, GIPCClientObj aGIPCClientObj) {
		gipcSender = aGIPCSender;
		gipcClientObj = aGIPCClientObj;
	}
	
	public void setLocalExec(boolean b) {
		ASimulationParametersBeanFactory.getSingleton().setLocalProcessingOnly(b);
	}

	public boolean getLocalExec() {
		return ASimulationParametersBeanFactory.getSingleton().isLocalProcessingOnly();
	}
	
	public void setSendMessages(boolean value){
		sendMessages = value;
	}
	
	public boolean getSendMessages(){
		return sendMessages;
	}

	// method fired whenever something is written to the socket
	@Override
	public void propertyChange(PropertyChangeEvent anEvent) {
		if (!anEvent.getPropertyName().equals("InputString")) {return;}
		if(ASimulationParametersBeanFactory.getSingleton().isLocalProcessingOnly()) {return;}
		if(ASimulationParametersBeanFactory.getSingleton().isWaitForIPCMechanismConsensus()
				|| ASimulationParametersBeanFactory.getSingleton().isWaitForBroadcastConsensus()) {
			ActionWhileEnablingProposalIsPending.newCase(null, null, 0, null);
			return;
		}
		if(ASimulationParametersBeanFactory.getSingleton().getIPCMechanism() == IPCMechanism.NIO){
			// if sendMessages is true, send the message over channel, otherwise ignore (local execution)
			if(sendMessages){
				ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap(((String) anEvent.getNewValue()).getBytes());
				NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
			}
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getIPCMechanism() == IPCMechanism.RMI){
			ASimulationParametersBeanFactory.getSingleton().simulationCommand(anEvent.getNewValue().toString()); // make proposal
			util.misc.ThreadSupport.sleep(client.getDelayTime());
			try {
				RemoteProposeRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, anEvent.getNewValue().toString()); // proposal sent
				rmiSender.rmiMessage(rmiClientObj, anEvent.getNewValue().toString());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else if(ASimulationParametersBeanFactory.getSingleton().getIPCMechanism() == IPCMechanism.GIPC) {
			ASimulationParametersBeanFactory.getSingleton().simulationCommand(anEvent.getNewValue().toString()); // make proposal
			util.misc.ThreadSupport.sleep(client.getDelayTime());
			try {
				RemoteProposeRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, anEvent.getNewValue().toString()); // proposal sent
				gipcSender.gipcMessage(gipcClientObj, anEvent.getNewValue().toString());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
