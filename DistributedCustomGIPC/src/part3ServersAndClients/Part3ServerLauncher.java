package part3ServersAndClients;

import assignments.util.A4TraceUtility;
import examples.gipc.counter.customization.ACustomCounterServer;
import examples.gipc.counter.customization.ATracingFactorySetter;
import examples.gipc.counter.customization.FactorySetterFactory;
import main.ANilsTracingFactorySetter;
import part1ServersAndClients.Part1Server;
import part1ServersAndClients.Part1TracingFactorySetter;
import part2ServersAndClients.Part2Server;
import part2ServersAndClients.Part2TracingFactorySetter;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.BLOCKING_RPC_SERVER})
public class Part3ServerLauncher {
	public static void main (String[] args) {
//		util.trace.port.objects.ObjectTraceUtility.setTracing();
//		util.trace.port.rpc.RPCTraceUtility.setTracing();
		assignments.util.A4TraceUtility.setTracing();
		FactorySetterFactory.setSingleton(new Part3TracingFactorySetter());
		ACustomCounterServer.launch();
	}

}
