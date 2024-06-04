package redis.utils;

public class ResponseUtil {
    public static byte[] getResponse(String response) {
        return ("$" + response.length() + "\r\n" + response + "\r\n").getBytes();
    }
}
