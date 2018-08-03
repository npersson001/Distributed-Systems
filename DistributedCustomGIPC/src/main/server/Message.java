package main.server;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public interface Message {
	public void setByteMessage(ByteBuffer message);
	
	public ByteBuffer getByteMessage();
	
	public void setClientSocket(SocketChannel socket);
	
	public SocketChannel getClientSocket();
}
