package com.ractoc.mblock.communication.example;

import com.ractoc.mblock.communication.*;
import purejavacomm.CommPortIdentifier;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class RunClient {


    private Sender sender;

    public static void main(String[] args) {
        RunClient client = new RunClient();
        client.go();
    }

    /**
     * Sets up the sender to enable sending of the commands, then starts the actual program. Finally, when the program is done, shuts down the sender.
     */
    private void go() {
        List<CommPortIdentifier> portIds = CommunicationUtil.getComPorts().collect(Collectors.toList());
// in practical applications you would probably not hard code the Comm port like this.
        for (CommPortIdentifier portId : portIds) {
            if (portId.getName().equals("COM3")) {
                try {
                    sender = CommunicationUtil.openConnection(portId, new LightSensorListener());
                    sender.start();
                    runProgram();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    sender.stop();
                }
            }
        }
    }

    /**
     * Runs the actual example program.
     */
    private void runProgram() {
        sender.sendCommand(playNote((byte) 124, (byte) 124));
        // movement has been tested, but has been disabled for now.
        //sender.sendCommand(moveForward((byte) 180));
        for (int i = 0; i < 100; i++) {
            sender.sendCommand(sensor((byte) 1));
            // This interval is double the interval in the makeblock script. This seems to work best.
            // By reducing the interval in the makeblock script, it is also possible to reduce this interval.
            // keep in mind that this will in crease the load on the ranger, and thus drain the batteries faster.
            interval(0.4);
        }
        sender.sendCommand(moveStop());
        sender.sendCommand(setAllOnboardLEDsSingleColor((byte) 0));
        // final 1 second interval to make sure all commands are finished before shutting down.
        interval(1);
    }

    /**
     * Sleep interval in seconds. This is done in seconds to keep it in sync with the scripts in MakeBlock
     *
     * @param seconds Sleep interval in seconds.
     */
    private void interval(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * To send a color for the onboard LED's, one LED requires 3 color codes (RGB).
     * Here they are simply all set to the same value to generate a uniform color.
     *
     * @param color The color of all the LED's.
     * @return The Command which can be send to the robot.
     */
    private Command setAllOnboardLEDsSingleColor(byte color) {
        // to send a color for the onboard LED's, one LED requires 3 color codes (RGB).
        // here they are simply all set to the same value to generate a uniform color.
        return new Command((byte) 1, new byte[]{color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color, color});
    }

    /**
     * To play a note, both the note and the beat need to be sent to the robot.
     *
     * @param note The note as used in the corresponding block in makeblock
     * @param beat The beat as used in the corresponding block in makeblock
     * @return The Command which can be send to the robot.
     */
    private Command playNote(byte note, byte beat) {
        return new Command((byte) 2, new byte[]{note, beat});
    }

    /**
     * Creates the command to stop moving.
     *
     * @return The Command which can be send to the robot.
     */
    private Command moveStop() {
        return new Command((byte) 3, new byte[]{(byte) 0});
    }

    /**
     * Creates the command to move forward
     *
     * @param speed The speed with which to move forward
     * @return The Command which can be send to the robot.
     */
    private Command moveForward(byte speed) {
        return new Command((byte) 3, new byte[]{(byte) 1, speed});
    }

    /**
     * Creates a command to read one of the sensors. The response can then be handled by a IncomingMessageListeners.
     * {@link LightSensorListener}
     *
     * @param sensor The sensor to read
     * @return The Command which can be send to the robot.
     */
    private Command sensor(byte sensor) {
        return new Command((byte) 10, new byte[]{sensor});
    }

    /**
     * Processes an incoming light sensor response
     */
    private class LightSensorListener implements IncomingMessageListener {
        /**
         * Only handle the incoming response if the command is SENSOR and the sub-command is LIGHT
         *
         * @param response The incoming response
         */
        @Override
        public void responseReceived(Response response) {
            System.out.println(response.getCommand());
            if ("SENSOR".equals(response.getCommand()) && response.getData().length > 0 && "LIGHT".equals(response.getData()[0])) {
                processLightSensorCommand(response.getData());
            }
        }

        /**
         * Processesthe data portion of the response. In this case, turns the lights on or off depending on the light intensity.
         *
         * @param data The response payload.
         */
        private void processLightSensorCommand(String[] data) {
            System.out.println("light intensity: " + data[1]);
            if (Integer.parseInt(data[1]) < 400) {
                System.out.println("turn on");
                sender.sendCommand(setAllOnboardLEDsSingleColor((byte) 100));
            }
            if (Integer.parseInt(data[1]) > 900) {
                System.out.println("turn off");
                sender.sendCommand(setAllOnboardLEDsSingleColor((byte) 0));
            }
        }
    }
}
