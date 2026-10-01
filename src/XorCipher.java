import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

public class XorCipher {
    @FunctionalInterface
    public interface XorKeyMaker {
        byte[] makeKey(int length);
    }

    private static final CipherSeed cipherSeed = CipherSeed.loadDefault();

    private static final XorKeyMaker keyMaker = cipherSeed::createKey;

    public static byte[] encrypt(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return xor(data, keyMaker.makeKey(data.length));
    }

    public static String decrypt(byte[] data) {
        byte[] result = xor(data, keyMaker.makeKey(data.length));
        return new String(result, StandardCharsets.UTF_8);
    }

    private static byte[] xor(byte[] data, byte[] key) {
        if (key == null || data.length != key.length) {
            throw new IllegalStateException("메시지와 XOR 키의 길이가 일치하지 않습니다.");
        }

        byte[] result = new byte[data.length];
        IntStream.range(0, data.length)
                .forEach(i -> result[i] = (byte) (data[i] ^ key[i]));
        return result;
    }

    public static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] fromHex(String hex) {
        if (hex == null || hex.isBlank() || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("16진수 암호문 형식이 올바르지 않습니다.");
        }

        byte[] data = new byte[hex.length() / 2];
        for (int i = 0; i < data.length; i++) {
            int pos = i * 2;
            data[i] = (byte) Integer.parseInt(hex.substring(pos, pos + 2), 16);
        }
        return data;
    }
}