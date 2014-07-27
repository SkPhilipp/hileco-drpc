package com.hileco.drpc.http.core;

import com.hileco.drpc.core.stream.ArgumentsStreamer;
import com.hileco.drpc.core.stream.JSONArgumentsStreamer;

/**
 * Any constants for routing paths.
 *
 * @author Philipp Gayret
 */
public class HttpConstants {

    public static final ArgumentsStreamer DEFAULT_STREAMER = new JSONArgumentsStreamer();
    public static final String HDRPC_CONSUMER_PATH = "hdrpc";

}
