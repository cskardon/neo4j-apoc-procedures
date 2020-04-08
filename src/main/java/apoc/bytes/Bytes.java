package apoc.bytes;

import apoc.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.nio.ByteBuffer;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Math.min;

public class Bytes {

    public static final Pattern HEXSTRING = Pattern.compile("^[0-9A-F]+$", Pattern.CASE_INSENSITIVE);
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    @UserFunction("apoc.bytes.fromHexString")
    @Description("turns hex string into array of bytes, invalid values will result in null")
    public byte[] fromHexString(@Name("text") String text) {
        if (text == null || text.trim().isEmpty()) return null;

        if (!HEXSTRING.matcher(text).matches()) return null;

        int textLen = text.length();
        if(textLen % 2 == 1) {
            textLen++;
            text = String.format("0%s", text);
        }

        int bytesCount = textLen / 2 + textLen % 2;
        byte[] result = new byte[bytesCount];

        for (int pos = 0; pos < bytesCount; pos++) {
            int index = pos * 2;
            result[pos] = (byte) parseInt(text.substring(index, min(index + 2, textLen)), 16);
        }
        return result;
    }

    @UserFunction("apoc.bytes.toHexString")
    @Description("Turns a byte array into a hex string. Invalid values will result in null.")
    public String toHexString(@Name("bytes") byte[] bytes){
        //Original code from: https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
        if(bytes == null || bytes.length == 0)
            return null;

        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_ARRAY[v >>> 4];
            hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }

        if(hexChars[0] == '0'){
            return new String(hexChars, 1, hexChars.length - 1);
        }

        return new String(hexChars);
    }

    @UserFunction("apoc.bytes.fromLong")
    @Description("Turns a long into an array of bytes.")
    public byte[] fromLong(@Name("value") Long value){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        return buffer.array();
    }

    @UserFunction("apoc.bytes.toLong")
    @Description("Turns a byte array into a long.")
    public Long toLong(@Name("bytes") byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getLong();
    }
}
