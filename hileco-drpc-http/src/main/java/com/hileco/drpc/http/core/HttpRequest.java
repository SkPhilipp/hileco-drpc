package com.hileco.drpc.http.core;

import java.io.InputStream;

/**
 * @author Philipp Gayret
 */
public interface HttpRequest {

    public InputStream getInputStream();

    public String getHeader(String name);

    public String getRemoteHost();

}
