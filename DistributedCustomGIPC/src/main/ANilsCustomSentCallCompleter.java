package main;

import inputport.datacomm.duplex.object.explicitreceive.ReceiveReturnMessage;
import inputport.datacomm.duplex.object.explicitreceive.ExplicitSourceReceive;
import inputport.rpc.duplex.ADuplexSentCallCompleter;
import inputport.rpc.duplex.DuplexRPCInputPort;
import inputport.rpc.duplex.DuplexSentCallCompleter;
import inputport.rpc.duplex.LocalRemoteReferenceTranslator;
import inputport.rpc.duplex.RPCReturnValue;

public class ANilsCustomSentCallCompleter extends ADuplexSentCallCompleter	{
	
	DuplexRPCInputPort rpcPort;
	
	public ANilsCustomSentCallCompleter(DuplexRPCInputPort aPort, LocalRemoteReferenceTranslator aRemoteHandler) {
		super(aPort, aRemoteHandler);	
		rpcPort = aPort;
	}	
	
	@Override
	protected void returnValueReceived(String aRemoteEndPoint, Object message) {
		System.out.println ("Processing return value of call:" + aRemoteEndPoint + "." + message);
//		super.returnValueReceived(aRemoteEndPoint, message);		
	}
	
	@Override
	public Object waitForReturnValue(String aRemoteEndPoint) {
//		Object retVal = super.waitForReturnValue(aRemoteEndPoint); // should I leave this here? 
		Object retVal = ((RPCReturnValue)rpcPort.receive(aRemoteEndPoint).getMessage()).getReturnValue();  // just why
		System.out.println (aRemoteEndPoint +  "-->" + retVal);
		return retVal;
	}
	
	protected Object getReturnValueOfRemoteFunctionCall(String aRemoteEndPoint, Object aMessage) {
		System.out.println ("getReturnValueOfRemoteFunctionCall called");
		Object retVal = super.getReturnValueOfRemoteFunctionCall(aRemoteEndPoint, aMessage);
		System.out.println ("Returning:" + retVal);
		return retVal;
	}
	
	protected Object getReturnValueOfRemoteProcedureCall(String aRemoteEndPoint, Object aMessage) {
		System.out.println ("getReturnValueOfRemoteProcedureCall called");
		Object retVal = super.getReturnValueOfRemoteProcedureCall(aRemoteEndPoint, aMessage);
		System.out.println ("Returning:" + retVal);
		return retVal;
	}
}
