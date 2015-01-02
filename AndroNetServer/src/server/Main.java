package server;

import interfaces.IDisconnected;
import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.network.enums.Tags;

public class Main {
	private static Server server;

	public static void main(String[] args)
	{
		server=new main.Server(5555);
		setupNetworking();
		server.start();
	}

	private static void setupNetworking()
	{
		server.addListener("drawPosition", new IListener() {
			@Override
			public void received(Connection connection, Object object) {
				server.sendToAll("drawPosition", object);
			}
		});

		server.addListener(Tags.chatMessage.name(), new IListener() {
			@Override
			public void received(Connection connection, Object o) {
				server.sendToAll(Tags.chatMessage.name(), o);
			}
		});

		server.setDisconnectedAction(new IDisconnected() {
			@Override
			public void disconnected(Connection connection) {
				System.out.println("bye connection " + connection.getId());
			}
		});
	}
}