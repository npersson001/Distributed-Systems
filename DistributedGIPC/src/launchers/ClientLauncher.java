package launchers;

import main.client.AClient;
import main.client.Client;

public class ClientLauncher implements Launcher {
	public static void main(String[] args) {
		// Client.launchClient(serverHost,Constants.ATMOSPHERE_SERVER_PORT,"client1" ,RegistryHost, Constants.ATMOSPHERE_RMI_PORT, Constants.ATMOSPHERE_GIPC_PORT);
		String clientName = CLIENT_NAME + System.currentTimeMillis();
		AClient.launchClient(SERVER_HOST, SERVER_PORT, clientName, SERVER_HOST, RMI_PORT, GIPC_PORT);
	}
}
