package part2ServersAndClients;

import assignments.util.A4TraceUtility;
import examples.gipc.counter.customization.ACustomCounterClient;
import examples.gipc.counter.customization.ATracingFactorySetter;
import examples.gipc.counter.customization.FactorySetterFactory;
import examples.gipc.counter.layers.AMultiLayerCounterClient;
import examples.gipc.counter.layers.AMultiLayerCounterClient1;
import examples.gipc.counter.layers.AMultiLayerCounterClient2;
import main.ANilsTracingFactorySetter;
import part1ServersAndClients.Part1Client2;
import part1ServersAndClients.Part1TracingFactorySetter;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.CUSTOM_RPC_CLIENT2})
public class Part2Client2Launcher extends AMultiLayerCounterClient2 {
	public static void main (String[] args) {
//		util.trace.port.objects.ObjectTraceUtility.setTracing();
		assignments.util.A4TraceUtility.setTracing();
		FactorySetterFactory.setSingleton(new Part2TracingFactorySetter());
		ACustomCounterClient.launch(CLIENT2_NAME);
	}

}
