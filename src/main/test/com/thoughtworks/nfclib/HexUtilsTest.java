package com.thoughtworks.nfclib;


import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HexUtilsTest {

    HexUtils hexUtils;

    @Before
    public void setUp() throws Exception {
        hexUtils = new HexUtils();
    }

    @Test
    public void shouldReturnCorrectNumberOfBytesWhenStringIsEmpty() throws Exception {
        assertThat(hexUtils.getBytesWithPadding("")).hasSize(4);
    }

    @Test
    public void shouldReturnCorrectNumberOfBytesWhenStringSizeDivisibleByFour() throws Exception {
        assertThat(hexUtils.getBytesWithPadding("four")).hasSize(8);
        assertThat(hexUtils.getBytesWithPadding("google.co.uk")).hasSize(16);
    }

    @Test
    public void shouldReturnCorrectNumberOfBytesWhenStringSizeNotDivisibleByFour() throws Exception {
        assertThat(hexUtils.getBytesWithPadding("to")).hasSize(4);
        assertThat(hexUtils.getBytesWithPadding("1")).hasSize(4);
        assertThat(hexUtils.getBytesWithPadding("thisstringhaslengthof23")).hasSize(24);
        assertThat(hexUtils.getBytesWithPadding("learn.adafruit.com/adafruit-pn532-rfid-nfc/ndef")).hasSize(48);
    }
}
