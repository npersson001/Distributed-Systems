package serversAndClients;

import inputport.datacomm.duplex.object.DuplexObjectInputPortSelector;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import inputport.rpc.duplex.DuplexReceivedCallInvokerSelector;
import inputport.rpc.duplex.DuplexSentCallCompleterSelector;
import main.ANilsCustomDuplexObjectInputPortFactory;
import main.ANilsCustomDuplexReceivedCallInvokerFactory;
import serialization.SerializerSelector;
import util.trace.port.rpc.RPCTraceUtility;
import examples.gipc.counter.customization.ACustomDuplexObjectInputPortFactory;
import examples.gipc.counter.customization.ACustomDuplexReceivedCallInvokerFactory;
import examples.gipc.counter.customization.ACustomSentCallCompleterFactory;
import examples.gipc.counter.customization.ACustomSerializerFactory;
import examples.gipc.counter.layers.AMultiLayerCounterClient;
import examples.gipc.counter.layers.AMultiLayerCounterServer;

public class ANilsCustomCounterServer extends AMultiLayerCounterServer{
	public static void setFactories() {
		DuplexReceivedCallInvokerSelector.setReceivedCallInvokerFactory(
				new ANilsCustomDuplexReceivedCallInvokerFactory());
//		DuplexReceivedCallInvokerSelector.setReceivedCallInvokerFactory(
//				new AnAsynchronousCustomDuplexReceivedCallInvokerFactory());
		DuplexSentCallCompleterSelector.setDuplexSentCallCompleterFactory(
				new ACustomSentCallCompleterFactory());
		DuplexObjectInputPortSelector.setDuplexInputPortFactory(
				new ANilsCustomDuplexObjectInputPortFactory());
		SerializerSelector.setSerializerFactory(new ACustomSerializerFactory());

	}
	public static void main (String[] args) {		
//		BufferTraceUtility.setTracing();
//		RPCTraceUtility.setTracing();
		setFactories();
		init();
		setPort();
		addListeners();
		
		ReceiveReturnMessage<Object> ret = duplexRPCServerInputPort.receive("Client 1");
		System.out.print(ret.getMessage());
		duplexRPCServerInputPort.send("Client 1", "Got your message: " + ret.getMessage());
		
//		while (true) {
//			ReceiveReturnMessage aReceivedMessage = gipcRegistry.getRPCServerPort().receive();
//			if (aReceivedMessage == null) {
//				break;
//			}
//			System.out.println("Received message:" + aReceivedMessage );
//		}
	}
	

}
