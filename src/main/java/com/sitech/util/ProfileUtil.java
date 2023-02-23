package com.sitech.util;

import com.sitech.exception.DataConflictException;
import com.sitech.exception.ErrorResponse;
import com.sitech.exception.ResourceNotFoundException;
import com.sitech.oidc.keycloak.ServerConnection;
import io.quarkus.security.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.RealmResource;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@ApplicationScoped
public class ProfileUtil {

    public static final String REALM = "Realm ";
    @Inject
    protected  ServerConnection connection;

    public List<String> stringToList(String value) {
        String[] strSplit = value.split(",");
        ArrayList<String> strList = new ArrayList<>(Arrays.asList(strSplit));
        return strList;
    }

    public RealmResource getRealmByName(String realmName) {
        RealmResource realmResource = connection.getInstance().realm(realmName);
        try{
            if(Objects.isNull(realmResource.toRepresentation().getRealm())){
                log.error(REALM.concat(realmName).concat(" Not Found"));
            }
        } catch (Exception ex) {
            exceptionHandler(404, REALM.concat(realmName).concat(" Not Found"));
        }
        return realmResource;
    }

    protected void exceptionHandler(int statusCode, String exceptionMessage) {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setCode(statusCode);
        errorResponse.setErrorMessage(exceptionMessage);
        switch (statusCode) {
            case 401:
                throw new UnauthorizedException(errorResponse.toString());
            case 404:
                throw new ResourceNotFoundException(errorResponse.toString());
            case 409:
                throw new DataConflictException(errorResponse.toString());
            default:
                throw new InternalServerErrorException(errorResponse.toString());
        }
    }
}
