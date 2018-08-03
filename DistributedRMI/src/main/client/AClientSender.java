package main.client;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import main.rmi.ASimulationParametersBeanFactory;
import main.rmi.RMIClientObj;
import main.rmi.RMISender;
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
	boolean rmi;
	boolean nio;
	RMISender rmiSender;
	RMIClientObj clientObj;
	Client client;
	
	public AClientSender(SocketChannel aSocketChannel, String aClientName, Client aClient) {
		socketChannel = aSocketChannel;	
		clientName = aClientName;
		sendMessages = true;
		nio = true;
		rmi = false;
		client = aClient;
	}
	
	public void setRMI(boolean b){
		rmi = b;
		nio = !b;
	}
	
	public void setupRMI(RMISender s, RMIClientObj c) {
		rmiSender = s;
		clientObj = c;
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
		if (!anEvent.getPropertyName().equals("InputString")) return;
		if(ASimulationParametersBeanFactory.getSingleton().isLocalProcessingOnly()){return;}
		if(nio){
			// if sendMessages is true, send the message over channel, otherwise ignore (local execution)
			if(sendMessages){
				ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap(((String) anEvent.getNewValue()).getBytes());
				NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
			}
		}
		else if(rmi){
			ASimulationParametersBeanFactory.getSingleton().simulationCommand(anEvent.getNewValue().toString()); // make proposal
			util.misc.ThreadSupport.sleep(client.getDelayTime());
			try {
				RemoteProposeRequestSent.newCase(this, CommunicationStateNames.COMMAND, -1, anEvent.getNewValue().toString()); // proposal sent
				rmiSender.rmiMessage(clientObj, anEvent.getNewValue().toString());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

}
