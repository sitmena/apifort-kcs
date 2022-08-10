package com.sitech.mapper;

import com.sitech.dto.Dto;
import com.sitech.dto.Dto.RealmDto;
import org.keycloak.representations.idm.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DtoMapper {

    public List<RealmDto> toRealmList(List<RealmRepresentation> realmRepresentations) {
        return realmRepresentations.stream().map(
                r -> Dto.RealmDto.newBuilder()
                        .setId(r.getId())
                        .setRealm(r.getRealm())
                        .setDisplayName(r.getDisplayName())
                        .setEnabled(r.isEnabled()).build()
        ).collect(Collectors.toList());
    }

    public List<Dto.UserDto> toUserList(List<UserRepresentation> userRepresentations) {
        return userRepresentations.stream().map(
                r -> Dto.UserDto.newBuilder()
                        .setId(r.getId())
                        .setCreatedTimestamp(r.getCreatedTimestamp())
                        .setUsername(r.getUsername())
                        .setEnabled(r.isEnabled())
                        .setFirstName(r.getFirstName())
                        .setLastName(r.getLastName())
                        .setEmail(r.getEmail()).build()
        ).collect(Collectors.toList());
    }

    public List<Dto.GroupDto> toGroupList(List<GroupRepresentation> groupRepresentations) {
        return groupRepresentations.stream().map(
                r -> Dto.GroupDto.newBuilder()
                        .setId(r.getId())
                        .setName(r.getName())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<Dto.ClientDto> toClientList(List<ClientRepresentation> clientRepresentations) {
        return clientRepresentations.stream().map(
                r -> Dto.ClientDto.newBuilder()
                        .setId(r.getId())
                        .setName(r.getName())
                        .setEnabled(r.isEnabled())
                        .build()
        ).collect(Collectors.toList());
    }

    public List<Dto.RoleDto> toRoleList(List<RoleRepresentation> roleRepresentations) {
        return roleRepresentations.stream().map(
                r -> Dto.RoleDto.newBuilder()
                        .setId(r.getId())
                        .setName(r.getName())
                        .setDescription(r.getDescription())
                        .build()
        ).collect(Collectors.toList());
    }

    public Dto.GroupDto toGroup(GroupRepresentation group) {
        return Dto.GroupDto.newBuilder()
                .setId(group.getId())
                .setName(group.getName())
                .build();
    }

    public RealmDto toRealmDto(RealmRepresentation realmRepresentation) {
        RealmDto realmDto = RealmDto.newBuilder().setId(realmRepresentation.getId())
                .setRealm(realmRepresentation.getRealm())
                .setDisplayName(realmRepresentation.getDisplayName())
                .setEnabled(realmRepresentation.isEnabled()).build();
        return realmDto;
    }


    public Dto.UserDto toUser(UserRepresentation result) {
        return Dto.UserDto.newBuilder()
                .setId(result.getId())
                .setCreatedTimestamp(result.getCreatedTimestamp())
                .setUsername(result.getUsername())
                .setEnabled(result.isEnabled())
                .setFirstName(result.getFirstName())
                .setLastName(result.getLastName())
                .setEmail(result.getEmail()).build();
    }
}
