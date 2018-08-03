package main.rmi;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ProposalMade;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class ASimulationParametersBean extends AnAbstractSimulationParametersBean{

	@Override
	public void setBroadcastMetaState(boolean broadcastMetaState) {
		this.broadcastMetaState = broadcastMetaState;
		broadcastBroadcastMode(broadcastMetaState);
		broadcastIPCMechanism(broadcastMetaState);
	}
	
	@Override
	public synchronized void setAtomicBroadcast(Boolean newValue) {
		ProposedStateSet.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newValue);
		atomicBroadcast = newValue;
	}
	
	@Override
	public void atomicBroadcast(boolean newValue) {
		ProposalMade.newCase(this, CommunicationStateNames.BROADCAST_MODE, -1, newValue);
		//setAtomicBroadcast(newValue);
	}
	
	@Override
	public synchronized void setIPCMechanism(IPCMechanism newValue) {
		ProposedStateSet.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newValue);
		ipcMechanism = newValue;
	}

	@Override
	public void ipcMechanism(IPCMechanism newValue) {
		ProposalMade.newCase(this, CommunicationStateNames.IPC_MECHANISM, -1, newValue);
		//setIPCMechanism(newValue);
	}
	
	@Override
	public void simulationCommand(String aCommand) {
		ProposalMade.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
	}
	
	public void acceptSimulationCommand(String aCommand) {
		ProposedStateSet.newCase(this, CommunicationStateNames.COMMAND, -1, aCommand);
	}
	
	@Override
	public void experimentInput() {}

	@Override
	public void quit(int aCode) {}
}
