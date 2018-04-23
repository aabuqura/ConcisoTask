
import com.company.conciso.HomeBean;
import com.company.conciso.redis.RedisConnection;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class TestCase {

    Jedis jedis;

    @Before
    public void init() {
        jedis = RedisConnection.getConnection();
        HomeBean bean = new HomeBean();
        for (int i = 0; i < 23; i++) {
            bean.loadCountryData("test_" + i + "", "testKey");
        }

    }

    @Test
    public void redisSize() {
        assertEquals("Checking size of List", 20, jedis.llen("testKey").longValue());
    }

    @Test
    public void redisFirstOut() {
        assertEquals("First In", "test_3", jedis.lindex("testKey", 19));
    }

    @Test
    public void redisFirstIn() {
        assertEquals("First In", "test_22", jedis.lindex("testKey", 0));
    }

    @After
    public void removekey() {
        jedis.del("testKey");
    }

}
