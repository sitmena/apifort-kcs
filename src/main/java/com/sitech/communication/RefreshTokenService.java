package com.sitech.communication;


import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("/realms/{realm}/protocol/openid-connect/token")
@RegisterRestClient
public interface RefreshTokenService {

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    Response refreshToken( @PathParam("realm") String realm , MultivaluedMap<String, String> multivaluedMap);
}
