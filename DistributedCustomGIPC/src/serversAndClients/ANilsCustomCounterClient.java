package serversAndClients;

import inputport.datacomm.duplex.object.DuplexObjectInputPortSelector;
import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import inputport.rpc.ACachingAbstractRPCProxyInvocationHandler;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.duplex.DuplexRPCClientInputPort;
import inputport.rpc.duplex.DuplexReceivedCallInvokerSelector;
import inputport.rpc.duplex.DuplexSentCallCompleterSelector;
import inputport.rpc.duplex.SynchronousDuplexReceivedCallInvokerSelector;
import main.ANilsCustomDuplexObjectInputPortFactory;
import main.ANilsCustomDuplexReceivedCallInvokerFactory;
import port.ATracingConnectionListener;
import serialization.SerializerSelector;
import util.trace.port.rpc.RPCTraceUtility;

import java.util.Scanner;

import examples.gipc.counter.customization.ACustomDuplexObjectInputPortFactory;
import examples.gipc.counter.customization.ACustomDuplexReceivedCallInvokerFactory;
import examples.gipc.counter.customization.ACustomSentCallCompleterFactory;
import examples.gipc.counter.customization.ACustomSerializerFactory;
import examples.gipc.counter.layers.AMultiLayerCounterClient;
import examples.mvc.rmi.duplex.DistributedRMICounter;

public class ANilsCustomCounterClient extends AMultiLayerCounterClient{
//	protected static DuplexRPCClientInputPort duplexRPCClientInputPort;
	
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
		Scanner sc = new Scanner(System.in);
		System.out.println("What is the name of your client?");
		String clientName = sc.nextLine();
		
		setFactories();
		init(clientName);
		setPort();
		
		
		while(true) {
			System.out.println("What do you want to send?");
			String input = sc.nextLine();
			if(input.equals("q")) {
				break;
			}
			duplexRPCClientInputPort.send(input);
			duplexRPCClientInputPort.receive("Server");
		}
		
		
//		sendByteBuffers();
//		sendObjects();
//		doOperations();	
//		while (true) {
//			ReceiveReturnMessage aReceivedMessage = gipcRegistry.getRPCClientPort().receive();
//			if (aReceivedMessage == null) {
//				break;
//			}
//			System.out.println("Received message:" + aReceivedMessage );
//		}
	}
	

}
