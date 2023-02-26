package com.sitech.oidc.keycloak;

import com.sitech.util.ProfileUtil;
import org.keycloak.representations.idm.KeysMetadataRepresentation;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PublicAccess  extends ProfileUtil {

    public String getRealmPublicKey(String realName) {
        List<KeysMetadataRepresentation.KeyMetadataRepresentation> keys = getRealmByName(realName).keys().getKeyMetadata().getKeys();
        for (KeysMetadataRepresentation.KeyMetadataRepresentation key : keys) {
            if (key.getType().equals("RSA") && key.getUse().name().equals("SIG")) {
                return key.getPublicKey();
            }
        }
        return null;
    }


    public String getRealmCertificate(String realName) {
        List<KeysMetadataRepresentation.KeyMetadataRepresentation> keys = getRealmByName(realName).keys().getKeyMetadata().getKeys();
        for (KeysMetadataRepresentation.KeyMetadataRepresentation key : keys) {
            if (key.getType().equals("RSA") && key.getUse().name().equals("SIG")) {
                return key.getCertificate();
            }
        }
        return null;
    }
}
