package main.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;

import assignments.util.MiscAssignmentUtils;
import inputport.nio.manager.NIOManagerFactory;

public class AServerReceiver implements
		ServerReceiver {
	BlockingQueue<Message> receivedMessages;
	
	public AServerReceiver(BlockingQueue<Message> queue){
		receivedMessages = queue;
	}

	// method that fires when a messages is sent over the socket channel
	@Override
	public void socketChannelRead(SocketChannel aSocketChannel,
			ByteBuffer aMessage, int aLength) {
		String aMeaning = new String(aMessage.array(), aMessage.position(),
				aLength);
		//System.out.println("Received: " + aMeaning);
		
		// add message to the reading queue with associated sender (socket channel)
		if(!receivedMessages.add(new AMessage(MiscAssignmentUtils.deepDuplicate(aMessage), aSocketChannel))){
			System.out.println("ERROR: buffer full!");
		}
	}

}
