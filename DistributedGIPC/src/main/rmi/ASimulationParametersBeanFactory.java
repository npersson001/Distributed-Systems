package main.rmi;

public class ASimulationParametersBeanFactory {
	static ASimulationParametersBean simParamBean = new ASimulationParametersBean();
	
	public static ASimulationParametersBean getSingleton() {
		return simParamBean;
	}
}
