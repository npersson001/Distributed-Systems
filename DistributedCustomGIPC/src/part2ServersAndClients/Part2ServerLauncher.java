package part2ServersAndClients;

import assignments.util.A4TraceUtility;
import examples.gipc.counter.customization.ACustomCounterServer;
import examples.gipc.counter.customization.ATracingFactorySetter;
import examples.gipc.counter.customization.FactorySetterFactory;
import main.ANilsTracingFactorySetter;
import part1ServersAndClients.Part1Server;
import part1ServersAndClients.Part1TracingFactorySetter;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.CUSTOM_RPC_SERVER})
public class Part2ServerLauncher {
	public static void main (String[] args) {
//		util.trace.port.objects.ObjectTraceUtility.setTracing();
		assignments.util.A4TraceUtility.setTracing();
		FactorySetterFactory.setSingleton(new Part2TracingFactorySetter());
		ACustomCounterServer.launch();
	}

}
