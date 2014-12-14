import interfaces.IListener;
import main.Client;
import main.Connection;

/**
 * Created by Lukasz on 2014-11-17.
 */
public class Main {

    public static void main(String[] args)
    {
        Client client=new Client("localhost", 5555);

        Frame frame=new Frame(client);
        client.start();
        client.addListener("test", new IListener() {
            @Override
            public void received(Connection connection, Object object) {
                System.out.println("dupa");

            }
        });
    }

}
