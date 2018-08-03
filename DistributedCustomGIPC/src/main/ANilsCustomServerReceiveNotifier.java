package main;

import util.trace.Tracer;
import util.trace.port.objects.ReceivedMessageQueued;
import inputport.datacomm.AReceiveRegistrarAndNotifier;
import inputport.datacomm.ReceiveListener;

public class ANilsCustomServerReceiveNotifier extends AReceiveRegistrarAndNotifier{
	
	ANilsCustomDuplexObjectServerInputPort inputPort;
	
	public ANilsCustomServerReceiveNotifier(ANilsCustomDuplexObjectServerInputPort i) {
		inputPort = i;
	}
	
	@Override
	public void notifyPortReceive (String aSource, Object aMessage) {	
		System.out.println ("*** ON NILS NOTIFY *** " + aSource + "->" + aMessage);
		super.notifyPortReceive(aSource, aMessage);
		
		ANilsCustomBlockingQueue queue = inputPort.getQueue(aSource);
		queue.put(aMessage);
		ReceivedMessageQueued.newCase(aSource, queue, aMessage);
	}
}
