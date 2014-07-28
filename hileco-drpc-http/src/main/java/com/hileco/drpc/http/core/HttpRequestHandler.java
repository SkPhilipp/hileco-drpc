package com.hileco.drpc.http.core;

import java.io.IOException;

/**
 * @author Philipp Gayret
 */
public interface HttpRequestHandler {

    public void handle(HttpRequest httpRequest) throws IOException;

}
