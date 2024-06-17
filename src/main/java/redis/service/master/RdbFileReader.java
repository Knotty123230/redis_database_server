package redis.service.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.out;

public class RdbFileReader {
    private final RdbFileInfo rdbFileInfo;
    private final Map<String, Long> keysExpiration;

    public RdbFileReader() {
        rdbFileInfo = RdbFileInfo.getInstance();
        keysExpiration = new HashMap<>();
    }

    private static String getKey(InputStream fis, int b) throws IOException {
        String key;
        int strLength = lengthEncoding(fis, b);
        b = fis.read();
        out.println("strLength == " + strLength);
        if (strLength == 0) {
            strLength = b;
        }
        byte[] bytes = fis.readNBytes(strLength);
        key = new String(bytes);
        return key;
    }

    private static String getValue(InputStream fis) throws IOException {
        int strLength;
        int b;
        b = fis.read();
        strLength = lengthEncoding(fis, b);
        if (strLength == 0) {
            strLength = b;
        }
        byte[] bytesValue = fis.readNBytes(strLength);
        String value = new String(bytesValue);
        out.println(value);
        return value;
    }

    private static int lengthEncoding(InputStream is, int b) throws IOException {
        int length = 100;

        int first2bits = b & 11000000;
        out.println("first2bits = " + first2bits);
        if (first2bits == 0) {
            out.println("00");
            length = 0;
        } else if (first2bits == 128) {
            out.println("01");
            length = 2;
        } else if (first2bits == 256) {
            out.println("10");
            ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
            buffer.put(is.readNBytes(4));
            buffer.rewind();
            length = 1 + buffer.getInt();
        } else if (first2bits == 256 + 128) {
            out.println("11");
            length = 1;
        }
        return length;
    }

    public Map<String, String> readFile() {
        String key = "";
        Long expiration = null;
        Map<String, String> storage = new HashMap<>();
        try (
                InputStream fis =
                        new FileInputStream(new File(rdbFileInfo.getPath(), rdbFileInfo.getFileName()))) {
            byte[] redis = new byte[5];
            byte[] version = new byte[4];
            fis.read(redis);
            fis.read(version);
            out.println("Magic String = " +
                    new String(redis, StandardCharsets.UTF_8));
            out.println("Version = " +
                    new String(version, StandardCharsets.UTF_8));
            int b;
            header:
            while ((b = fis.read()) != -1) {
                switch (b) {
                    case 0xFF:
                        out.println("EOF");
                        break;
                    case 0xFE:
                        out.println("SELECTDB");
                        break;
                    case 0xFD:
                        out.println("EXPIRETIME");
                        break;
                    case 0xFC:
                        out.println("EXPIRETIMEMS");
                        break;
                    case 0xFB:
                        out.println("RESIZEDB");
                        b = fis.read();
                        fis.readNBytes(lengthEncoding(fis, b));
                        fis.readNBytes(lengthEncoding(fis, b));
                        break header;
                    case 0xFA:
                        out.println("AUX");
                        break;
                }
            }

            out.println("header done");
            b = fis.read();
            while ((b = fis.read()) != -1) {
                out.println("value-type = " + b);
                out.println("value-type = " + b);
                if (b == 0xFC) {
                    expiration = getExpiration(fis);
                    out.println("expiration = " + expiration);
                    b = fis.read();
                }
                out.println(" b = " + Integer.toBinaryString(b));

                if (!Integer.toBinaryString(b).equals("0")) {
                    break;
                }
                out.println("reading keys");
                key = getKey(fis, b);
                out.println(key);
                String value = getValue(fis);
                storage.put(key, value);
                if (expiration != null && expiration != 0) {
                    keysExpiration.put(key, expiration);
                }
            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        out.flush();
        return storage;
    }

    private Long getExpiration(InputStream fis) {
        try {
            byte[] bytes = fis.readNBytes(8);
            ByteBuffer wrap = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            return wrap.getLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Long> getKeysExpiration() {
        return keysExpiration;
    }
}
