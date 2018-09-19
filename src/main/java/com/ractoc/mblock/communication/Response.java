package com.ractoc.mblock.communication;

import java.util.stream.Stream;

public final class Response {
    private final byte command;
    private final Stream<String> data;

    public Response(byte command, Stream<String> data) {
        this.command = command;
        this.data = data;
    }

    public byte getCommand() {
        return command;
    }

    public Stream<String> getData() {
        return data;
    }
}
