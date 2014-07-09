package com.hileco.lib.service;

import com.hileco.lib.service.exceptions.EmbeddedServerStartException;

public interface LocalServer {

    public void start() throws EmbeddedServerStartException;

}
