package main;

import inputport.datacomm.duplex.DuplexClientInputPort;
import inputport.datacomm.duplex.DuplexServerInputPort;
import inputport.datacomm.duplex.object.ADuplexObjectInputPortFactory;

import java.nio.ByteBuffer;

public class ANilsCustomDuplexObjectInputPortFactory extends ADuplexObjectInputPortFactory{
	public DuplexClientInputPort<Object> createDuplexClientInputPort(DuplexClientInputPort<ByteBuffer> bbClientInputPort) {
		return new ANilsCustomDuplexObjectClientInputPort(bbClientInputPort);
	}
	public DuplexServerInputPort<Object> createDuplexServerInputPort(DuplexServerInputPort<ByteBuffer> bbServerInputPort) {
		return new ANilsCustomDuplexObjectServerInputPort(bbServerInputPort);
	}
}
