package com.sitech.grpc.service;

import com.sitech.access.key.PublicKeyReplay;
import com.sitech.access.key.PublicKeyRequest;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import javax.inject.Inject;

@GrpcService
public class KeysService implements com.sitech.access.key.KeysService {


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
