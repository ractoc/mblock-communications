package com.ractoc.mblock.communication;

import com.ractoc.mblock.communication.example.RunClient;
import purejavacomm.CommPortIdentifier;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.stream.Stream;

public class Connection implements AutoCloseable {

    private SerialPort serialPort;

    public Connection(CommPortIdentifier portId, IncomingMessageListener listener) throws IOException {
        try {
            serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
            serialPort.addEventListener(new ConnectionListener(serialPort, listener));
            serialPort.notifyOnDataAvailable(true);
            serialPort.setSerialPortParams(115200,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        } catch (PortInUseException | IOException | UnsupportedCommOperationException | TooManyListenersException e) {
            throw new IOException("Unable to setup a connection to " + portId.getName(), e);
        }
    }

    public static Stream<CommPortIdentifier> getComPorts() {
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        return Collections.list(portList).stream();
    }

    public void writeCommand(Command command) throws IOException {
        OutputStream out = serialPort.getOutputStream();
        out.write(command.getCommand());
        out.write(command.getData());
        out.flush();
    }

    @Override
    public void close() {
        serialPort.close();
    }
}
