package com.sitech.grpc.service;

import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import com.sitech.service.KeysService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;

import javax.inject.Inject;

@GrpcService
public class PublicAccessGrpcService implements com.sitech.access.PublicAccessService {

    @Inject
    KeysService keysService;

    @Override
    public Uni<PublicKeyReplay> getPublicKey(PublicKeyRequest request) {
        return Uni.createFrom().item( () -> PublicKeyReplay.newBuilder().setValue(keysService.getPublicKey(request.getRealmName())).build());
    }

    @Override
    public Uni<PublicKeyReplay> getCertificate(PublicKeyRequest request) {
        return Uni.createFrom().item( () -> PublicKeyReplay.newBuilder().setValue(keysService.getRealmCertificate(request.getRealmName())).build());
    }

}
