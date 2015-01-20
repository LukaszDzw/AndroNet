package server;

import interfaces.IDisconnected;
import interfaces.IListener;
import main.Connection;
import main.Server;
import server.interfaces.IModule;
import server.modules.ChatModule;
import server.modules.ColorModule;
import server.modules.ConnectModule;
import server.modules.DrawModule;

public class Main {
	private static Server server;
	private static IModule[] modules;

	public static void main(String[] args)
	{
		server=new main.Server();
		modules=new IModule[]{new ChatModule(), new DrawModule(), new ConnectModule(), new ColorModule()};
		setupNetworking();
		server.start(5555);
	}

	private static void setupNetworking()
	{
		for(IModule module:modules)
		{
			module.setup(server);
		}
	}
}