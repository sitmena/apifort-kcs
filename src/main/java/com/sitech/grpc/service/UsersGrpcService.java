package com.sitech.grpc.service;

import com.sitech.mapper.DtoMapper;
import com.sitech.service.TokenService;
import com.sitech.service.UserService;
import com.sitech.users.*;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


@GrpcService
@Slf4j
public class UsersGrpcService implements com.sitech.users.UserService {

    @Inject
    UserService userService;
    @Inject
    TokenService tokenService;
    @Inject
    DtoMapper dtoMapper;

    @Override
    public Uni<UserResponse> addUser(AddUserRequest request)  {
        log.debug(".... Add User = {} ", request.toString());
        Response userResponse = userService.addUser(request);
        return getUserByUserName(GetUserByUserNameRequest.newBuilder().setRealmName(request.getRealmName()).setUserName(request.getUserName()).build());

    }

    @Override
    public Uni<UserResponse> getUserById(GetUserByIdRequest request) {
        UserRepresentation result = userService.getUserById(request.getRealmName(), request.getUserId());
        return Uni.createFrom().item(() -> UserResponse.newBuilder().setUserDto(dtoMapper.toUser(result)).build());
    }

    @Override
    public Uni<UserResponse> getUserByUserName(GetUserByUserNameRequest request) {
        UserRepresentation result = userService.getUserByUserName(request.getRealmName(), request.getUserName());
        return Uni.createFrom().item(() -> UserResponse.newBuilder().setUserDto(dtoMapper.toUser(result)).build());
    }

    @Override
    public Uni<UserResponse> getUserByEmail(GetUserByEmailRequest request) {
        UserRepresentation result = userService.getUserByEmail(request.getRealmName(), request.getUserEmail());
        return Uni.createFrom().item(() -> UserResponse.newBuilder().setUserDto(dtoMapper.toUser(result)).build());
    }


    @Override
    public Uni<GetUserGroupsResponse> getUserGroups(GetUserGroupRequest request) {
        List<GroupRepresentation> groupRepresentations = userService.getUserGroups(request.getRealmName(), request.getUserId());
        return Uni.createFrom().item(() -> GetUserGroupsResponse.newBuilder().addAllGroupDto(dtoMapper.toGroupList(groupRepresentations)).build());
    }

    @Override
    public Uni<GetUserRoleResponse> getUserRoleEffective(UserRoleRequest request) {
        List<RoleRepresentation> roleRepresentations = userService.getUserRoleEffective(request.getRealmName(), request.getUserId());
        return Uni.createFrom().item(() -> GetUserRoleResponse.newBuilder().addAllRoleDto(dtoMapper.toRoleList(roleRepresentations)).build());
    }

    @Override
    public Uni<com.sitech.users.GetUserRoleResponse> getUserRoleAvailable(UserRoleRequest request) {
        List<RoleRepresentation> roleRepresentations = userService.getUserRoleAvailable(request.getRealmName(), request.getUserId());
        return Uni.createFrom().item(() -> GetUserRoleResponse.newBuilder().addAllRoleDto(dtoMapper.toRoleList(roleRepresentations)).build());
    }

    @Override
    public Uni<UsersResponse> findAllUsersInGroup(UserGroupRequest request) {
        List<UserRepresentation> userRepresentations = userService.findAllUsersInGroup(request.getRealmName(), request.getGroupName());
        return Uni.createFrom().item(() -> UsersResponse.newBuilder().addAllUserDto(dtoMapper.toUserList(userRepresentations)).build());
    }

    @Override
    public Uni<StatusReplay> addUserToGroup(AddUserGroupRequest request) {
        String status = userService.addUserToGroup(request.getRealmName(), request.getUserName(), request.getGroupName());
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).build());
    }

    @Override
    public Uni<UsersResponse> findUserByRole(FindUserRoleRequest request) {
        Collection<UserRepresentation> userRepresentations = userService.findUserByRole(request.getRealmName(), request.getRoleName());
        List newUserRepresentations = new ArrayList(userRepresentations);
        return Uni.createFrom().item(() -> UsersResponse.newBuilder().addAllUserDto(dtoMapper.toUserList(newUserRepresentations)).build());
    }

    @Override
    public Uni<com.sitech.users.StatusReplay> addUserRole(com.sitech.users.AddUserRoleRequest request) {
        String status = userService.addUserRole(request.getRealmName(), request.getUserName(), request.getRoleName());
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).setResponseMessage("").build());
    }

    @Override
    public Uni<UserResponse> updateUser(UpdateUserRequest updateUserRequest) {
        UserRepresentation response = userService.updateUser(updateUserRequest);
        return Uni.createFrom().item(() -> UserResponse.newBuilder().setUserDto(dtoMapper.toUser(response)).build());
    }

    @Override
    public Uni<StatusReplay> updateUserPassword(UpdateUserPasswordRequest request) {
        String status = userService.updateUserPassword(request);
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).build());
    }

    @Override
    public Uni<StatusReplay> resetUserPassword(ResetUserPasswordRequest request) {
        AccessTokenResponse token = tokenService.getUserAccessToken(request.getRealmName(), request.getUserName(), request.getOldPassword());
        if (!Objects.isNull(token)) {
            UpdateUserPasswordRequest updateUserPasswordRequest = UpdateUserPasswordRequest.newBuilder().setRealmName(request.getRealmName()).setUserId(request.getUserId()).setPassword(request.getNewPassword()).build();
            return updateUserPassword(updateUserPasswordRequest);
        }
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode("401").setResponseMessage("Old Password is Invalid").build());
    }

    @Override
    public Uni<UsersResponse> findAllUsersInRealm(GetUsersRequest request) {
        List<UserRepresentation> userRepresentations = userService.getAllUsersByRealm(request);
        List newUserRepresentations = new ArrayList(userRepresentations);
        return Uni.createFrom().item(() -> UsersResponse.newBuilder().addAllUserDto(dtoMapper.toUserList(newUserRepresentations)).build());
    }

    @Override
    public Uni<UserResponse> updateUserAttributes(updateUserAttributesRequest request) {
        UserRepresentation response = userService.updateUserAttributes(request);
        return Uni.createFrom().item(() -> UserResponse.newBuilder().setUserDto(dtoMapper.toUser(response)).build());
    }

    @Override
    public Uni<UserStatusResponse> killUserSession(DeleteUserSessionRequest request) {
        userService.killUserSession(request);
        return Uni.createFrom().item(() -> UserStatusResponse.newBuilder().setStatus(200).build());
    }

    @Override
    public Uni<StatusReplay> sendVerificationLink(SendVerificationLinkRequest request) {
        String status = userService.sendVerificationLink(request);
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).build());
    }

    @Override
    public Uni<StatusReplay> sendResetPassword(SendResetPasswordRequest request) {
        String status = userService.sendResetPassword(request);
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).build());
    }

    @Override
    public Uni<UserLoginResponse> userLogin(UserLoginRequest request) {
        AccessTokenResponse token = userService.getUserLogin(request);
        return Uni.createFrom().item(() -> UserLoginResponse.newBuilder()
                .setAccessToken(token.getToken())
                .setExpiresIn(token.getExpiresIn())
                .setRefreshExpiresIn(token.getRefreshExpiresIn())
                .setRefreshToken(token.getRefreshToken())
                .setTokenType(token.getTokenType())
                .setNotBeforePolicy(token.getNotBeforePolicy())
                .setSessionState(token.getSessionState())
                .setScope(token.getScope())
                .build());
    }

}
