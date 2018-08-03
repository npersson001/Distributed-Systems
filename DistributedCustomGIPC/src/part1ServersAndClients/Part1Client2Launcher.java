package part1ServersAndClients;

import assignments.util.A4TraceUtility;
import examples.gipc.counter.customization.ACustomCounterClient;
import examples.gipc.counter.customization.ATracingFactorySetter;
import examples.gipc.counter.customization.FactorySetterFactory;
import examples.gipc.counter.layers.AMultiLayerCounterClient;
import examples.gipc.counter.layers.AMultiLayerCounterClient1;
import examples.gipc.counter.layers.AMultiLayerCounterClient2;
import main.ANilsTracingFactorySetter;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.EXPLICIT_RECEIVE_CLIENT2})
public class Part1Client2Launcher extends AMultiLayerCounterClient2 {
	public static void main (String[] args) {
//		util.trace.port.objects.ObjectTraceUtility.setTracing();
		assignments.util.A4TraceUtility.setTracing();
		FactorySetterFactory.setSingleton(new Part1TracingFactorySetter());
		ACustomCounterClient.launch(CLIENT2_NAME);
	}

}
