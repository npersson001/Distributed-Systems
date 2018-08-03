package main.client;

import assignments.util.inputParameters.ASimulationParametersController;
import assignments.util.inputParameters.SimulationParametersListener;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersController;
/**
 * Methods in this class are called when corresponding user commands are invoked.
 * These method simply print the method name and parameters.
 */
public class AClientParametersListener implements SimulationParametersListener {
	boolean localProcessing;
	NIOClient client;
	
	public AClientParametersListener(NIOClient aClient){
		client = aClient;
	}

	// method called to set the system to atomic broadcast
	@Override
	public void atomicBroadcast(boolean newValue) {
		System.out.println("atomicBroadcast " + newValue);	
		client.setAtomicBroadcast(newValue);
	}

	// method called to run the simulation experiment to time atomic vs non-atomic 
	@Override
	public void experimentInput() {
		System.out.println("experimentInput");
		client.runExperiment();
	}
	
	// method to set the simulation to run locally only (no communication)
	@Override
	public void localProcessingOnly(boolean newValue) {
		System.out.println("localProcessingOnly " + newValue);	
		client.setLocalProcessing(newValue);
	}
	
	// method to set inputString in simulation directy from console 
	@Override
	public void simulationCommand(String aCommand) {
		System.out.println("Simulation command: " + aCommand);
		client.getCommandProcessor().setInputString(aCommand);
	}
	
	// method to exit simulation
	@Override
	public void quit(int aCode) {
		System.out.println("Quitting with code " + aCode);
		System.exit(aCode);
	}
	// *********** below methods not currently in use for this assignment ************ //

	@Override
	public void ipcMechanism(IPCMechanism newValue) {
		//System.out.println("ipcMechanism " + newValue);	
	}

	public void broadcastBroadcastMode(boolean newValue) {
		//System.out.println("broadcastBroadcastMode " + newValue);		
	}

	@Override
	public void waitForBroadcastConsensus(boolean newValue) {
		//System.out.println("waitForBroadcastConsensus " + newValue);		
	}

	public void broadcastIPCMechanism(boolean newValue) {
		//System.out.println("broadcastIPCMechanism " + newValue);		
	}

	@Override
	public void waitForIPCMechanismConsensus(boolean newValue) {
		//System.out.println("waitForIPCMechanismConsensus " + newValue);		
	}

	@Override
	public void consensusAlgorithm(ConsensusAlgorithm newValue) {
		//System.out.println("consensusAlgorithm " + newValue);		
	}

	@Override
	public void broadcastMetaState(boolean newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delaySends(int aMillisecondDelay) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rejectMetaStateChange(boolean newValue) {
		// TODO Auto-generated method stub
		
	}
	
}
