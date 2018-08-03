package launchers;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Scanner;

import assignments.util.mainArgs.ServerArgsProcessor;
import main.rmi.ARMILauncher;
import util.trace.port.rpc.rmi.RMIRegistryCreated;

public class RegistryLauncher implements Launcher{
	public static void main(String[] args) {
		try {
			LocateRegistry.createRegistry(RMI_PORT);
			RMIRegistryCreated.newCase(new ARMILauncher(), RMI_PORT); 
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
