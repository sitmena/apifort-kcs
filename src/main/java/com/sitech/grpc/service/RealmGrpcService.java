package com.sitech.grpc.service;

import com.google.protobuf.Empty;
import com.sitech.dto.Dto;
import com.sitech.mapper.DtoMapper;
import com.sitech.realm.*;
import com.sitech.service.RealmService;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import org.keycloak.representations.idm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

@GrpcService
public class RealmGrpcService implements com.sitech.realm.RealmService {

    @Inject
    RealmService realmService;
    @Inject
    DtoMapper dtoMapper;
    private static final Logger log = LoggerFactory.getLogger(RealmGrpcService.class);

    @Override
    public Uni<RealmResponse> addRealm(AddRealmRequest request) {
        realmService.createRealm(request.getRealmName() , request.getDisplayName());
        return getRealmByName(RealmNameRequest.newBuilder().setRealmName(request.getRealmName()).build());
    }

    @Override
    public Uni<RealmsResponse> getRealms(Empty request) {
        List<RealmRepresentation> realmRepresentations = realmService.getRealms();
        return Uni.createFrom().item(() -> RealmsResponse.newBuilder().addAllRealmDto(dtoMapper.toRealmList(realmRepresentations)).build());
    }

    @Override
    public Uni<RealmResponse> getRealmByName(com.sitech.realm.RealmNameRequest request) {
        Dto.RealmDto realmDto = dtoMapper.toRealmDto(realmService.getRealmByName(request.getRealmName()).toRepresentation());
        return Uni.createFrom().item(() -> RealmResponse.newBuilder().setRealmDto(realmDto).build());
    }

    @Override
    public Uni<com.sitech.realm.AddRealmGroupResponse> addRealmGroup(com.sitech.realm.AddRealmGroupRequest request) {
        int result = realmService.addRealmGroup(request);
        return Uni.createFrom().item(() -> com.sitech.realm.AddRealmGroupResponse.newBuilder().setStatus(Long.valueOf(result)).build());
    }

    @Override
    public Uni<com.sitech.realm.GetRealmGroupResponse> getRealmGroupByName(com.sitech.realm.AddRealmGroupRequest request) {
        GroupRepresentation group = realmService.getRealmGroupByName(request.getRealmName() ,request.getGroupName());
        return Uni.createFrom().item( () -> com.sitech.realm.GetRealmGroupResponse.newBuilder().setGroupDto(dtoMapper.toGroup(group)).build());
    }


    @Override
    public Uni<com.sitech.realm.RealmUserResponse> getRealmUsers(com.sitech.realm.RealmNameRequest request) {
        List<UserRepresentation> userRepresentations = realmService.getRealmUsers(request.getRealmName());
        return Uni.createFrom().item(() -> com.sitech.realm.RealmUserResponse.newBuilder().addAllUserDto(dtoMapper.toUserList(userRepresentations)).build());
    }

    @Override
    public Uni<com.sitech.realm.GetRealmGroupsResponse> getRealmGroups(com.sitech.realm.RealmNameRequest request) {
       List<GroupRepresentation> groupRepresentations = realmService.getRealmGroups(request.getRealmName());
       return Uni.createFrom().item(() -> com.sitech.realm.GetRealmGroupsResponse.newBuilder().addAllGroupDto(dtoMapper.toGroupList(groupRepresentations)).build());
    }

    @Override
    public Uni<com.sitech.realm.GetRealmClientsResponse> getRealmClients(com.sitech.realm.RealmNameRequest request) {
        List<ClientRepresentation> clientRepresentations = realmService.getRealmClients(request.getRealmName());
        return Uni.createFrom().item(() -> com.sitech.realm.GetRealmClientsResponse.newBuilder().addAllClientDto(dtoMapper.toClientList(clientRepresentations)).build());
    }

    @Override
    public Uni<com.sitech.realm.GetRealmRolesResponse> getRealmRoles(com.sitech.realm.RealmNameRequest request) {
        List<RoleRepresentation> roleRepresentations = realmService.getRealmRoles(request.getRealmName());
        return Uni.createFrom().item(() -> com.sitech.realm.GetRealmRolesResponse.newBuilder().addAllRoleDto(dtoMapper.toRoleList(roleRepresentations)).build());
    }

    @Override
    public Uni<StatusResponse> logoutAllUsers(RealmNameRequest request) {
        realmService.logoutAllUsers(request);
        return Uni.createFrom().item(() -> com.sitech.realm.StatusResponse.newBuilder().setStatus(200).build());
    }

}
