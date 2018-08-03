package main.client;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import inputport.nio.manager.NIOManagerFactory;
import main.rmi.ASimulationParametersBeanFactory;
import stringProcessors.HalloweenCommandProcessor;

public class AClientReceiver implements
		ClientReceiver {
	HalloweenCommandProcessor commandProcessor;
	BlockingQueue<String> receivedMessages;
	boolean recMessages;
	boolean rmi;
	boolean nio;
	
	public AClientReceiver(BlockingQueue<String> aReceivedMessages){
		receivedMessages = aReceivedMessages;
		recMessages = true;
		rmi = false;
		nio = true;
	}
	
	public void setRMI(boolean b) {
		rmi = b;
		nio = !b;
	}
	
	public void setLocalExec(boolean b) {
		ASimulationParametersBeanFactory.getSingleton().setLocalProcessingOnly(b);
	}
	
	public boolean getLocalExec() {
		return ASimulationParametersBeanFactory.getSingleton().isLocalProcessingOnly();
	}
	
	public void setRecMessages(boolean value){
		recMessages = value;
	}
	
	public boolean getRecMessages(){
		return recMessages;
	}

	// method fired whenever something is received over the socket
	@Override
	public void socketChannelRead(SocketChannel aSocketChannel,
			ByteBuffer aMessage, int aLength) {
		if(nio) {
			// if boolean recMessages is true, socket is read, otherwise ignored
			if(ASimulationParametersBeanFactory.getSingleton().isLocalProcessingOnly()){return;}
			if(recMessages){
				String aMeaning = new String(aMessage.array(), aMessage.position(),
						aLength);
				System.out.println("Received NIO: " + aMeaning);
				
				// add message to the reading queue
				if(!receivedMessages.add(aMeaning)){
					System.out.println("ERROR: buffer full!");
				}
			}
		}
		else if(rmi) {}
	}

}
