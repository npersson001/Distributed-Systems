package main.client;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import inputport.nio.manager.NIOManagerFactory;
import stringProcessors.HalloweenCommandProcessor;

public class AClientReceiver implements
		ClientReceiver {
	HalloweenCommandProcessor commandProcessor;
	BlockingQueue<String> receivedMessages;
	boolean recMessages;
	boolean localExec;
	
	public AClientReceiver(BlockingQueue<String> aReceivedMessages){
		receivedMessages = aReceivedMessages;
		recMessages = true;
		localExec = false;
	}
	
	public void setLocalExec(boolean b) {
		localExec = b;
	}
	
	public boolean getLocalExec() {
		return localExec;
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
		// if boolean recMessages is true, socket is read, otherwise ignored
		if(localExec){return;}
		if(recMessages){
			String aMeaning = new String(aMessage.array(), aMessage.position(),
					aLength);
			System.out.println("Received: " + aMeaning);
			
			// add message to the reading queue
			if(!receivedMessages.add(aMeaning)){
				System.out.println("ERROR: buffer full!");
			}
		}
	}

}
