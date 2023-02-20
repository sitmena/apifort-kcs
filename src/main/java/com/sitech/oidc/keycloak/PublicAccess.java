package com.sitech.oidc.keycloak;

import com.sitech.exception.DataConflictException;
import com.sitech.exception.ErrorResponse;
import com.sitech.exception.ResourceNotFoundException;
import io.quarkus.security.UnauthorizedException;
import org.keycloak.representations.idm.KeysMetadataRepresentation;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.InternalServerErrorException;
import java.util.List;

@ApplicationScoped
public class PublicAccess {

    @Inject
    ServerConnection connection;

    public String getRealmPublicKey(String realName) {
        List<KeysMetadataRepresentation.KeyMetadataRepresentation> keys = connection.getInstance().realm(realName).keys().getKeyMetadata().getKeys();
        for (KeysMetadataRepresentation.KeyMetadataRepresentation key : keys) {
            if (key.getType().equals("RSA") && key.getUse().name().equals("SIG")) {
                return key.getPublicKey();
            }
        }
        return null;
    }


    public String getRealmCertificate(String realName) {
        List<KeysMetadataRepresentation.KeyMetadataRepresentation> keys = connection.getInstance().realm(realName).keys().getKeyMetadata().getKeys();
        for (KeysMetadataRepresentation.KeyMetadataRepresentation key : keys) {
            if (key.getType().equals("RSA") && key.getUse().name().equals("SIG")) {
                return key.getCertificate();
            }
        }
        return null;
    }
}
