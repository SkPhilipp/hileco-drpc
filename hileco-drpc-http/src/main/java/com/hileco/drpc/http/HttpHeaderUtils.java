package com.hileco.drpc.http;

import com.hileco.drpc.core.spec.Metadata;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Methods for reading and writing Metadata to and from transport level (usually HTTP) headers.
 *
 * @author Philipp Gayret
 */
public class HttpHeaderUtils {

    // Message metadata header names
    public static final String HDRPC_HEADER_META_ID = "H-Id";
    public static final String HDRPC_HEADER_META_TYPE = "H-Type";
    public static final String HDRPC_HEADER_META_REPLY_TO = "H-ReplyTo";
    public static final String HDRPC_HEADER_META_SERVICE = "H-Service";
    public static final String HDRPC_HEADER_META_OPERATION = "H-Operation";
    public static final String HDRPC_HEADER_META_EXPECTING_RESPONSE = "H-Respond";

    /**
     * Constructs a {@link com.hileco.drpc.core.spec.Metadata} object out of transport headers.
     *
     * @param headerFunction request with headers set properly
     * @return parsed metadata
     */
    public static Metadata fromHeaders(Function<String, String> headerFunction) {
        Metadata.Type type = Metadata.Type.valueOf(headerFunction.apply(HDRPC_HEADER_META_TYPE));
        String id = headerFunction.apply(HDRPC_HEADER_META_ID);

        switch (type) {
            case CALLBACK: {
                String replyTo = headerFunction.apply(HDRPC_HEADER_META_REPLY_TO);
                Metadata metadata = new Metadata(id, replyTo);
                metadata.setExpectResponse(Boolean.parseBoolean(headerFunction.apply(HDRPC_HEADER_META_EXPECTING_RESPONSE)));
                return metadata;
            }
            case SERVICE: {
                String service = headerFunction.apply(HDRPC_HEADER_META_SERVICE);
                String operation = headerFunction.apply(HDRPC_HEADER_META_OPERATION);
                Metadata metadata = new Metadata(id, service, operation);
                metadata.setExpectResponse(Boolean.parseBoolean(headerFunction.apply(HDRPC_HEADER_META_EXPECTING_RESPONSE)));
                return metadata;
            }
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
    public static void writeHeaders(Metadata metadata, BiConsumer<String, String> headerConsumer) {
        switch (metadata.getType()) {
            case SERVICE:
                headerConsumer.accept(HDRPC_HEADER_META_TYPE, metadata.getType().toString());
                headerConsumer.accept(HDRPC_HEADER_META_ID, metadata.getId());
                headerConsumer.accept(HDRPC_HEADER_META_SERVICE, metadata.getService());
                headerConsumer.accept(HDRPC_HEADER_META_OPERATION, metadata.getOperation());
                headerConsumer.accept(HDRPC_HEADER_META_EXPECTING_RESPONSE, Boolean.toString(metadata.getExpectResponse()));
                break;
            case CALLBACK:
                headerConsumer.accept(HDRPC_HEADER_META_TYPE, metadata.getType().toString());
                headerConsumer.accept(HDRPC_HEADER_META_ID, metadata.getId());
                headerConsumer.accept(HDRPC_HEADER_META_REPLY_TO, metadata.getReplyTo());
                headerConsumer.accept(HDRPC_HEADER_META_EXPECTING_RESPONSE, Boolean.toString(metadata.getExpectResponse()));
                break;
        }
    }

}
