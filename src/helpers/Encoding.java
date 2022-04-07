package helpers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Encoding {
    private static final String B62_ALPHABET =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final BigInteger B62_BASE = BigInteger.valueOf(62);

    private static MessageDigest md5;

    public static String base62Encode(String s) {
        String b62 = "";

        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(s.getBytes());
            BigInteger digestValue = new BigInteger(1, md5.digest());

            while (digestValue.compareTo(BigInteger.ZERO) > 0) {
                b62 += B62_ALPHABET.charAt(digestValue.mod(B62_BASE).intValue());
                digestValue = digestValue.divide(B62_BASE);
            }
        } catch (NoSuchAlgorithmException e) {
        }

        return b62;
    }
}
