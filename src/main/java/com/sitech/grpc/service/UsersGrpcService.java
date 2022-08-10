package com.sitech.grpc.service;

import com.sitech.mapper.DtoMapper;
import com.sitech.service.UserService;
import com.sitech.users.AddUserRequest;
import com.sitech.users.GetUserByUserNameRequest;
import com.sitech.users.GetUserGroupsResponse;
import com.sitech.users.UserResponse;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.sitech.users.GetUserRoleResponse;
import com.sitech.users.UsersResponse;
import com.sitech.users.StatusReplay;
import com.sitech.users.FindUserRoleRequest;
import com.sitech.users.GetUserGroupRequest;
import com.sitech.users.UserRoleRequest;
import com.sitech.users.UserGroupRequest;
import com.sitech.users.AddUserGroupRequest;

@GrpcService
public class UsersGrpcService implements com.sitech.users.UserService {

    @Inject
    UserService userService;
    @Inject
    DtoMapper dtoMapper;
    private static final Logger log = LoggerFactory.getLogger(RealmGrpcService.class);

    @Override
    public Uni<UserResponse> addUser(AddUserRequest request) {
        userService.addUser(request);
        return getUserByUserName(GetUserByUserNameRequest.newBuilder().setRealmName(request.getRealmName()).setUserName(request.getUserName()).build());
    }

    @Override
    public Uni<UserResponse> getUserByUserName(GetUserByUserNameRequest request) {
        UserRepresentation result = userService.getUserByUserName(request.getRealmName(),request.getUserName());
        return Uni.createFrom().item(() -> UserResponse.newBuilder().setUserDto(dtoMapper.toUser(result)).build());
    }

    @Override
    public Uni<GetUserGroupsResponse> getUserGroups(GetUserGroupRequest request) {
        List<GroupRepresentation> groupRepresentations = userService.getUserGroups(request.getRealmName(),request.getUserId());
        return Uni.createFrom().item(() -> GetUserGroupsResponse.newBuilder().addAllGroupDto(dtoMapper.toGroupList(groupRepresentations)).build());
    }

    @Override
    public Uni<GetUserRoleResponse> getUserRoleEffective(UserRoleRequest request) {
        List<RoleRepresentation> roleRepresentations = userService.getUserRoleEffective(request.getRealmName() , request.getUserId());
        return Uni.createFrom().item(() -> GetUserRoleResponse.newBuilder().addAllRoleDto(dtoMapper.toRoleList(roleRepresentations)).build());
    }

    @Override
    public Uni<com.sitech.users.GetUserRoleResponse> getUserRoleAvailable(UserRoleRequest request) {
        List<RoleRepresentation> roleRepresentations =  userService.getUserRoleAvailable(request.getRealmName() , request.getUserId());
        return Uni.createFrom().item(() -> GetUserRoleResponse.newBuilder().addAllRoleDto(dtoMapper.toRoleList(roleRepresentations)).build());
    }

    @Override
    public Uni<UsersResponse> findAllUsersInGroup(UserGroupRequest request) {
        List<UserRepresentation> userRepresentations = userService.findAllUsersInGroup(request.getRealmName(),request.getGroupName());
        return Uni.createFrom().item(() -> UsersResponse.newBuilder().addAllUserDto(dtoMapper.toUserList(userRepresentations)).build());
    }

    @Override
    public Uni<StatusReplay> addUserToGroup(AddUserGroupRequest request) {
        String status = userService.addUserToGroup(request.getRealmName(),request.getUserName(), request.getGroupName());
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).build());
    }

    @Override
    public Uni<UsersResponse> findUserByRole(FindUserRoleRequest request) {
        Collection<UserRepresentation> userRepresentations = userService.findUserByRole(request.getRealmName(),request.getRoleName());
        List newUserRepresentations = new ArrayList(userRepresentations);
        return Uni.createFrom().item(() -> UsersResponse.newBuilder().addAllUserDto(dtoMapper.toUserList(newUserRepresentations)).build());

    }

    @Override
    public Uni<com.sitech.users.StatusReplay> addUserRole(com.sitech.users.AddUserRoleRequest request) {
        String status = userService.addUserRole(request.getRealmName(),request.getUserName(), request.getRoleName());
        return Uni.createFrom().item(() -> StatusReplay.newBuilder().setStatusCode(status).build());
    }

}
