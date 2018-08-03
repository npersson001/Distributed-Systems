package main.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.net.ServerSocketFactory;

import assignments.util.inputParameters.SimulationParametersListener;
import assignments.util.mainArgs.ClientArgsProcessor;
import assignments.util.mainArgs.ServerArgsProcessor;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.factories.SelectorFactorySet;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.nio.SocketChannelBound;
import inputport.nio.manager.AnNIOManager;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.SelectionManager;
import inputport.nio.manager.factories.SelectionManagerFactory;
import inputport.nio.manager.factories.classes.AReadingAcceptCommandFactory;
import inputport.nio.manager.factories.selectors.AcceptCommandFactorySelector;
import main.client.AClientParametersListener;
import util.annotations.Tags;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;
import util.tags.DistributedTags;

@Tags({DistributedTags.SERVER})
public class ANIOServer implements NIOServer {
	ServerReceiver receiver;
	ServerSocketChannel serverSocketChannel;
	List<SocketChannel> clientList;
	BlockingQueue<Message> receivedMessages;
	Thread readingThread;
	SimulationParametersListener parametersListener;
	boolean atomicBroadcast = true; 
	
	public static final String READ_THREAD_NAME = "Read Thread";
	
	public ANIOServer() {
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

	// method to set up the server 
	public void initialize(int aServerPort) {
		createMessageQueue();
		createClientList();
		setFactories();		
		serverSocketChannel = createSocketChannel(aServerPort);
		createCommunicationObjects();
		createReadingThread(receivedMessages);
		makeServerConnectable(aServerPort);
		createParameterController();
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
		atomicBroadcast = b;
	}
	
	// method to return the state of the server (atomic or nonatomic)
	public boolean getAtomicBroadcast(){
		return atomicBroadcast;
	}

	// main method to set tracing and instantiate server object
	public static void main(String[] args) {
		args = ServerArgsProcessor.removeEmpty(args);
		FactoryTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		BeanTraceUtility.setTracing();// not really needed, but does not hurt
		NIOServer aServer = new ANIOServer();
		aServer.initialize(ServerArgsProcessor.getServerPort(args));

	}
}
