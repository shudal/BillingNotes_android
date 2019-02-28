package moe.perci.haku.BillingNotes;

import java.security.MessageDigest;

public class Sha1 {
    public String data;
    private static String bytesToHexString(byte[] b) {
        String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            int n = b[i];
            if (n < 0) {
                n += 256;
            }
            int d1 = n / 16;
            int d2 = n % 16;
            resultSb.append(hexDigits[d1] + hexDigits[d2]);
        }
        return resultSb.toString();
    }

    public void setS(String s) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            this.data =  this.bytesToHexString(sha1.digest(s.getBytes()));
        }  catch (Exception e) {
        }
    }
}
