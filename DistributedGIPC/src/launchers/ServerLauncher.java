package launchers;

import main.server.AServer;

public class ServerLauncher implements Launcher{
	public static void main(String[] args) {
		//int serverPort, int registryPort, String registryHost, int gipcServerPort
		AServer.launchServer(SERVER_PORT,RMI_PORT,"localhost",GIPC_PORT);
	}
}
