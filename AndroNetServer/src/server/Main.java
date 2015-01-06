package server;

import interfaces.IDisconnected;
import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import server.interfaces.IModule;
import server.modules.ChatModule;
import server.modules.ConnectModule;
import server.modules.DrawModule;

public class Main {
	private static Server server;
	private static IModule[] modules;

	public static void main(String[] args)
	{
		server=new main.Server(5555);
		modules=new IModule[]{new ChatModule(), new DrawModule(), new ConnectModule()};
		setupNetworking();
		server.start();
	}

	private static void setupNetworking()
	{
		for(IModule module:modules)
		{
			module.setup(server);
		}
	}
}