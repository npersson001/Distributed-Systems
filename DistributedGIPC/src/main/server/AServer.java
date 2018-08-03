package main.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.net.ServerSocketFactory;

import assignments.util.inputParameters.SimulationParametersListener;
import assignments.util.mainArgs.ServerArgsProcessor;
import examples.gipc.counter.customization.FactorySetterFactory;
import examples.mvc.rmi.duplex.ADistributedInheritingRMICounter;
import examples.mvc.rmi.duplex.DistributedRMICounter;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.factories.SelectorFactorySet;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import util.trace.port.rpc.gipc.GIPCRPCTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;
import inputport.nio.manager.AnNIOManager;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.SelectionManager;
import inputport.nio.manager.factories.SelectionManagerFactory;
import inputport.nio.manager.factories.classes.AReadingAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import inputport.rpc.ACachingAbstractRPCProxyInvocationHandler;
import inputport.rpc.GIPCLocateRegistry;
import inputport.rpc.GIPCRegistry;
import main.client.AClientParametersListener;
import main.gipc.AGIPCSender;
import main.gipc.GIPCSender;
import main.rmi.ARMISender;
import main.rmi.ASimulationParametersBeanFactory;
import main.rmi.RMISender;
import port.ATracingConnectionListener;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;

@Tags({DistributedTags.SERVER, DistributedTags.RMI, DistributedTags.NIO, DistributedTags.GIPC})
public class AServer implements Server {
	ServerReceiver receiver;
	ServerSocketChannel serverSocketChannel;
	List<SocketChannel> clientList;
	BlockingQueue<Message> receivedMessages;
	Thread readingThread;
	SimulationParametersListener parametersListener;
	//boolean atomicBroadcast = true; 
	RMISender rmiSender;
	GIPCSender gipcSender;
	//int delayTime;
	
	public static final String READ_THREAD_NAME = "Read Thread";
	
	public AServer() {
	}
	
	// method to get the delay time for remote calls
	public int getDelayTime() {
		return ASimulationParametersBeanFactory.getSingleton().getDelay();
	}
	
	// method to set the delay time for any remote calls
	public void setDelayTime(int t) {
		ASimulationParametersBeanFactory.getSingleton().setDelay(t);
	}

	// method to create communication objects (in this case only receiver)
	protected void createCommunicationObjects() {
		createReceiver();
	}
	
	// method to create a receiver for the server
	protected void createReceiver() {
		receiver = new AServerReceiver(receivedMessages);
	}
	
	// method to set the factory to accept connection and read
	protected void setFactories() {
		AcceptCommandFactorySelector.setFactory(new AReadingAcceptCommandFactory());
	}

	// method to make the server listen for connection requests and accept them
	protected void makeServerConnectable(int aServerPort) {
		NIOManagerFactory.getSingleton().enableListenableAccepts(
				serverSocketChannel, this);
	}
	
	// method for overall initialization
	public void initialize(int aServerPort, int aRegistryPort, String aRegistryHost, int aGIPCRegistryPort) {
		initializeNIO(aServerPort);
		initializeRMI(aRegistryPort, aRegistryHost);
		initializeGIPC(aGIPCRegistryPort);
	}

	// method to set up the server for NIO
	public void initializeNIO(int aServerPort) {
		createMessageQueue();
		createClientList();
		setFactories();		
		serverSocketChannel = createSocketChannel(aServerPort);
		createCommunicationObjects();
		createReadingThread(receivedMessages);
		makeServerConnectable(aServerPort);
	}
	
	// method to set up server for RMI
	public void initializeRMI(int aRegistryPort, String aRegistryHost){
		try {
			Registry rmiRegistry = LocateRegistry.getRegistry(aRegistryHost, aRegistryPort);
			RMIRegistryLocated.newCase(this, aRegistryHost, aRegistryPort, rmiRegistry);
			RMISender rmiSend = new ARMISender(this);
			rmiSender = rmiSend;
			UnicastRemoteObject.exportObject(rmiSend, 0);
			RMIObjectRegistered.newCase(this, RMI_SENDER_NAME, rmiSender, rmiRegistry);
			rmiRegistry.rebind(RMI_SENDER_NAME, rmiSend);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// method to set up server for GIPC
	public void initializeGIPC(int aGIPCRegistryPort) {
		GIPCRegistry gipcRegistry = GIPCLocateRegistry.createRegistry(aGIPCRegistryPort);
		gipcSender = new AGIPCSender();			
		gipcRegistry.rebind(GIPC_SENDER_NAME, gipcSender);	
		gipcRegistry.getInputPort().addConnectionListener(new ATracingConnectionListener(gipcRegistry.getInputPort()));
		createParameterController(); // used to be in initialize NIO, find where to put
	}
	
	// method to create list of client socket channels to keep track for when sending messages to all
	protected void createClientList(){
		clientList = new ArrayList<SocketChannel>();
	}
	
	// method to create the message blocking queue so the selector can put incomming messages on it for the reading thread
	protected void createMessageQueue(){
		receivedMessages = new ArrayBlockingQueue<Message>(262144);
	}
	
	// method to create the reading thread which will do the processing so the selector can take more messages
	protected void createReadingThread(BlockingQueue<Message> queue){
		ServerReadingThread thread = new ServerReadingThread(clientList, queue, this);
		readingThread = new Thread(thread);
		readingThread.setName(READ_THREAD_NAME);
		readingThread.start();
	}

	// method to create the socket channel so its open for connections
	protected ServerSocketChannel createSocketChannel(int aServerPort) {
		try {
			ServerSocketChannel retVal = ServerSocketChannel.open();
			InetSocketAddress isa = new InetSocketAddress(aServerPort);
			retVal.socket().bind(isa);
			SocketChannelBound.newCase(this, retVal, isa);
			return retVal;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// method to add the receiver as a listener of the socket channel to take messages off of it
	protected void addReadListener(SocketChannel aSocketChannel) {
		NIOManagerFactory.getSingleton().addReadListener(aSocketChannel,
				receiver);
	}

	// method to add listeners (in this case only the read listener)
	protected void addListeners(SocketChannel aSocketChannel) {
		addReadListener(aSocketChannel);		
	}
	
	// method that fires when a connection has been accepted
	@Override
	public void socketChannelAccepted(ServerSocketChannel aServerSocketChannel,
			SocketChannel aSocketChannel) {
		addListeners(aSocketChannel);
		clientList.add(aSocketChannel);
	}
	
	// method to set up the console reader for dynamic invocation of parameters
	protected void createParameterController(){
		parametersListener = new AServerParametersListener(this);
		SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(parametersListener);
		SimulationParametersControllerFactory.getSingleton().processCommands();
	}
	
	// method to set the server to atomic or nonatomic mode based on passed boolean
	public void setAtomicBroadcast(boolean b) {
		System.out.println("Atomic: " + b);
		ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(b);
	}
	
	// method to return the state of the server (atomic or nonatomic)
	public boolean getAtomicBroadcast(){
		return ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast();
	}
	
	public static void launchServer(int serverPort, int registryPort, String registryHost, int gipcServerPort) {
		//int serverPort, int registryPort, String registryHost, int gipcServerPort

		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();
		
		Server aServer = new AServer();
		aServer.initialize(serverPort, registryPort, registryHost, gipcServerPort);
	}

	// main method to set tracing and instantiate server object
	public static void main(String[] args) {
//		System.out.println(ServerArgsProcessor.getGIPCServerPort(args));
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		GIPCRPCTraceUtility.setTracing();
		
		Server aServer = new AServer();
		aServer.initialize(ServerArgsProcessor.getServerPort(args), ServerArgsProcessor.getRegistryPort(args), 
				ServerArgsProcessor.getRegistryHost(args), ServerArgsProcessor.getGIPCServerPort(args));
	}
}
