package part1ServersAndClients;

import assignments.util.A4TraceUtility;
import examples.gipc.counter.customization.ACustomCounterServer;
import examples.gipc.counter.customization.ATracingFactorySetter;
import examples.gipc.counter.customization.FactorySetterFactory;
import main.ANilsTracingFactorySetter;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.EXPLICIT_RECEIVE_SERVER})
public class Part1ServerLauncher {
	public static void main (String[] args) {
//		util.trace.port.objects.ObjectTraceUtility.setTracing();
		assignments.util.A4TraceUtility.setTracing();
		FactorySetterFactory.setSingleton(new Part1TracingFactorySetter());
		ACustomCounterServer.launch();
	}

}
