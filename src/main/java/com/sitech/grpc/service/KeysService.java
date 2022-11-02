package com.sitech.grpc.service;

import com.sitech.access.key.PublicKeyReplay;
import com.sitech.access.key.PublicKeyRequest;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;

@GrpcService
public class KeysService implements com.sitech.access.key.KeysService {


    private static final Logger log = LoggerFactory.getLogger(KeysService.class);
    @Inject
    com.sitech.service.KeysService keysService;

    @Override
    public Uni<PublicKeyReplay> getPublicKey(PublicKeyRequest request) {
        return Uni.createFrom().item(() -> com.sitech.access.key.PublicKeyReplay.newBuilder().setValue(keysService.getPublicKey(request.getRealmName())).build());
    }

    @Override
    public Uni<PublicKeyReplay> getCertificate(PublicKeyRequest request) {
        return Uni.createFrom().item(() -> com.sitech.access.key.PublicKeyReplay.newBuilder().setValue(keysService.getRealmCertificate(request.getRealmName())).build());
    }
}
