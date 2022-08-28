package com.sitech.oidc.keycloak;

import org.keycloak.representations.idm.KeysMetadataRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PublicAccess {

    @Inject
    ServerConnection connection;

    private static final Logger log = LoggerFactory.getLogger(PublicAccess.class);

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
