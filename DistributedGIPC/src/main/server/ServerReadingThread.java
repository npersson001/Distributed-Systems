package main.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import inputport.nio.manager.NIOManagerFactory;

public class ServerReadingThread implements Runnable {
	BlockingQueue<Message> receivedMessages;
	List<SocketChannel> socketChannels;
	Server server;
	
	public ServerReadingThread(List<SocketChannel> socketChannelList, BlockingQueue<Message> queue, Server aServer){
		socketChannels = socketChannelList;
		receivedMessages = queue;
		server = aServer;
	}
	
	public void run(){
		// check the queue for entry in continuous loop 
		while(true){
			Message message = null;
			try {
				message = receivedMessages.take();
				// if in atomic mode send message to all otherwise send to all except original sender (ecapsulated in object on queue)
				if(server.getAtomicBroadcast()){
					// send message to clients
					for(SocketChannel socketChannel: socketChannels){
						NIOManagerFactory.getSingleton().write(socketChannel, message.getByteMessage());
					}
				}
				else{
					// send messages to all clients except original sender
					for(SocketChannel socketChannel: socketChannels){
						if(!socketChannel.equals(message.getClientSocket())){
							NIOManagerFactory.getSingleton().write(socketChannel, message.getByteMessage());
						}
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
