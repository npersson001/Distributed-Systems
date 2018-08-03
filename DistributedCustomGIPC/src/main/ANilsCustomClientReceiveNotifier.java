package main;

import util.trace.Tracer;
import util.trace.port.objects.ReceivedMessageQueued;
import inputport.datacomm.AReceiveRegistrarAndNotifier;
import inputport.datacomm.ReceiveListener;

public class ANilsCustomClientReceiveNotifier extends AReceiveRegistrarAndNotifier{
	
	ANilsCustomDuplexObjectClientInputPort inputPort;
	
	public ANilsCustomClientReceiveNotifier(ANilsCustomDuplexObjectClientInputPort i) {
		inputPort = i;
	}
	
	@Override
	public void notifyPortReceive (String aSource, Object aMessage) {	
		System.out.println (aSource + "->" + aMessage);
		super.notifyPortReceive(aSource, aMessage);
		
		ANilsCustomBlockingQueue queue = inputPort.getQueue();
		queue.put(aMessage);
		ReceivedMessageQueued.newCase(aSource, queue, aMessage);
	}
}
