package main.client;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import assignments.util.mainArgs.ClientArgsProcessor;
import assignments.util.mainArgs.ServerPort;
import coupledsims.Simulation;
import coupledsims.Simulation1;
import example.assignments.util.inputParameters.AnExampleSimulationParametersListener;
import examples.nio.manager.mvc.AMeaningOfLifeController;
import examples.nio.manager.mvc.AMeaningOfLifeModel;
import examples.nio.manager.mvc.AMeaningOfLifeView;
import examples.nio.manager.mvc.MeaningOfLifeController;
import examples.nio.manager.mvc.MeaningOfLifeModel;
import examples.nio.manager.mvc.MeaningOfLifeView;
import util.trace.bean.BeanTraceUtility;
import util.trace.factories.FactoryTraceUtility;
import util.trace.port.PerformanceExperimentEnded;
import util.trace.port.PerformanceExperimentStarted;
import util.trace.port.nio.NIOTraceUtility;
import inputport.nio.manager.AnNIOManager;
import inputport.nio.manager.NIOManager;
import inputport.nio.manager.NIOManagerFactory;
import inputport.nio.manager.factories.classes.AConnectCommandFactory;
import inputport.nio.manager.factories.classes.AReadingWritingConnectCommandFactory;
import inputport.nio.manager.factories.selectors.ConnectCommandFactorySelector;
import main.BeauAndersonFinalProject;
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

@Tags({DistributedTags.CLIENT})
public class ANIOClient implements NIOClient {
	String clientName;
	ClientSender sender;
	ClientReceiver receiver;
	SocketChannel socketChannel;
	HalloweenCommandProcessor commandProcessor;
	BlockingQueue<String> receivedMessages;
	Thread readingThread;
	SimulationParametersListener parametersListener;
	boolean atomicBroadcast = true;
	boolean local = false;
	
	public static final String READ_THREAD_NAME = "Read Thread";
	public static final int SIMULATION_SIZE = 1000;
	
	public HalloweenCommandProcessor getCommandProcessor(){
		return commandProcessor;
	}

	public ANIOClient(String aClientName) {
		clientName = aClientName;
	}
	
	protected void setFactories() {		
		ConnectCommandFactorySelector.setFactory(new AReadingWritingConnectCommandFactory());
	}
	
	// method that sets up simulation and connection to server using NIO
	public void initialize(String aServerHost, int aServerPort) {
		createMessageQueue();
		createHalloweenSimulation();
		createReadingThread();
		setFactories();
		socketChannel = createSocketChannel();
		createCommunicationObjects();
		addListeners(socketChannel);
		connectToServer(aServerHost, aServerPort);
		createParameterController();
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
				clientName);
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
	}
	
	// method to set simulation to local or nonlocal processing based on a boolean (called from param listener)
	public void setLocalProcessing(boolean b){
		if(b){
			commandProcessor.setConnectedToSimulation(b);
		}
		else{
			commandProcessor.setConnectedToSimulation(!atomicBroadcast);
		}
		sender.setLocalExec(b);
		receiver.setLocalExec(b);
	}
	
	// method to set simulation to atomic or nonatomic processing based on boolean (called from param listener)
	public void setAtomicBroadcast(boolean b){
		commandProcessor.setConnectedToSimulation(!b);
		atomicBroadcast = b;
	}
	
	// method to run the experiment on the simulation (the process input 1000 commands)
	public void runExperiment() {
		long startTime = System.nanoTime();
		PerformanceExperimentStarted.newCase(this, startTime, SIMULATION_SIZE);
		for(int i = 0; i < SIMULATION_SIZE/4; i++){
			commandProcessor.setInputString("move 5 0");
			commandProcessor.setInputString("move 0 5");
			commandProcessor.setInputString("move -5 0");
			commandProcessor.setInputString("move 0 -5");
		}
		long endTime = System.nanoTime();
		PerformanceExperimentEnded.newCase(this, startTime, endTime, endTime-startTime, SIMULATION_SIZE);
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
			String aClientName) {
		FactoryTraceUtility.setTracing();
		BeanTraceUtility.setTracing();
		NIOTraceUtility.setTracing();
		NIOClient aClient = new ANIOClient(aClientName);
		aClient.initialize(aServerHost, aServerPort);		
	}

	// main method to get args from start
	public static void main(String[] args) {	
		args = ClientArgsProcessor.removeEmpty(args);
		MiscAssignmentUtils.setHeadless(ClientArgsProcessor.getHeadless(args));
		//MiscAssignmentUtils.setHeadless("true");
		launchClient(ClientArgsProcessor.getServerHost(args),
				ClientArgsProcessor.getServerPort(args),
				ClientArgsProcessor.getClientName(args));
	}
}
