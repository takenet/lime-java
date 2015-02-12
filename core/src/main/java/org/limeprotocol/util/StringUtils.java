package org.limeprotocol.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class StringUtils {

    public static final String STRING_EMPTY = "";

    public static boolean isNullOrEmpty(String string){
        return string == null || string.equals("");
    }

    public static boolean isNullOrWhiteSpace(String string){
        return isNullOrEmpty(string) || string.trim().length() == 0;
    }

    public static String format(String pattern, Object... values){

        Map<String, Object> tags = new LinkedHashMap<String, Object>();

        for (int i=0; i<values.length; i++){
            tags.put("\\{" + i + "\\}", values[i]==null ? "" : values[i]);
        }

        String formatted = pattern;
        for (Map.Entry<String, Object> tag : tags.entrySet()) {
            // bottleneck, creating temporary String objects!
            formatted = formatted.replaceAll(tag.getKey(), tag.getValue().toString());
        }
        return formatted;
    }

    public static String trimEnd(String string, String finalCharacter){
        string.trim();
        if(string.endsWith(finalCharacter)){
            return string.substring(0, string.length()-1);
        }

        return string;
    }

    public static String toBase64(String value) {
        return new String(encodeBase64(value.getBytes(), false));
    }

    public static String toCamelCase(String s){

        String[] parts = s.split("_");

        //All caps without '_'
        if(parts.length == 0){
            return s.toLowerCase();
        }

        String camelCaseString = "";
        for (int i = 0; i< parts.length; i++){

            if(i == 0){
                camelCaseString = parts[i].toLowerCase();
            } else {
                camelCaseString = camelCaseString + toProperCase(parts[i]);
            }
        }
        return camelCaseString;
    }

    public static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }

    private static final char[] CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
    private static final int[] IA = new int[256];
    static {
        Arrays.fill(IA, -1);
        for (int i = 0, iS = CA.length; i < iS; i++)
            IA[CA[i]] = i;
        IA['='] = 0;
    }

    // ****************************************************************************************
    // *  char[] version
    // ****************************************************************************************

    /** Encodes a raw byte array into a BASE64 <code>char[]</code> representation i accordance with RFC 2045.
     * @param sArr The bytes to convert. If <code>null</code> or length 0 an empty array will be returned.
     * @param lineSep Optional "\r\n" after 76 characters, unless end of file.<br>
     * No line separator will be in breach of RFC 2045 which specifies max 76 per line but will be a
     * little faster.
     * @return A BASE64 encoded array. Never <code>null</code>.
     */
    private final static char[] encodeBase64(byte[] sArr, boolean lineSep)
    {
        // Check special case
        int sLen = sArr != null ? sArr.length : 0;
        if (sLen == 0)
            return new char[0];

        int eLen = (sLen / 3) * 3;              // Length of even 24-bits.
        int cCnt = ((sLen - 1) / 3 + 1) << 2;   // Returned character count
        int dLen = cCnt + (lineSep ? (cCnt - 1) / 76 << 1 : 0); // Length of returned array
        char[] dArr = new char[dLen];

        // Encode even 24-bits
        for (int s = 0, d = 0, cc = 0; s < eLen;) {
            // Copy next three bytes into lower 24 bits of int, paying attension to sign.
            int i = (sArr[s++] & 0xff) << 16 | (sArr[s++] & 0xff) << 8 | (sArr[s++] & 0xff);

            // Encode the int into four chars
            dArr[d++] = CA[(i >>> 18) & 0x3f];
            dArr[d++] = CA[(i >>> 12) & 0x3f];
            dArr[d++] = CA[(i >>> 6) & 0x3f];
            dArr[d++] = CA[i & 0x3f];

            // Add optional line separator
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }

        // Pad and encode last bits if source isn't even 24 bits.
        int left = sLen - eLen; // 0 - 2.
        if (left > 0) {
            // Prepare the int
            int i = ((sArr[eLen] & 0xff) << 10) | (left == 2 ? ((sArr[sLen - 1] & 0xff) << 2) : 0);

            // Set last four chars
            dArr[dLen - 4] = CA[i >> 12];
            dArr[dLen - 3] = CA[(i >>> 6) & 0x3f];
            dArr[dLen - 2] = left == 2 ? CA[i & 0x3f] : '=';
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }


}


