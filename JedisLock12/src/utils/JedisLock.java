package utils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
/**
 * @author rain
 * @create 2020-06-22 16:16
 */
public class JedisLock {
    BaseJedisLockAdapter jedis;
    String lockKey;
    int expireMsecs;
    int timeoutMsecs;
    boolean locked;

    public JedisLock(BaseJedisLockAdapter jedis, String lockKey) {
        this.expireMsecs = 60000;
        this.timeoutMsecs = 10000;
        this.locked = false;
        this.jedis = jedis;
        this.lockKey = lockKey;
    }

    public JedisLock(BaseJedisLockAdapter jedis, String lockKey, int timeoutMsecs) {
        this(jedis, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    public JedisLock(BaseJedisLockAdapter jedis, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(jedis, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    public JedisLock(String lockKey) {
        this(null, lockKey);
    }

    public JedisLock(String lockKey, int timeoutMsecs) {
        this(null, lockKey, timeoutMsecs);
    }

    public JedisLock(String lockKey, int timeoutMsecs, int expireMsecs) {
        this(null, lockKey, timeoutMsecs, expireMsecs);
    }

    public String getLockKey() {
        return this.lockKey;
    }

    public synchronized boolean acquire() throws InterruptedException {
        return this.acquire(this.jedis);
    }

    public synchronized boolean acquire1(Object object) throws InterruptedException {
        int timeout = this.timeoutMsecs;
        if(object instanceof  Jedis){
            Jedis jedis = (Jedis)object;
            while(timeout >= 0) {
                long expires = System.currentTimeMillis() + (long)this.expireMsecs + 1L;
                String expiresStr = String.valueOf(expires);
                if (jedis.setnx(this.lockKey, expiresStr) == 1L) {
                    this.locked = true;
                    return true;
                }

                String currentValueStr = jedis.get(this.lockKey);
                if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                    String oldValueStr = jedis.getSet(this.lockKey, expiresStr);
                    if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                        this.locked = true;
                        return true;
                    }
                }

                timeout -= 100;
                Thread.sleep(100L);
            }
        }

        if(object instanceof JedisCluster){
            JedisCluster jedis = (JedisCluster)object;
            while(timeout >= 0) {
                long expires = System.currentTimeMillis() + (long)this.expireMsecs + 1L;
                String expiresStr = String.valueOf(expires);
                if (jedis.setnx(this.lockKey, expiresStr) == 1L) {
                    this.locked = true;
                    return true;
                }

                String currentValueStr = jedis.get(this.lockKey);
                if (currentValueStr != null && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                    String oldValueStr = jedis.getSet(this.lockKey, expiresStr);
                    if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                        this.locked = true;
                        return true;
                    }
                }

                timeout -= 100;
                Thread.sleep(100L);
            }
        }


        return false;
    }

    public synchronized boolean acquire(BaseJedisLockAdapter jedis) throws InterruptedException {
        int timeout = this.timeoutMsecs;

        while(timeout >= 0) {
            long expires = System.currentTimeMillis() + (long)this.expireMsecs + 1L;
            String expiresStr = String.valueOf(expires);
            if (jedis.setNX(this.lockKey, expiresStr)) {
                this.locked = true;
                return true;
            }
            String currentValueStr = jedis.get(this.lockKey) + "";
            if (currentValueStr != null && !"null".equals(currentValueStr)  && Long.parseLong(currentValueStr) < System.currentTimeMillis()) {
                String oldValueStr = jedis.getSet(this.lockKey, expiresStr) + "";
                if (oldValueStr != null && !"null".equals(oldValueStr) &&  oldValueStr.equals(currentValueStr)) {
                    this.locked = true;
                    return true;
                }
            }

            timeout -= 100;
            Thread.sleep(100L);
        }
        return false;
    }
    public synchronized void release() {
        this.release(this.jedis);
    }

    public synchronized void release(BaseJedisLockAdapter jedis) {
        jedis.del(lockKey);

    }
}
