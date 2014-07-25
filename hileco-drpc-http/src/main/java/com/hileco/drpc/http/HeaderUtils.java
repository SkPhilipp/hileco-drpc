package com.hileco.drpc.http;

import com.hileco.drpc.core.spec.Metadata;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Methods for reading and writing Metadata to and from transport level (usually HTTP) headers.
 *
 * @author Philipp Gayret
 */
public class HeaderUtils {

    public static final String HDRPC_HEADER_META_ID = "META_ID";
    public static final String HDRPC_HEADER_META_TYPE = "META_TYPE";
    public static final String HDRPC_HEADER_META_REPLY_TO = "META_REPLY_TO";
    public static final String HDRPC_HEADER_META_SERVICE = "META_SERVICE";
    public static final String HDRPC_HEADER_META_OPERATION = "META_OPERATION";

    /**
     * Constructs a {@link com.hileco.drpc.core.spec.Metadata} object out of transport headers.
     *
     * @param headerFunction request with headers set properly
     * @return parsed metadata
     */
    public Metadata fromHeaders(Function<String, String> headerFunction) {
        Metadata.Type type = Metadata.Type.valueOf(headerFunction.apply(HDRPC_HEADER_META_TYPE));
        String id = headerFunction.apply(HDRPC_HEADER_META_ID);
        switch (type) {
            case CALLBACK:
                String replyTo = headerFunction.apply(HDRPC_HEADER_META_REPLY_TO);
                return new Metadata(id, replyTo);
            case SERVICE:
                String service = headerFunction.apply(HDRPC_HEADER_META_SERVICE);
                String operation = headerFunction.apply(HDRPC_HEADER_META_OPERATION);
                return new Metadata(id, service, operation);
            default:
                throw new IllegalArgumentException("Unknown " + HDRPC_HEADER_META_TYPE + " type: " + headerFunction.apply(HDRPC_HEADER_META_TYPE));
        }
    }

    /**
     * Sends all metadata to the given consumer as key-value pairs of transport headers.
     *
     * @param metadata       metadata whose properties to use to supply values
     * @param headerConsumer consumer of key-value pairs of transport headers
     */
    public void writeHeaders(Metadata metadata, BiConsumer<String, String> headerConsumer) {
        switch (metadata.getType()) {
            case SERVICE:
                headerConsumer.accept(HDRPC_HEADER_META_TYPE, metadata.getType().toString());
                headerConsumer.accept(HDRPC_HEADER_META_ID, metadata.getId());
                headerConsumer.accept(HDRPC_HEADER_META_SERVICE, metadata.getService());
                headerConsumer.accept(HDRPC_HEADER_META_OPERATION, metadata.getOperation());
                break;
            case CALLBACK:
                headerConsumer.accept(HDRPC_HEADER_META_TYPE, metadata.getType().toString());
                headerConsumer.accept(HDRPC_HEADER_META_ID, metadata.getId());
                headerConsumer.accept(HDRPC_HEADER_META_REPLY_TO, metadata.getReplyTo());
                break;
        }
    }

}
