package main.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

// an object to hold the message with the socket that it came for
// necessary for nonatomic execution so server doesnt send message back to the sender
public class AMessage implements Message {
	ByteBuffer byteMessage;
	SocketChannel clientSocket;
	
	public AMessage(ByteBuffer mes, SocketChannel client){
		byteMessage = mes;
		clientSocket = client;
	}
	
	public void setByteMessage(ByteBuffer message){
		byteMessage = message;
	}
	
	public ByteBuffer getByteMessage(){
		return byteMessage;
	}
	
	public void setClientSocket(SocketChannel socket){
		clientSocket = socket;
	}
	
	public SocketChannel getClientSocket(){
		return clientSocket;
	}
}
