package main;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import inputport.datacomm.AReceiveRegistrarAndNotifier;
import inputport.datacomm.ReceiveRegistrarAndNotifier;
import inputport.datacomm.duplex.DuplexServerInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectServerInputPort;
import inputport.datacomm.duplex.object.explicitreceive.AReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import util.trace.port.objects.ReceivedMessageDequeued;
import util.trace.port.objects.ReceivedMessageQueueCreated;

public class ANilsCustomDuplexObjectServerInputPort extends ADuplexObjectServerInputPort{
	
	Map<String, ANilsCustomBlockingQueue> mapOfQueues = new HashMap<>();

	public ANilsCustomDuplexObjectServerInputPort(
			DuplexServerInputPort<ByteBuffer> aBBDuplexServerInputPort) {
		super(aBBDuplexServerInputPort);
	}
	
	protected ReceiveRegistrarAndNotifier<Object> createReceiveRegistrarAndNotifier() {
		return new ANilsCustomServerReceiveNotifier(this);
	}
	
	public ANilsCustomBlockingQueue getQueueFromSource(String source) {
		ANilsCustomBlockingQueue queue = mapOfQueues.get(source);
		if(queue == null) {
			mapOfQueues.put(source, new ANilsCustomBlockingQueue(source));
			queue = mapOfQueues.get(source);
			ReceivedMessageQueueCreated.newCase(this, queue);
		}
		// probably need to check if threads are waiting on the queue
		return queue;
	}
	
	public ANilsCustomBlockingQueue getQueue(String source) {
		ANilsCustomBlockingQueue queue = mapOfQueues.get(source);
		if(queue == null) {
			mapOfQueues.put(source, new ANilsCustomBlockingQueue(source));
			queue = mapOfQueues.get(source);
			ReceivedMessageQueueCreated.newCase(this, queue);
		}
		// probably need to check if threads are waiting on the queue
		return queue;
	}
	
	public ReceiveReturnMessage<Object> receive(String aSource) {
		Object obj = getQueueFromSource(aSource).take();
		ReceiveReturnMessage<Object> returnMessage = new AReceiveReturnMessage<Object>(aSource, obj);
		ReceivedMessageDequeued.newCase(aSource, getQueueFromSource(aSource).getQueue(), returnMessage);
		return returnMessage;
	}

}
