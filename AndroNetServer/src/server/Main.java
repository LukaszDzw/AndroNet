package server;

import interfaces.IDisconnected;
import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;
import server.interfaces.IModule;
import server.modules.ChatModule;
import server.modules.DrawModule;

public class Main {
	private static Server server;

	private static ChatModule chatModule;
	private static DrawModule drawModule;

	public static void main(String[] args)
	{
		server=new main.Server(5555);
		chatModule=new ChatModule();
		drawModule=new DrawModule();
		setupNetworking();
		server.start();
	}

	private static void setupNetworking()
	{
		setupModule(chatModule, server);
		setupModule(drawModule, server);


	}

	private static void setupModule(IModule module, Server server)
	{
		module.setup(server);
	}
}