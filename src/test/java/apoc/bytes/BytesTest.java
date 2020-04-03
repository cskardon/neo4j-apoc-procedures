package apoc.bytes;

import apoc.util.TestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
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
        byte[] actual = new Bytes().fromHexString("CaFEBaBE");
        assertArrayEquals("expected CaFEBaBE byte array", expected, actual);
    }

    @Test
    public void fromHexStringOddlLength() {
        byte[] expected = new byte[]{(byte) 0xAB, (byte) 0x0C};
        byte[] actual = new Bytes().fromHexString("ABC");
        assertArrayEquals("expected ABC byte array", expected, actual);
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

}