import javax.swing.*;

/**
 * Created by Lukasz on 2014-11-17.
 */
public class Main {

    public static void main(String[] args)
    {
        Client client=new Client("localhost", 5555);

        Frame frame=new Frame(client);
        client.start();
    }

}
