package org.ballerinalang.integration.serveragent;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ServiceStartExceptionMapper implements ExceptionMapper<ServiceStartException> {
    @Override
    public Response toResponse(ServiceStartException e) {
        return Response.status(500).entity("Unable to start Ballerina Service at port 9090").type("text/plain").build();
    }
}
