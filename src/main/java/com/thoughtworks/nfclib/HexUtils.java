package com.thoughtworks.nfclib;


import java.nio.charset.Charset;

public class HexUtils {
    public byte[] getBytesWithPadding(String text) {
        byte[] urlAsBytes = text.getBytes(Charset.forName("UTF-8"));
        int numberOfBytes = urlAsBytes.length;
        
        byte[] urlAsBytesPadded = new byte[getNextDivisibleByFour(numberOfBytes)];

        System.arraycopy(urlAsBytes, 0, urlAsBytesPadded, 0, numberOfBytes);

        return urlAsBytesPadded;
    }

    private int getNextDivisibleByFour(int numberOfBytes) {
        return numberOfBytes + (4 - (numberOfBytes % 4));
    }
}
