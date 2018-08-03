package main.client;

import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;

import java.beans.PropertyChangeEvent;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
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
	boolean localExec;
	
	public AClientSender(SocketChannel aSocketChannel, String aClientName) {
		socketChannel = aSocketChannel;	
		clientName = aClientName;
		sendMessages = true;
		localExec = false;
	}
	
	public void setLocalExec(boolean b) {
		localExec = b;
	}

	public boolean getLocalExec() {
		return localExec;
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
		// if sendMessages is true, send the message over channel, otherwise ignore (local execution)
		if(localExec){return;}
		if(sendMessages){
			if (!anEvent.getPropertyName().equals("InputString")) return;
			ByteBuffer aMeaningByteBuffer = ByteBuffer.wrap(((String) anEvent.getNewValue()).getBytes());
			NIOManagerFactory.getSingleton().write(socketChannel, aMeaningByteBuffer);	
		}
	}

}
