package main.client;

import java.rmi.RemoteException;

import assignments.util.inputParameters.ASimulationParametersController;
import assignments.util.inputParameters.SimulationParametersListener;
import main.rmi.ASimulationParametersBeanFactory;
import util.interactiveMethodInvocation.ConsensusAlgorithm;
import util.interactiveMethodInvocation.IPCMechanism;
import util.interactiveMethodInvocation.SimulationParametersController;
import util.trace.port.consensus.RemoteProposeRequestSent;
import util.trace.port.consensus.communication.CommunicationStateNames;
/**
 * Methods in this class are called when corresponding user commands are invoked.
 * These method simply print the method name and parameters.
 */
public class AClientParametersListener implements SimulationParametersListener {
	Client client;
	
	public AClientParametersListener(Client aClient){
		client = aClient;
	}

	// method called to set the system to atomic broadcast
	@Override
	public void atomicBroadcast(boolean newValue) {
		System.out.println("atomicBroadcast " + newValue);
		ASimulationParametersBeanFactory.getSingleton().atomicBroadcast(newValue); // make proposal
		util.misc.ThreadSupport.sleep(client.getDelayTime());
		try {
			if(ASimulationParametersBeanFactory.getSingleton().isBroadcastBroadcastMode()) {
				RemoteProposeRequestSent.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newValue); // proposal sent
				client.getRMISender().setBroadcastMode(newValue);
			}
			else {
				//ASimulationParametersBeanFactory.getSingleton().setAtomicBroadcast(newValue);
				client.setAtomicBroadcast(newValue); 
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
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

	public void broadcastBroadcastMode(boolean newValue) {
		System.out.println("broadcastBroadcastMode " + newValue);	
		util.misc.ThreadSupport.sleep(client.getDelayTime());
		try {
			client.getRMISender().setBroadcast(newValue);
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}
	
	@Override
	public void ipcMechanism(IPCMechanism newValue) {
		System.out.println("ipcMechanism " + newValue);
		ASimulationParametersBeanFactory.getSingleton().ipcMechanism(newValue); // make proposal 
		util.misc.ThreadSupport.sleep(client.getDelayTime());
		if(client.getBroadcastIPC()) {
			try {
				RemoteProposeRequestSent.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newValue); // proposal sent
				client.getRMISender().setIPC(newValue);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else {	
			if(newValue == IPCMechanism.NIO) {
				client.setRMISenderReceiver(false);
			}
			else if(newValue == IPCMechanism.RMI) {
				client.setRMISenderReceiver(true);
			}
		}
	}

	public void broadcastIPCMechanism(boolean newValue) {
		System.out.println("broadcastIPCMechanism " + newValue);
		util.misc.ThreadSupport.sleep(client.getDelayTime());
		try {
			client.getRMISender().setIPCBroadcast(newValue);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void broadcastMetaState(boolean newValue) {
		System.out.println("broadcastMetaState " + newValue);
		broadcastIPCMechanism(newValue);
		broadcastBroadcastMode(newValue);
	}
	
	@Override
	public void delaySends(int aMillisecondDelay) {
		client.setDelayTime(aMillisecondDelay);
	}
		
	// *********** below methods not currently in use for this assignment ************ //

	@Override
	public void waitForBroadcastConsensus(boolean newValue) {
		//System.out.println("waitForBroadcastConsensus " + newValue);		
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
	public void rejectMetaStateChange(boolean newValue) {
		// TODO Auto-generated method stub
	}
	
}
