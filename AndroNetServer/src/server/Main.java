package server;

import interfaces.IListener;
import main.Connection;
import main.Server;
import pl.umk.andronetandroidclient.app.Dupa;

public class Main {

	public static void main(String[] args)
	{
		final main.Server server=new main.Server(5555);
		server.addListener("drawPosition", new IListener() {
			@Override
			public void received(Connection connection, Object object) {
				server.sendToAll("drawPosition", object);
			}
		});
		server.start();
	}
}