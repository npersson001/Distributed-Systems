package main.rmi;

import assignments.util.inputParameters.AnAbstractSimulationParametersBean;
import util.interactiveMethodInvocation.IPCMechanism;
import util.trace.port.consensus.ProposalMade;
import util.trace.port.consensus.ProposedStateSet;
import util.trace.port.consensus.communication.CommunicationStateNames;

public class ASimulationParametersBean extends AnAbstractSimulationParametersBean{
	String command;
	boolean rejectMetaStateChange;
	boolean waitConcensusBroadcastMode;
	boolean waitConcensusIPCMechanism;
	
//	public void setWaitConcensusBroadcastMode(boolean b) {
//		waitConcensusBroadcastMode = b;
//	}
//	
//	public boolean getWaitConcensusBroadcastMode() {
//		return waitConcensusBroadcastMode;
//	}
//	
//	public void setWaitConcensusIPCMechanism(boolean b) {
//		waitConcensusIPCMechanism = b;
//	}
//	
//	public boolean getWaitConcensusIPCMechanism() {
//		return waitConcensusIPCMechanism;
//	}
	
	public void setReject(boolean b) {
		rejectMetaStateChange = b;
	}
	
	public boolean getReject() {
		return rejectMetaStateChange;
	}
	
	public void setCommand(String com) {
		command = com;
	}
	
	public String getCommand() {
		return command;
	}
	
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
	
	public void setAtomic(boolean b) {
		atomicBroadcast = b;
	}
	
	public boolean getAtomic() {
		return atomicBroadcast;
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
	
	public void setIPC(IPCMechanism newValue) {
		ipcMechanism = newValue;
	}
	
	public IPCMechanism getIPC() {
		return ipcMechanism;
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
