package com.ractoc.mblock.communication;

import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.SerialPortEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ConnectionListener implements SerialPortEventListener {

    private final IncomingMessageListener listener;
    private final InputStream inputStream;

    public ConnectionListener(SerialPort serialPort, IncomingMessageListener listener) throws IOException {
        inputStream = serialPort.getInputStream();
        this.listener = listener;
    }

    public void serialEvent(SerialPortEvent event) {
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
            case SerialPortEvent.DATA_AVAILABLE:
                readIncomingData();
                break;
        }
    }

    private void readIncomingData() {
        byte[] readBuffer = new byte[20];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()){
            while (inputStream.available() > 0) {
                inputStream.read(readBuffer);
                baos.write(readBuffer);
            }
            processResponse(baos.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processResponse(String response) {
        if (response.contains("|")) {
            String[] content = response.trim().split("\\|");
            byte command = Byte.parseByte(content[0].substring(0, content[0].lastIndexOf('.')));
            listener.responseReceived(new Response(command, Arrays.stream(content).skip(1)));
        }
    }
}
