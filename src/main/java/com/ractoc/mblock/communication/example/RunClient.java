package com.ractoc.mblock.communication.example;

import com.ractoc.mblock.communication.Command;
import com.ractoc.mblock.communication.Connection;
import com.ractoc.mblock.communication.IncomingMessageListener;
import com.ractoc.mblock.communication.Response;
import purejavacomm.CommPortIdentifier;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RunClient implements IncomingMessageListener {

    private Connection connection;

    public static void main(String[] args) {
        RunClient client = new RunClient();
        client.go();
    }

    private void go() {
        List<CommPortIdentifier> portIds = Connection.getComPorts().filter((portId) -> portId.getPortType() == CommPortIdentifier.PORT_SERIAL).collect(Collectors.toList());

        for (CommPortIdentifier portId : portIds) {
            if (portId.getName().equals("COM3")) {
                try {
                    connection = new Connection(portId, this);
                    runProgram();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void runProgram() throws IOException {
        connection.writeCommand(playNote((byte)124, (byte) 124));
        //connection.writeCommand(moveForward((byte) 180));
        for (int i=0;i<10;i++) {
            connection.writeCommand(sensor((byte) 1));
            interval(1000L);
        }
        connection.writeCommand(moveStop());
        connection.writeCommand(setAllOnboardLEDsSingleColor((byte)0));
        interval(1000L);
    }

    private void interval(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Command setAllOnboardLEDsSingleColor(byte color) {
        return new Command((byte) 1, new byte[]{color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color});
    }

    private Command playNote(byte note, byte beat) {
        return new Command((byte) 2, new byte[]{note, beat});
    }

    private Command moveStop() {
        return new Command((byte) 3, new byte[]{(byte) 0});
    }

    private Command moveForward(byte speed) {
        return new Command((byte) 3, new byte[]{(byte) 1, speed});
    }

    private Command sensor(byte sensor) {
        return new Command((byte) 10, new byte[]{sensor});
    }

    @Override
    public void responseReceived(Response response) {
        if ("SENSOR".equals(response.getCommand()) && response.getData().length > 0) {
            processSensorCommand(response.getData());
        } else {
            System.out.println(response.getCommand() + "=" + response.getData());
        }
    }

    private void processSensorCommand(String[] data) {
        if ("LIGHT".equals(data[0])) {
            System.out.println("light intensity: " + data[1]);
            if (Integer.parseInt(data[1]) < 200) {
                try {
                    connection.writeCommand(setAllOnboardLEDsSingleColor((byte)100));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (Integer.parseInt(data[1]) >800) {
                try {
                    connection.writeCommand(setAllOnboardLEDsSingleColor((byte)0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
