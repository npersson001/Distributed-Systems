package main;

import java.nio.ByteBuffer;

import inputport.datacomm.ReceiveRegistrarAndNotifier;
import inputport.datacomm.duplex.DuplexClientInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectClientInputPort;
import inputport.datacomm.duplex.object.explicitreceive.AReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import util.trace.port.objects.ReceivedMessageDequeued;
import util.trace.port.objects.ReceivedMessageQueueCreated;

public class ANilsCustomDuplexObjectClientInputPort extends ADuplexObjectClientInputPort {
	final String DEFAULT_CLIENT_NAME = "default_client";
	ANilsCustomBlockingQueue queue = new ANilsCustomBlockingQueue(DEFAULT_CLIENT_NAME);

	public ANilsCustomDuplexObjectClientInputPort(
			DuplexClientInputPort<ByteBuffer> aBBClientInputPort) {
		super(aBBClientInputPort);
		ReceivedMessageQueueCreated.newCase(this, queue);
	}
	
	protected ReceiveRegistrarAndNotifier<Object> createReceiveRegistrarAndNotifier() {
		return new ANilsCustomClientReceiveNotifier(this);
	}
		
	public void send(String aDestination, Object aMessage) {
		System.out.println (aDestination + "<-" + aMessage);
		super.send(aDestination, aMessage);	
	}
	
	public ANilsCustomBlockingQueue getQueue() {
		return queue;
	}
	
	public ReceiveReturnMessage<Object> receive(String aSource) {		
		Object obj = queue.take();
		ReceivedMessageDequeued.newCase(aSource, queue, obj);
		return new AReceiveReturnMessage<Object>(aSource, obj);
	}
}