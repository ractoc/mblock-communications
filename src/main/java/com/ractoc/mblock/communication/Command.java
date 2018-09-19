package com.ractoc.mblock.communication;

public final class Command {

    private final byte command;
    private final byte[] data;

    public Command(byte command, byte[] data) {
        this.command = command;
        this.data = data;
    }

    public byte getCommand() {
        return command;
    }

    public byte[] getData() {
        return data;
    }
}
