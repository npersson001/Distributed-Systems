package part3ServersAndClients;

import assignments.util.A4TraceUtility;
import examples.gipc.counter.customization.ACustomCounterClient;
import examples.gipc.counter.customization.ATracingFactorySetter;
import examples.gipc.counter.customization.FactorySetterFactory;
import examples.gipc.counter.layers.AMultiLayerCounterClient;
import examples.gipc.counter.layers.AMultiLayerCounterClient1;
import main.ANilsTracingFactorySetter;
import part1ServersAndClients.Part1Client1;
import part1ServersAndClients.Part1TracingFactorySetter;
import part2ServersAndClients.Part2Client1;
import part2ServersAndClients.Part2TracingFactorySetter;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.BLOCKING_RPC_CLIENT1})
public class Part3Client1Launcher extends AMultiLayerCounterClient1 {
	public static void main (String[] args) {
//		util.trace.port.objects.ObjectTraceUtility.setTracing();
//		util.trace.port.rpc.RPCTraceUtility.setTracing();
		assignments.util.A4TraceUtility.setTracing();
		FactorySetterFactory.setSingleton(new Part3TracingFactorySetter());
		ACustomCounterClient.launch(CLIENT1_NAME);
	}

}
