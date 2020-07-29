package utils;

/**
 * @author itdlyh01
 */
public abstract class BaseJedisLockAdapter {
    public abstract Boolean setNX( String key,  String value);

    public abstract Object get( String key);

    public abstract Object getSet( String key,  String value);

    public abstract void del( String key);
}
