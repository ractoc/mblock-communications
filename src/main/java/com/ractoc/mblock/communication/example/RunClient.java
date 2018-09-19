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

    public static void main(String[] args) {
        RunClient client = new RunClient();
        client.go();
    }

    private void go() {
        List<CommPortIdentifier> portIds = Connection.getComPorts().filter((portId) -> portId.getPortType() == CommPortIdentifier.PORT_SERIAL).collect(Collectors.toList());

        for (CommPortIdentifier portId : portIds) {
            if (portId.getName().equals("COM4")) {
                try(Connection connection = new Connection(portId, this)) {
                    connection.writeCommand(new Command((byte) 1, new byte[]{100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100, 100}));
                    connection.writeCommand(new Command((byte) 2, new byte[]{124, 124}));
                    Thread.sleep(10000);
                    connection.writeCommand(new Command((byte) 1, new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
                    Thread.sleep(10000);
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void responseReceived(Response response) {
        System.out.println(response.getCommand() + "=" + response.getData().reduce(String::concat).orElseGet(()->"no content"));
    }
}
