package com.sitech.mapper;

import com.sitech.dto.Dto;
import com.sitech.dto.Dto.RealmDto;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.*;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Objects;
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


    public Dto.UserAccessTokenDto toUserAccessTokenDto(AccessTokenResponse accessTokenResponse) {
        return Dto.UserAccessTokenDto.newBuilder()
                .setToken(accessTokenResponse.getToken() != null ? accessTokenResponse.getToken() : "")
                .setExpiresIn(Objects.isNull(accessTokenResponse.getExpiresIn()) ? 0 : accessTokenResponse.getExpiresIn())
                .setRefreshExpiresIn(accessTokenResponse.getRefreshExpiresIn())
                .setRefreshToken(Objects.isNull(accessTokenResponse.getRefreshToken()) ? "" : accessTokenResponse.getRefreshToken())
                .setTokenType(Objects.isNull(accessTokenResponse.getTokenType()) ? "" : accessTokenResponse.getTokenType())
                .setIdToken(Objects.isNull(accessTokenResponse.getIdToken()) ? "" : accessTokenResponse.getIdToken())
                .setNotBeforePolicy(Objects.isNull(accessTokenResponse.getNotBeforePolicy()) ? 0 : accessTokenResponse.getNotBeforePolicy())
                .setSessionState(Objects.isNull(accessTokenResponse.getSessionState()) ? "" : accessTokenResponse.getSessionState())
                .setScope(Objects.isNull(accessTokenResponse.getScope()) ? "" : accessTokenResponse.getScope())
                .setError(Objects.isNull(accessTokenResponse.getError()) ? "" : accessTokenResponse.getError())
                .setErrorDescription(Objects.isNull(accessTokenResponse.getErrorDescription()) ? "" : accessTokenResponse.getErrorDescription())
                .setErrorUri(Objects.isNull(accessTokenResponse.getErrorUri()) ? "" : accessTokenResponse.getErrorUri())
                .build();
    }
}
