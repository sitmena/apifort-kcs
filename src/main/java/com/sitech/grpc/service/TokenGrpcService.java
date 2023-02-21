package com.sitech.grpc.service;

import com.sitech.mapper.DtoMapper;
import com.sitech.service.TokenService;
import com.sitech.token.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;

@GrpcService
@Slf4j
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

    @Override
    public Uni<UserAccessTokenResponse> loginByUserCredentials(LoginByUserCredentialsRequest request) {
        log.debug(">>>>>>>>> {} " , request.toString());
        return Uni.createFrom().item(() -> UserAccessTokenResponse.newBuilder().setUserAccessTokenDto(
                dtoMapper.toUserAccessTokenDto(tokenService.loginByUserCredentials(request)
                )
        ).build());
    }

    @Override
    public Uni<UserAccessTokenResponse> loginByServiceCredentials(LoginByServiceCredentialsRequest request) {
        log.debug(">>>>>>>>> {} " , request.toString());
        return Uni.createFrom().item(() -> UserAccessTokenResponse.newBuilder().setUserAccessTokenDto(
                dtoMapper.toUserAccessTokenDto(tokenService.loginByServiceCredentials(request)
                )
        ).build());
    }

    @Override
    public Uni<UserAccessTokenResponse> refreshToken(RefreshTokenRequest request) {
        log.debug(">>>>>> {} " , request.toString());
        return Uni.createFrom().item(() -> UserAccessTokenResponse.newBuilder().setUserAccessTokenDto(
                dtoMapper.toUserAccessTokenDto(tokenService.refreshToken(request))).build());
    }


}
