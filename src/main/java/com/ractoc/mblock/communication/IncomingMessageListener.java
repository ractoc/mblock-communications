package com.ractoc.mblock.communication;

/**
 * Listens to incoming {@link Response}
 */
public interface IncomingMessageListener {
    /**
     * Processses the incoming {@link Response}
     * @param response The incoming {@link Response}
     */
    void responseReceived(Response response);
}
