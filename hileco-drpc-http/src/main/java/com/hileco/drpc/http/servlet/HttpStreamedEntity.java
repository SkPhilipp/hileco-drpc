package com.hileco.drpc.http.servlet;

import com.hileco.drpc.core.stream.ArgumentsStreamer;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HttpStreamedEntity implements HttpEntity {

    private final ArgumentsStreamer argumentsStreamer;
    private final Object[] object;

    public HttpStreamedEntity(ArgumentsStreamer argumentsStreamer, Object object[]) {
        this.argumentsStreamer = argumentsStreamer;
        this.object = object;
    }

    @Override
    public boolean isRepeatable() {
        return false;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    @Override
    public long getContentLength() {
        return -1;
    }

    @Override
    public Header getContentType() {
        return null;
    }

    @Override
    public Header getContentEncoding() {
        return null;
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        throw new IllegalStateException("HttpStreamedEntity can only be ");
    }

    @Override
    public void writeTo(OutputStream outstream) throws IOException {
        this.argumentsStreamer.serializeTo(outstream, object);
    }

    @Override
    public boolean isStreaming() {
        return false;
    }

    @Override
    public void consumeContent() throws IOException {

    }

}
