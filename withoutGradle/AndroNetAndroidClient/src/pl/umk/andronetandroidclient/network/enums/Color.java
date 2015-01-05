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
                return 0xff00ff00;
            case BLUE:
                return 0xff0000ff;
            case RED:
            default:
                return 0xffff0000;
        }
    }
}