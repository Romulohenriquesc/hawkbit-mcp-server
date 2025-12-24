package com.romulo.hawkbit.mcp.service;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.distributionsettype.MgmtDistributionSetType;
import org.eclipse.hawkbit.mgmt.json.model.distributionsettype.MgmtDistributionSetTypeAssignment;
import org.eclipse.hawkbit.mgmt.json.model.targettype.MgmtTargetType;
import org.eclipse.hawkbit.mgmt.json.model.targettype.MgmtTargetTypeRequestBodyPost;
import org.eclipse.hawkbit.mgmt.json.model.targettype.MgmtTargetTypeRequestBodyPut;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetTypeRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class TargetTypeService {

    private final MgmtTargetTypeRestApi mgmtTargetTypeRestApi;

    TargetTypeService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.mgmtTargetTypeRestApi = hawkbitClient.mgmtService(MgmtTargetTypeRestApi.class, tenant);
    }

    @McpTool(name = "getTargetTypes", description = "Handles the GET request of retrieving all target types.")
    PagedList<MgmtTargetType> getTargetTypes(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return mgmtTargetTypeRestApi.getTargetTypes(rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getTargetType", description = "Handles the GET request of retrieving a single target type.")
    MgmtTargetType getTargetType(Long targetTypeId) {
        return mgmtTargetTypeRestApi.getTargetType(targetTypeId).getBody();
    }

    @McpTool(name = "createTargetTypes", description = "Handles the POST request for creating new target types. The request body must always be a list of types.")
    List<MgmtTargetType> createTargetTypes(List<MgmtTargetTypeRequestBodyPost> targetTypes) {
        return mgmtTargetTypeRestApi.createTargetTypes(targetTypes).getBody();
    }

    @McpTool(name = "updateTargetType", description = "Handles the PUT request for a single target type.")
    MgmtTargetType updateTargetType(Long targetTypeId, MgmtTargetTypeRequestBodyPut targetType) {
        return mgmtTargetTypeRestApi.updateTargetType(targetTypeId, targetType).getBody();
    }

    @McpTool(name = "deleteTargetType", description = "Handles the DELETE request for a single target type.")
    void deleteTargetType(Long targetTypeId) {
        mgmtTargetTypeRestApi.deleteTargetType(targetTypeId);
    }

    @McpTool(name = "getCompatibilityDistributionSetTypes", description = "Handles the GET request of retrieving the list of compatible distribution set types in that target type.")
    List<MgmtDistributionSetType> getCompatibilityDistributionSetTypes(Long targetTypeId) {
        return mgmtTargetTypeRestApi.getCompatibleDistributionSets(targetTypeId).getBody();
    }

    @McpTool(name = "addCompatibleDistributionSets", description = "Handles the POST request for adding compatible distribution set types to a target type.")
    void addCompatibleDistributionSets(Long targetTypeId,
            List<MgmtDistributionSetTypeAssignment> distributionSetTypeIds) {
        mgmtTargetTypeRestApi.addCompatibleDistributionSets(targetTypeId, distributionSetTypeIds);
    }

    @McpTool(name = "removeCompatibleDistributionSet", description = "Handles the DELETE request for removing a distribution set type from a single target type.")
    void removeCompatibleDistributionSet(Long targetTypeId, Long distributionSetTypeId) {
        mgmtTargetTypeRestApi.removeCompatibleDistributionSet(targetTypeId, distributionSetTypeId);
    }

}
