import org.junit.*;

import java.util.UUID;

public class UuidGeneratorTest {
    UUID testUuid;

    @Before
    public void setUp() {
        testUuid = UUID.randomUUID();
    }

    @Test
    public void testUuidValid() {
        byte[] uuidBytes = UuidGenerator.asBytes(testUuid);
        Assert.assertEquals(16, uuidBytes.length);
    }

    @Test
    public void testSameUuidIsSameBytes() {
        byte[] uuidBytes = UuidGenerator.asBytes(testUuid);
        byte[] sameBytes = UuidGenerator.asBytes(testUuid);
        Assert.assertArrayEquals(uuidBytes, sameBytes);
    }

    @Test
    public void testUuidConversion() {
        byte[] uuidBytes = UuidGenerator.asBytes(testUuid);
        UUID convertedUuid = UuidGenerator.asUuid(uuidBytes);
        Assert.assertEquals(testUuid, convertedUuid);
    }
}
