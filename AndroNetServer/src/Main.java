import interfaces.IListener;
import main.Connection;
import main.Server;

public class Main {

	public static void main(String[] args)
	{
		final Server server=new Server(5555);
		server.addListener("test", new IListener() {
			@Override
			public void received(Connection connection, Object object) {
				server.sendToAll("test", object);
			}
		});
		server.start();
	}
}