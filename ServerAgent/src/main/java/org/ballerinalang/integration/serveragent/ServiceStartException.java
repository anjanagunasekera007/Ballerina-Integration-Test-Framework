package org.ballerinalang.integration.serveragent;

public class ServiceStartException extends Throwable {
    public ServiceStartException() {
        super();
    }

    public ServiceStartException(String message) {
        super(message);
    }

    public ServiceStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceStartException(Throwable cause) {
        super(cause);
    }
}
