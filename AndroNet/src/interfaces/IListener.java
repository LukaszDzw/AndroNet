package interfaces;

import main.Connection;

/**
 * Created by Lukasz on 2014-12-07.
 */
public interface IListener {
    public void received(Connection connection, Object object);
}
