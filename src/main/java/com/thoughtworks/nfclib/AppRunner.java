package com.thoughtworks.nfclib;

import javax.smartcardio.*;
import java.util.List;

public class AppRunner {

    public void run() throws Exception {

        TerminalFactory factory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = factory.terminals().list();
        CardTerminal terminal = terminals.get(0);

        if (terminal.waitForCardPresent(60000)) {
            Card card = terminal.connect("*");

            CardChannel channel = card.getBasicChannel();

            for (int i = 4; i < 39; i++) {
                ResponseAPDU responseReadData = readNfcTag((byte) i, (byte) 4, channel);
                StringBuilder sb = new StringBuilder();
                for (byte b : responseReadData.getBytes())
                    sb.append(String.format("%02X ", b));
                System.out.println(sb.toString());
            }

            writeNfcTagUrl("bbc.co.uk/news", channel);
        }
    }

    public void writeNfcTagUrl(String url, CardChannel channel) throws Exception {
        HexUtils hexUtils = new HexUtils();
        byte[] paddedBytes = hexUtils.getBytesWithPadding(url);

        initialiseUrlData(paddedBytes.length, channel);

        System.out.println(paddedBytes.length);
        for (int byteIndex = 0, pageAddress = 7; byteIndex <= paddedBytes.length - 4; byteIndex += 4, pageAddress += 1) {
            byte[] writeData = {
                    (byte) 0xFF,
                    (byte) 0xD6,
                    0x00,
                    (byte) pageAddress,
                    0x04,
                    paddedBytes[byteIndex],
                    paddedBytes[byteIndex + 1],
                    paddedBytes[byteIndex + 2],
                    paddedBytes[byteIndex + 3]
            };
            CommandAPDU writeDataCommand = new CommandAPDU(writeData);
            ResponseAPDU responseAPDU = channel.transmit(writeDataCommand);
            System.out.println(responseAPDU.getSW1() + " " + responseAPDU.getSW2());
        }
    }

    private void initialiseUrlData(int numberOfBytes, CardChannel channel) throws Exception {
        byte[] writeData1 = {(byte) 0xFF, (byte) 0xD6, 0x00, (byte) 0x04, 0x04, (byte) 0x01, (byte) 0x03, (byte) 0xA0, (byte) 0x0C};
        byte[] writeData2 = {(byte) 0xFF, (byte) 0xD6, 0x00, (byte) 0x05, 0x04, (byte) 0x34, (byte) 0x03, (byte) (numberOfBytes + 4), (byte) 0xD1};
        byte[] writeData3 = {(byte) 0xFF, (byte) 0xD6, 0x00, (byte) 0x06, 0x04, (byte) 0x01, (byte) numberOfBytes, (byte) 0x55, (byte) 0x02};

        CommandAPDU writeDataCommand = new CommandAPDU(writeData1);
        channel.transmit(writeDataCommand);
        writeDataCommand = new CommandAPDU(writeData2);
        channel.transmit(writeDataCommand);
        writeDataCommand = new CommandAPDU(writeData3);
        channel.transmit(writeDataCommand);
    }

    public ResponseAPDU readNfcTag(byte block, byte numberOfBytes, CardChannel channel) throws Exception {
        byte[] readData = {(byte) 0xFF, (byte) 0xB0, 0x00, block, numberOfBytes};
        CommandAPDU readDataCommand = new CommandAPDU(readData);
        ResponseAPDU responseReadData = channel.transmit(readDataCommand);

        return responseReadData;
    }
}
