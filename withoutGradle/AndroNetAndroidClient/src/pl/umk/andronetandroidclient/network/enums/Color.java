package pl.umk.andronetandroidclient.network.enums;

/**
 * Created by Lukasz on 2015-01-02.
 */
public enum Color{
    RED,
    GREEN,
    BLUE;

    public int getColorValue() {
        switch (this) {
            case GREEN:
                return 0x00ff0000;
            case BLUE:
                return 0x0000ff00;
            case RED:
            default:
                return 0xff000000;
        }
    }
}