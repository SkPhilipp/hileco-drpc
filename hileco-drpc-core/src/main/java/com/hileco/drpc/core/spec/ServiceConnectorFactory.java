package com.hileco.drpc.core.spec;

/**
 * The connection to DRPC services.
 *
 * @author Philipp Gayret
 */
public interface ServiceConnectorFactory {

    /**
     * Constructs a {@link ServiceConnector} out of the given interface class, useable for RPC and DRPC.
     *
     * @param type any RPC compliant interface
     * @param <T>  network object type
     * @return the given class' {@link ServiceConnector}
     */
    public abstract <T> ServiceConnector<T> connector(Class<T> type);

}
