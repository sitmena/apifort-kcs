package com.sitech;


import com.google.protobuf.Empty;
import com.sitech.dto.Dto;
import com.sitech.realm.RealmService;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
