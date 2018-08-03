package main.client;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import inputport.nio.manager.NIOManagerFactory;
import stringProcessors.HalloweenCommandProcessor;

public class ClientReadingThread implements Runnable {
	BlockingQueue<String> receivedMessages;
	HalloweenCommandProcessor commandProcessor;
	
	public ClientReadingThread(BlockingQueue<String> queue, HalloweenCommandProcessor aCommandProcessor){
		receivedMessages = queue;
		commandProcessor = aCommandProcessor;
	}
	
	// runnable execute method to start the thread
	public void run(){
		// check the queue for entry in continuous loop 
		while(true){
			String message = null;
			try {
				// take is a blocking call so it waits on entry of data
				message = receivedMessages.take();
				
				// execute command on the client side (the processing part)
				commandProcessor.processCommand(message);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public long getTime() {
		return System.currentTimeMillis();
	}
}
