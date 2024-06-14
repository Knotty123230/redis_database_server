package redis.service.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

import static java.lang.System.out;

public class RdbFileReader {
    private final RdbFileInfo rdbFileInfo;

    public RdbFileReader() {
        rdbFileInfo = RdbFileInfo.getInstance();
    }

    public List<String> readFile(){
        String key = "";
        List<String> keys = new LinkedList<>();
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
                    default:
                        out.println("DEFAULT CASE");
                        break;
                }
            }

            out.println("header done");
            while ((b = fis.read()) != -1) { // value type
                out.println("value-type = " + b);
                b = fis.read();
                out.println("value-type = " + b);
                out.println(" b = " + Integer.toBinaryString(b));
                out.println("reading keys");
                int strLength = lengthEncoding(fis, b);
                b = fis.read();
                out.println("strLength == " + strLength);
                if (strLength == 0) {
                    strLength = b;
                }
                out.println("strLength == " + strLength);
                byte[] bytes = fis.readNBytes(strLength);
                key = new String(bytes);
                keys.add(key);
                break;
            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        out.flush();
        return keys;
    }
    private static int lengthEncoding(InputStream is, int b) throws IOException {
        int length = 100;

        int first2bits = b & 11000000;
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
}
