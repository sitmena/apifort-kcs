package com.sitech.grpc.service;

import com.sitech.mapper.DtoMapper;
import com.sitech.service.TokenService;
import com.sitech.token.UserAccessTokenRequest;
import com.sitech.token.UserAccessTokenResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import javax.inject.Inject;

@GrpcService
public class TokenGrpcService implements com.sitech.token.TokenService {

    @Inject
    TokenService tokenService;
    @Inject
    DtoMapper dtoMapper;

    @Override
    public Uni<UserAccessTokenResponse> getUserToken(UserAccessTokenRequest request) {
        return Uni.createFrom().item(() -> UserAccessTokenResponse.newBuilder().setUserAccessTokenDto(
                dtoMapper.toUserAccessTokenDto(tokenService.getAccessToken(
                        request.getUserName(), request.getUserPass())
                )
        ).build());
    }
}
