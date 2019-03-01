package com.ractoc.mblock.communication;

import java.util.stream.Stream;

public final class Response {
    private final String command;
    private final String[] data;

    public Response(String command, String[] data) {
        this.command = command;
        this.data = data;
    }

    public String getCommand() {
        return command;
    }

    public String[] getData() {
        return data;
    }
}
