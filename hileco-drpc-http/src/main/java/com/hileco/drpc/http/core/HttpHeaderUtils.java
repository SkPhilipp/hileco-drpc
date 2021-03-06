package com.hileco.drpc.http.core;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.hileco.drpc.core.spec.Metadata;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Methods for reading and writing Metadata to and from transport level (usually HTTP) headers.
 *
 * @author Philipp Gayret
 */
public class HttpHeaderUtils {

    private static final Joiner TARGET_JOINER = Joiner.on(",").skipNulls();

    // Message metadata header names
    public static final String HDRPC_HEADER_META_TARGETS = "H-Targets";
    public static final String HDRPC_HEADER_META_ID = "H-Id";
    public static final String HDRPC_HEADER_META_TYPE = "H-Type";
    public static final String HDRPC_HEADER_META_REPLY_TO = "H-ReplyTo";
    public static final String HDRPC_HEADER_META_SERVICE = "H-Service";
    public static final String HDRPC_HEADER_META_OPERATION = "H-Operation";
    public static final String HDRPC_HEADER_META_EXPECTING_RESPONSE = "H-Respond";

    public static final String ROUTER_REPLY_TO_HOST = "H-ReplyToHost";
    public static final String ROUTER_REPLY_TO_PORT = "H-ReplyToPort";

    /**
     * Constructs a {@link com.hileco.drpc.core.spec.Metadata} object out of transport headers.
     *
     * @param headerFunction request with headers set properly
     * @return parsed metadata
     */
    public static Metadata fromHeaders(Function<String, String> headerFunction) {

        Metadata.Type type = Metadata.Type.valueOf(headerFunction.apply(HDRPC_HEADER_META_TYPE));

        switch (type) {
            case CALLBACK: {
                String replyTo = headerFunction.apply(HDRPC_HEADER_META_REPLY_TO);
                String id = headerFunction.apply(HDRPC_HEADER_META_ID);
                return new Metadata(id, replyTo);
            }
            case SERVICE: {
                String targets = headerFunction.apply(HDRPC_HEADER_META_TARGETS);
                ArrayList<String> targetsList = targets == null ? null : Lists.newArrayList(targets.split(","));
                String id = headerFunction.apply(HDRPC_HEADER_META_ID);
                String service = headerFunction.apply(HDRPC_HEADER_META_SERVICE);
                String operation = headerFunction.apply(HDRPC_HEADER_META_OPERATION);
                return new Metadata(id, service, operation, targetsList);
            }
            default:
                throw new IllegalArgumentException("Unknown " + HDRPC_HEADER_META_TYPE + " value: " + headerFunction.apply(HDRPC_HEADER_META_TYPE));
        }

    }

    /**
     * Sends all metadata to the given consumer as key-value pairs of transport headers.
     *
     * @param metadata       metadata whose properties to use to supply values
     * @param headerConsumer consumer of key-value pairs of transport headers
     */
    public static void writeHeaders(Metadata metadata, BiConsumer<String, String> headerConsumer) {

        // generic headers

        if (metadata.hasTargets()) {
            headerConsumer.accept(HDRPC_HEADER_META_TARGETS, TARGET_JOINER.join(metadata.getTargets()));
        }
        headerConsumer.accept(HDRPC_HEADER_META_ID, metadata.getId());
        headerConsumer.accept(HDRPC_HEADER_META_EXPECTING_RESPONSE, Boolean.toString(metadata.getExpectResponse()));
        headerConsumer.accept(HDRPC_HEADER_META_TYPE, metadata.getType().toString());

        // type specific headers

        switch (metadata.getType()) {
            case SERVICE:
                headerConsumer.accept(HDRPC_HEADER_META_SERVICE, metadata.getService());
                headerConsumer.accept(HDRPC_HEADER_META_OPERATION, metadata.getOperation());
                break;
            case CALLBACK:
                headerConsumer.accept(HDRPC_HEADER_META_REPLY_TO, metadata.getReplyTo());
                break;
        }

    }

    public static String getReplyToHost(HttpRequest httpRequest) {
        String header = httpRequest.getHeader(HttpHeaderUtils.ROUTER_REPLY_TO_HOST);
        return header != null ? header : httpRequest.getRemoteHost();
    }

    public static Integer getReplyToPost(HttpRequest httpRequest) {
        String header = httpRequest.getHeader(HttpHeaderUtils.ROUTER_REPLY_TO_PORT);
        return header != null ? Integer.parseInt(header) : 8080;
    }

}
