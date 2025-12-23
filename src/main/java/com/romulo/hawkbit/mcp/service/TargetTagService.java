package com.romulo.hawkbit.mcp.service;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.tag.MgmtTag;
import org.eclipse.hawkbit.mgmt.json.model.tag.MgmtTagRequestBodyPut;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetTagRestApi;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetTagRestApi.OnNotFoundPolicy;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class TargetTagService {

    private final MgmtTargetTagRestApi mgmtTargetTagRestApi;

    TargetTagService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.mgmtTargetTagRestApi = hawkbitClient.mgmtService(MgmtTargetTagRestApi.class, tenant);
    }

    @McpTool(name = "getTargetsTags", description = "Get all Targets Tags")
    PagedList<MgmtTag> getTargetsTags(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return mgmtTargetTagRestApi.getTargetTags(rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getTargetTag", description = "Get a specific Target Tag by its ID")
    MgmtTag getTargetTag(Long tagId) {
        return mgmtTargetTagRestApi.getTargetTag(tagId).getBody();
    }

    @McpTool(name = "createTargetTags", description = "Handles the POST request of creating new target tag. The request body must always be a list of target tags.")
    List<MgmtTag> createTargetTags(List<MgmtTagRequestBodyPut> tags) {
        return mgmtTargetTagRestApi.createTargetTags(tags).getBody();
    }

    @McpTool(name = "updateTargetTag", description = "Handles the PUT request of updating a specific target tag by its ID.")
    MgmtTag updateTargetTag(Long tagId, MgmtTagRequestBodyPut restTargetTagRest) {
        return mgmtTargetTagRestApi.updateTargetTag(tagId, restTargetTagRest).getBody();
    }

    @McpTool(name = "deleteTargetTag", description = "Handles the DELETE request of deleting a single target tag.")
    void deleteTargetTag(Long tagId) {
        mgmtTargetTagRestApi.deleteTargetTag(tagId);
    }

    // Target tag assignments tools

    @McpTool(name = "getAssignedTargets", description = "Handles the GET request of retrieving a list of assigned targets.")
    PagedList<MgmtTarget> getAssignedTargets(
            Long targetTagId,
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return mgmtTargetTagRestApi.getAssignedTargets(targetTagId, rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "assignTargets", description = "Handles the POST request of target assignment. Already assigned target will be ignored.")
    void assignTargets(Long targetTagId, List<String> controllerIds,
            @McpToolParam(description = "On not found policy (default: FAIL)", required = false) OnNotFoundPolicy onNotFoundPolicy) {
        mgmtTargetTagRestApi.assignTargets(targetTagId, controllerIds, onNotFoundPolicy);
    }

    @McpTool(name = "unassignTargets", description = "Handles the DELETE request to unassign the given targets.")
    void unassignTargets(Long targetTagId, List<String> controllerIds,
            @McpToolParam(description = "On not found policy (default: FAIL)", required = false) OnNotFoundPolicy onNotFoundPolicy) {
        mgmtTargetTagRestApi.unassignTargets(targetTagId, onNotFoundPolicy, controllerIds);
    }

    @McpTool(name = "unassignTarget", description = "Handles the DELETE request to unassign the given target.")
    void unassignTarget(Long targetTagId, String controllerId) {
        mgmtTargetTagRestApi.unassignTarget(targetTagId, controllerId);
    }

    @McpTool(name = "assignTarget", description = "Handles the POST request of target assignment. Already assigned target will be ignored.")
    void assignTarget(Long targetTagId, String controllerId) {
        mgmtTargetTagRestApi.assignTarget(targetTagId, controllerId);
    }
}
