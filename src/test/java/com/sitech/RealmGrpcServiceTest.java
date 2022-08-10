package com.sitech;


import com.google.protobuf.Empty;
import com.sitech.dto.Dto;
import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import com.sitech.realm.RealmService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.subscription.Cancellable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class RealmGrpcServiceTest {

    @GrpcClient
    RealmService realmServiceGrpc;
    private static final Logger log = LoggerFactory.getLogger(RealmGrpcServiceTest.class);

    @Test
    public Uni<List<Dto.RealmDto>> getRealms(){
        return realmServiceGrpc.getRealms(Empty.newBuilder().build()).onItem().transform( r -> r.getRealmDtoList());
    }


    @Test
    void shouldReturnRealms() {
        Uni<Object> result = realmServiceGrpc.getRealms(Empty.newBuilder().build()).onItem().transform(r -> r.getRealmDtoList());
        log.info(" ............ {} ", result.onItem().ifNotNull().toString());
    }
}
