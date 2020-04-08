package apoc.bytes;

import apoc.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.rule.DbmsRule;
import org.neo4j.test.rule.ImpermanentDbmsRule;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class BytesTest {

    public static final Bytes BYTES = new Bytes();
    @ClassRule
    public static DbmsRule db = new ImpermanentDbmsRule();

    @BeforeClass
    public static void setUp() throws Exception {
        TestUtil.registerProcedure(db, Bytes.class);
    }

    @AfterClass
    public static void tearDown() {
        db.shutdown();
    }

    @Test
    public void fromHexString() {
        byte[] expected = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
        byte[] actual = BYTES.fromHexString("CaFEBaBE");
        assertArrayEquals("expected CaFEBaBE byte array", expected, actual);
    }

    @Test
    public void fromHexStringOddLength() {
        byte[] expected = new byte[]{(byte) 0x0A, (byte) 0xBC};
        byte[] actual = BYTES.fromHexString("ABC");
        assertArrayEquals("expected ABC byte array", expected, actual);
    }

    @Test
    public void fromHexStringEvenLength() {
        byte[] expected = new byte[]{(byte) 0xAB, (byte) 0xBA};
        byte[] actual = BYTES.fromHexString("ABBA");
        assertArrayEquals("expected ABBA byte array", expected, actual);
    }

    @Test
    public void fromHexStringNull() {
        assertNull("on null argument", BYTES.fromHexString(null));
        assertNull("on empty argument", BYTES.fromHexString(""));
        assertNull("on illegal hex string", BYTES.fromHexString("Hello"));
    }

    @Test
    public void fromHexStringUsingFunction() {
        Map<String, Object> parameters = Collections.singletonMap("text", "CAFEBABE");
        String query = "RETURN  apoc.bytes.fromHexString($text) AS value";
        final byte[] expected = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};

        try (Transaction tx = db.beginTx()) {
            final ResourceIterator<byte[]> actual = tx.execute(query, parameters).columnAs("value");
            assertArrayEquals("expected CAFEBABE byte array", expected, actual.next());
            assertFalse(actual.hasNext());
        }
    }

    @Test
    public void toHexString() {
        final byte[] input = new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE};
        final String expected = "CAFEBABE";
        final String actual = BYTES.toHexString(input);
        assertEquals(expected, actual);
    }

    @Test
    public void toHexStringOddLength() {
        final byte[] input = new byte[]{(byte) 0x0A, (byte) 0xBC};
        final String expected = "ABC";
        final String actual = BYTES.toHexString(input);
        assertEquals(expected, actual);
    }

    @Test
    public void toHexStringNull() {
        assertNull("On null argument", BYTES.toHexString(null));
        assertNull("On empty argument", BYTES.toHexString(new byte[0]));
    }

    @Test
    public void toHexStringUsingFunction() {
        Map<String, Object> parameters = Collections.singletonMap("bytes", new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE});
        String query = "RETURN apoc.bytes.toHexString($bytes) AS value";
        final String expected = "CAFEBABE";

        try (Transaction tx = db.beginTx()) {
            final ResourceIterator<String> actual = tx.execute(query, parameters).columnAs("value");
            assertEquals("expected CAFEBABE", expected, actual.next());
            assertFalse(actual.hasNext());
        }
    }

    @Test
    public void fromLongMax() {
        final byte[] expected = new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        final byte[] actual = BYTES.fromLong(Long.MAX_VALUE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void fromLongMin() {
        final byte[] expected = new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        final byte[] actual = BYTES.fromLong(Long.MIN_VALUE);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void fromLongZero() {
        final byte[] expected = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        final byte[] actual = BYTES.fromLong(0L);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void fromLongUsingFunction() {
        Map<String, Object> parameters = Collections.singletonMap("value", Long.MAX_VALUE);
        String query = "RETURN  apoc.bytes.fromLong($value) AS value";
        final byte[] expected = new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

        try (Transaction tx = db.beginTx()) {
            final ResourceIterator<byte[]> actual = tx.execute(query, parameters).columnAs("value");
            assertArrayEquals("Expected 7FFFFFFFFFFFFFFF byte array", expected, actual.next());
            assertFalse(actual.hasNext());
        }
    }

    @Test
    public void toLongMax() {
        final Long expected = Long.MAX_VALUE;
        final byte[] bytes = new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        final Long actual = BYTES.toLong(bytes);
        assertEquals(expected, actual);
    }

    @Test
    public void toLongMin() {
        final Long expected = Long.MIN_VALUE;
        final byte[] bytes = new byte[]{(byte) 0x80, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        final Long actual = BYTES.toLong(bytes);
        assertEquals(expected, actual);
    }

    @Test
    public void toLongZero() {
        final Long expected = 0L;
        final byte[] bytes = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        final Long actual = BYTES.toLong(bytes);
        assertEquals(expected, actual);
    }

    @Test
    public void toLongUsingFunction() {
        final Map<String, Object> parameters = Collections.singletonMap("value", new byte[]{(byte) 0x7F, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF});
        final String query = "RETURN  apoc.bytes.toLong($value) AS value";
        final Long expected = Long.MAX_VALUE;

        try (Transaction tx = db.beginTx()) {
            final ResourceIterator<Long> actual = tx.execute(query, parameters).columnAs("value");
            assertEquals(expected, actual.next());
            assertFalse(actual.hasNext());
        }
    }

    @Test
    public void fromString() {
        final byte[] expected = new byte[]{(byte) 0x46, (byte) 0x6F, (byte) 0x6F};
        final String input = "Foo";

        final byte[] actual = BYTES.fromString(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void fromStringEmpty() {
        final byte[] expected = new byte[0];
        final String input = "";

        final byte[] actual = BYTES.fromString(input);
        assertArrayEquals(expected, actual);
    }

    @Test
    public void fromStringNull() {
        final byte[] expected = null;
        final String input = null;

        final byte[] actual = BYTES.fromString(input);
        assertEquals(expected, actual);
    }

    @Test
    public void fromStringUsingFunction() {
        Map<String, Object> parameters = Collections.singletonMap("text", "Foo");
        String query = "RETURN  apoc.bytes.fromString($text) AS value";
        final byte[] expected = new byte[]{(byte) 0x46, (byte) 0x6F, (byte) 0x6F};

        try (Transaction tx = db.beginTx()) {
            final ResourceIterator<byte[]> actual = tx.execute(query, parameters).columnAs("value");
            assertArrayEquals("expected Foo byte array", expected, actual.next());
            assertFalse(actual.hasNext());
        }
    }

    @Test
    public void toStringEmpty() {
        final String expected = "";
        final byte[] input = new byte[0];

        final String actual = BYTES.toString(input);
        assertEquals(expected, actual);
    }

    @Test
    public void toStringNull() {
        final String expected = null;
        final byte[] input = null;

        final String actual = BYTES.toString(input);
        assertEquals(expected, actual);
    }

    @Test
    public void toStringCaseSensitive() {
        final String expected = "Foo";
        final String expectedNot = "foo";
        final byte[] input = new byte[]{(byte) 0x46, (byte) 0x6F, (byte) 0x6F};

        final String actual = BYTES.toString(input);
        assertEquals(expected, actual);
        assertNotEquals(expectedNot, actual);
    }

    @Test
    public void toStringUsingFunction() {
        Map<String, Object> parameters = Collections.singletonMap("bytes", new byte[]{(byte) 0x46, (byte) 0x6F, (byte) 0x6F});
        String query = "RETURN apoc.bytes.toString($bytes) AS value";
        final String expected = "Foo";

        try (Transaction tx = db.beginTx()) {
            final ResourceIterator<String> actual = tx.execute(query, parameters).columnAs("value");
            assertEquals("expected Foo", expected, actual.next());
            assertFalse(actual.hasNext());
        }
    }
}