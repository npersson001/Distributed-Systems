package main.client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import assignments.util.mainArgs.ClientArgsProcessor;
import assignments.util.mainArgs.ServerPort;
import coupledsims.Simulation;
import coupledsims.Simulation1;
import example.assignments.util.inputParameters.AnExampleSimulationParametersListener;
import examples.mvc.rmi.duplex.DistributedRMICounter;
import examples.nio.manager.mvc.AMeaningOfLifeController;
import examples.nio.manager.mvc.AMeaningOfLifeModel;
import examples.nio.manager.mvc.AMeaningOfLifeView;
import examples.nio.manager.mvc.MeaningOfLifeController;
import examples.nio.manager.mvc.MeaningOfLifeModel;
import examples.nio.manager.mvc.MeaningOfLifeView;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.misc.ThreadDelayed;
import util.trace.port.consensus.ConsensusTraceUtility;
import util.trace.port.nio.NIOTraceUtility;
import util.trace.port.rpc.rmi.RMIObjectLookedUp;
import util.trace.port.rpc.rmi.RMIObjectRegistered;
import util.trace.port.rpc.rmi.RMIRegistryLocated;
import util.trace.port.rpc.rmi.RMITraceUtility;
import inputport.nio.manager.AnNIOManager;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AConnectCommandFactory;
import inputport.nio.manager.factories.classes.AReadingWritingConnectCommandFactory;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import main.BeauAndersonFinalProject;
import main.rmi.ARMIClientObj;
import main.rmi.ASimulationParametersBeanFactory;
import main.rmi.RMIClientObj;
import main.rmi.RMISender;
import main.server.AServerReceiver;
import main.server.ServerReadingThread;
import stringProcessors.HalloweenCommandProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;
import assignments.util.MiscAssignmentUtils;
import assignments.util.inputParameters.ASimulationParametersController;
import assignments.util.inputParameters.SimulationParametersListener;
import util.interactiveMethodInvocation.SimulationParametersController;
import util.interactiveMethodInvocation.SimulationParametersControllerFactory;

@Tags({DistributedTags.CLIENT, DistributedTags.RMI, DistributedTags.NIO})
public class AClient implements Client {
	String clientName;
	ClientSender sender;
	ClientReceiver receiver;
	SocketChannel socketChannel;
	HalloweenCommandProcessor commandProcessor;
	BlockingQueue<String> receivedMessages;
	Thread readingThread;
	SimulationParametersListener parametersListener;
	RMIClientObj clientObj;
	RMISender rmiSend;
	//boolean atomicBroadcast = true;
	//boolean broadcastIPC = false;
	
	public static final String READ_THREAD_NAME = "Read Thread";

	public AClient(String aClientName) {
		clientName = aClientName;
	}
	
	// method to get the delay time for any remote calls
	public int getDelayTime() {
		return ASimulationParametersBeanFactory.getSingleton().getDelay();
	}
	
	// method to set the delay time for any remote calls
	public void setDelayTime(int t) {
		ASimulationParametersBeanFactory.getSingleton().setDelay(t);
	}
	
	// method to get the broadcast ipc boolean
	public boolean getBroadcastIPC() {
		return ASimulationParametersBeanFactory.getSingleton().isBroadcastIPCMechanism();
	}
	
	// method to set the broadcast ipc boolean
	public void setIPCBroadcast(boolean b) {
		System.out.println("Setting BroadcastIPC: " + b);
		ASimulationParametersBeanFactory.getSingleton().setBroadcastIPCMechanism(b);
	}
	
	// a method to get the clientObj
	public RMIClientObj getClientObj() {
		return clientObj;
	}
	
	// a method to get the RMI sender
	public RMISender getRMISender() {
		return rmiSend;
	}
	
	// a method to get the command processor
	public HalloweenCommandProcessor getCommandProcessor(){
		return commandProcessor;
	}
	
	// a method to set the factory for the commands
	protected void setFactories() {		
		ConnectCommandFactorySelector.setFactory(new AReadingWritingConnectCommandFactory());
	}
	
	// a method to set the sender and receiver of the RMI
	public void setRMISenderReceiver(boolean b) {
		System.out.println("Setting RMI/NIO: " + b);
		sender.setRMI(b);
		receiver.setRMI(b);
	}
	
	// initialize all forms of communication 
	public void initialize(String aServerHost, int aServerPort, String aRegistryHost, int aRegistryPort) {
		initializeNIO(aServerHost, aServerPort);
		initializeRMI(aRegistryHost, aRegistryPort); // check if host and port should be the same
		setRMISenderReceiver(true);
		setAtomicBroadcast(true);
		createParameterController(); // used to be in initializeNIO find better way to integrate this
	}
	
	// method that sets up simulation and connection to server using NIO
	public void initializeNIO(String aServerHost, int aServerPort) {
		createMessageQueue();
		createHalloweenSimulation();
		createReadingThread();
		setFactories();
		socketChannel = createSocketChannel();
		createCommunicationObjects();
		addListeners(socketChannel);
		connectToServer(aServerHost, aServerPort);
	}
	
	// method that sets up simulation and connection to server using RMI
	public void initializeRMI(String aRegistryHost, int aRegistryPort){
		try {
			Registry rmiRegistry = LocateRegistry.getRegistry(aRegistryHost, aRegistryPort);
			RMIRegistryLocated.newCase(this, aRegistryHost, aRegistryPort, rmiRegistry);
			rmiSend = (RMISender) rmiRegistry.lookup(SENDER_NAME);
			RMIObjectLookedUp.newCase(this, rmiSend, SENDER_NAME, rmiRegistry);
			clientObj = new ARMIClientObj(this);
			UnicastRemoteObject.exportObject(clientObj, 0);
			RMIObjectRegistered.newCase(this, SENDER_NAME, clientObj, rmiRegistry);
			rmiSend.connect(clientObj);
			sender.setupRMI(rmiSend, clientObj); // got to be a safer way to do this, find out later
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// method to create the blocking queue for messages received
	protected void createMessageQueue(){
		receivedMessages = new ArrayBlockingQueue<String>(262144);
	}
	
	// method to create the simulation with specified parameters
	public void createHalloweenSimulation(){
		commandProcessor = BeauAndersonFinalProject.createSimulation(
				"prefix_filler",
				Simulation1.SIMULATION1_X_OFFSET, 
				Simulation.SIMULATION_Y_OFFSET, 
				Simulation.SIMULATION_WIDTH, 
				Simulation.SIMULATION_HEIGHT, 
				Simulation1.SIMULATION1_X_OFFSET, 
				Simulation.SIMULATION_Y_OFFSET);
		// connection to simulation should be false in beginning (only execute when receive messages from server)
		commandProcessor.setConnectedToSimulation(false);
	}

	// method to call connectToSocketChannel to establic connection
	public void connectToServer(String aServerHost, int aServerPort) {
		// no listeners need to be registered, assuming writes go through
		connectToSocketChannel(aServerHost, aServerPort);
	}
	
	// method to create the parameter controller (allows dynamic method invocation from console)
	protected void createParameterController(){
		parametersListener = new AClientParametersListener(this);
		SimulationParametersControllerFactory.getSingleton().addSimulationParameterListener(parametersListener);
		SimulationParametersControllerFactory.getSingleton().processCommands();
	}

	// method to connect to to server over channel
	protected void connectToSocketChannel(String aServerHost, int aServerPort) {
		try {
			InetAddress aServerAddress = InetAddress.getByName(aServerHost);
			NIOManagerFactory.getSingleton().connect(socketChannel,
					aServerAddress, aServerPort, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// method to create the socket channel
	protected SocketChannel createSocketChannel() {
		try {
			SocketChannel retVal = SocketChannel.open();
			return retVal;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// method to create and name the reading thread
	protected void createReadingThread(){
		ClientReadingThread thread = new ClientReadingThread(receivedMessages, commandProcessor);
		readingThread = new Thread(thread);
		readingThread.setName(READ_THREAD_NAME);
		readingThread.start();
	}

	// method that fires to announce the socket channel is properly connected
	@Override
	public void connected(SocketChannel aSocketChannel) {
		System.out.println("Ready to send messages to server");
	}
	
	// method that fires to announce the socket channel was not properly connected
	@Override
	public void notConnected(SocketChannel aSocketChannel, Exception e) {
		System.err.println("Could not connect:" +aSocketChannel);
		if (e != null)
		   e.printStackTrace();
	}
	
	// method to call create sender and receiver
	protected void createCommunicationObjects() {
		createSender();
		createReceiver();
	}
	
	// method to create the client sender
	protected void createSender() {
		sender = new AClientSender(socketChannel,
				clientName, this);
	}
	
	// method to create the client receiver
	protected void createReceiver() {
		receiver = new AClientReceiver(receivedMessages);
	}
	
	// method to ad listeners to the simulation and to the socket.
	protected void addListeners(SocketChannel aSocketChannel) {
		addModelListener();
		addReadListener(aSocketChannel);
	}
	
	// method that adds the receiver as a listener of the socket channel
	protected void addReadListener(SocketChannel aSocketChannel) {
		NIOManagerFactory.getSingleton().addReadListener(aSocketChannel,
				receiver);
	}
	
	// method that adds the sender as a listener of the simulation
	protected void addModelListener(){		
		commandProcessor.addPropertyChangeListener(sender);
		commandProcessor.addPropertyChangeListener(new ADummyListener()); // for auto grader, dont really need to call add listener 2 times
	}
	
	// method to set simulation to local or nonlocal processing based on a boolean (called from param listener)
	public void setLocalProcessing(boolean b){
		if(b){
			commandProcessor.setConnectedToSimulation(b);
		}
		else{
			commandProcessor.setConnectedToSimulation(!ASimulationParametersBeanFactory.getSingleton().isAtomicBroadcast());
		}
		//sender.setLocalExec(b);
		//receiver.setLocalExec(b);
		ASimulationParametersBeanFactory.getSingleton().setLocalProcessingOnly(b);
	}
	
	// method to set simulation to atomic or nonatomic processing based on boolean (called from param listener)
	public void setAtomicBroadcast(boolean b){
		commandProcessor.setConnectedToSimulation(!b);
		ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(b);
		System.out.println("Atomic broadcast: " + b);
	}
	
	// method to run the experiment on the simulation (the process input 1000 commands)
	public void runExperiment() {
		long startTime = System.nanoTime();
		for(int i = 0; i < 250; i++){
			commandProcessor.setInputString("move 5 0");
			commandProcessor.setInputString("move 0 5");
			commandProcessor.setInputString("move -5 0");
			commandProcessor.setInputString("move 0 -5");
		}
		long endTime = System.nanoTime();
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Execution time of experiment: " + (endTime - startTime) + " ns");
	}

	// method to instantiate client and set tracing on 
	public static void launchClient(String aServerHost, int aServerPort,
			String aClientName, String aRegistryHost, int aRegistryPort) {
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		RMITraceUtility.setTracing();
		ConsensusTraceUtility.setTracing();
		ThreadDelayed.enablePrint();
		Client aClient = new AClient(aClientName);
		aClient.initialize(aServerHost, aServerPort, aRegistryHost, aRegistryPort);
	}

	// main method to get args from start
	public static void main(String[] args) {	
		args = ClientArgsProcessor.removeEmpty(args);
		MiscAssignmentUtils.setHeadless(ClientArgsProcessor.getHeadless(args));
		launchClient(ClientArgsProcessor.getServerHost(args),
				ClientArgsProcessor.getServerPort(args),
				ClientArgsProcessor.getClientName(args), 
				ClientArgsProcessor.getRegistryHost(args), 
				ClientArgsProcessor.getRegistryPort(args));
	}
}
