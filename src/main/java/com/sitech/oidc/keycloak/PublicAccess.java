package com.sitech.oidc.keycloak;

import com.sitech.exception.DataConflictException;
import com.sitech.exception.ErrorResponse;
import com.sitech.exception.ResourceNotFoundException;
import io.quarkus.security.UnauthorizedException;
import org.keycloak.representations.idm.KeysMetadataRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;

@ApplicationScoped
public class PublicAccess {

    @Inject
    ServerConnection connection;

    private static final Logger log = LoggerFactory.getLogger(PublicAccess.class);

    public String getRealmPublicKey(String realmName) {

        try {
            List<KeysMetadataRepresentation.KeyMetadataRepresentation> keys = connection.getInstance().realm(realmName).keys().getKeyMetadata().getKeys();
            for (KeysMetadataRepresentation.KeyMetadataRepresentation key : keys) {
                if (key.getType().equals("RSA") && key.getUse().name().equals("SIG")) {
                    return key.getPublicKey();
                }
            }
        } catch (Exception ex) {
            exceptionHandler(404, "Realm ".concat(realmName).concat(" Dose Not Exist"));
        }
        return null;
    }


    public String getRealmCertificate(String realmName) {
        try {
            List<KeysMetadataRepresentation.KeyMetadataRepresentation> keys = connection.getInstance().realm(realmName).keys().getKeyMetadata().getKeys();
            for (KeysMetadataRepresentation.KeyMetadataRepresentation key : keys) {
                if (key.getType().equals("RSA") && key.getUse().name().equals("SIG")) {
                    return key.getCertificate();
                }
            }
        } catch (Exception ex) {
            exceptionHandler(404, "Realm ".concat(realmName).concat(" Dose Not Exist"));
        }
        return null;
    }


    private void exceptionHandler(int statusCode, String exceptionMessage) {
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
