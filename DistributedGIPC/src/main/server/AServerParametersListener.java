package main.server;

import assignments.util.inputParameters.ASimulationParametersController;
import assignments.util.inputParameters.SimulationParametersListener;
import main.rmi.ASimulationParametersBeanFactory;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersController;
/**
 * Methods in this class are called when corresponding user commands are invoked.
 * These method simply print the method name and parameters.
 */
public class AServerParametersListener implements SimulationParametersListener {
	//boolean atomicBroadcast;
	boolean localProcessing;
	Server server;
	
	public AServerParametersListener(Server aServer){
		server = aServer;
	}

	// method that sets the server to atomic or nonatomic mode based on boolean passed from user on console
	@Override
	public void atomicBroadcast(boolean newValue) {
		System.out.println("atomicBroadcast " + newValue);	
		server.setAtomicBroadcast(newValue);
	}
	
	@Override
	public void delaySends(int aMillisecondDelay) {
		server.setDelayTime(aMillisecondDelay);
	}
	
	@Override
	public void consensusAlgorithm(ConsensusAlgorithm newValue) {
		System.out.println("consensusAlgorithm " + newValue);
		ASimulationParametersBeanFactory.getSingleton().consensusAlgorithm(newValue);
	}

	// **************** none of these methods were used for this assignment ***************** //
	
	@Override
	public void ipcMechanism(IPCMechanism newValue) {
		//System.out.println("ipcMechanism " + newValue);	
	}

	@Override
	public void experimentInput() {
		//System.out.println("experimentInput");			
	}
	
	@Override
	public void localProcessingOnly(boolean newValue) {
		//System.out.println("localProcessingOnly " + newValue);	
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
	public void quit(int aCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void simulationCommand(String aCommand) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void broadcastMetaState(boolean newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rejectMetaStateChange(boolean newValue) {
		// TODO Auto-generated method stub
		
	}
	
}
