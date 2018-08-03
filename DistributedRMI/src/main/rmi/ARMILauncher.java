package main.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import assignments.util.mainArgs.ServerArgsProcessor;
import util.annotations.Tags;
import util.tags.DistributedTags;
import util.trace.port.rpc.rmi.RMIRegistryCreated;

@Tags({DistributedTags.REGISTRY, DistributedTags.RMI})
public class ARMILauncher implements RegistryServer{
	public static void main(String args[]) {
		try {
			LocateRegistry.createRegistry(ServerArgsProcessor.getRegistryPort(args));
			RMIRegistryCreated.newCase(new ARMILauncher(), ServerArgsProcessor.getRegistryPort(args)); 
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
}
