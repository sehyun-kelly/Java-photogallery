import org.junit.*;

import java.sql.Connection;
import java.sql.SQLException;

public class SetUpTest {

    @Test
    public void testConnectionValid() {
        Connection testConnection = SetUp.getConnection();
        try {
            Assert.assertTrue(testConnection.isValid(5));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testConnectionNotNull() {
        Connection testConnection = SetUp.getConnection();
        Assert.assertNotEquals(null, testConnection);
    }

    @Test
    public void testConnectionNotClose() {
        Connection testConnection = SetUp.getConnection();
        try {
            testConnection.close();
            testConnection = SetUp.getConnection();
            Assert.assertTrue(testConnection.isValid(5));
        } catch (SQLException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSameConnection() {
        Connection testConnection = SetUp.getConnection();
        Connection anotherConnection = SetUp.getConnection();
        Assert.assertEquals(anotherConnection, testConnection);
    }
}
