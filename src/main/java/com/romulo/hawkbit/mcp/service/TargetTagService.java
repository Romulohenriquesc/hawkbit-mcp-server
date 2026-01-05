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

    @McpTool(name = "manageTargetTag", description = "Manages the lifecycle of Target Tags (Get, Create, Update, Delete).")
    Object manageTargetTag(
            @McpToolParam(description = "The action to perform (GET, CREATE, UPDATE, DELETE)", required = true) TargetTagCrudAction action,

            @McpToolParam(description = "Tag ID (Required for GET, UPDATE, DELETE)", required = false) Long tagId,

            @McpToolParam(description = "List of tags to create (Required for CREATE)", required = false) List<MgmtTagRequestBodyPut> tagsToCreate,

            @McpToolParam(description = "Tag body to update (Required for UPDATE)", required = false) MgmtTagRequestBodyPut tagToUpdate) {
        switch (action) {
            case GET:
                if (tagId == null)
                    throw new IllegalArgumentException("Tag ID is required for GET action");
                return mgmtTargetTagRestApi.getTargetTag(tagId).getBody();

            case CREATE:
                if (tagsToCreate == null || tagsToCreate.isEmpty())
                    throw new IllegalArgumentException("List of tags is required for CREATE action");
                return mgmtTargetTagRestApi.createTargetTags(tagsToCreate).getBody();

            case UPDATE:
                if (tagId == null || tagToUpdate == null)
                    throw new IllegalArgumentException("Tag ID and Body are required for UPDATE action");
                return mgmtTargetTagRestApi.updateTargetTag(tagId, tagToUpdate).getBody();

            case DELETE:
                if (tagId == null)
                    throw new IllegalArgumentException("Tag ID is required for DELETE action");
                mgmtTargetTagRestApi.deleteTargetTag(tagId);
                return "Target Tag " + tagId + " deleted successfully.";

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    // Target tag assignments tools

    @McpTool(name = "getAssignedTargets", description = "Handles the GET request of retrieving a list of assigned targets.")
    PagedList<MgmtTarget> getAssignedTargets(
            @McpToolParam(description = "The ID of the Target Tag", required = true) Long targetTagId,
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return mgmtTargetTagRestApi.getAssignedTargets(targetTagId, rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "manageTagAssignments", description = "Manages target assignments for a Target Tag (Assign or Unassign targets).")
    String manageTagAssignments(
            @McpToolParam(description = "The ID of the Target Tag", required = true) Long targetTagId,

            @McpToolParam(description = "The action to perform (ASSIGN or UNASSIGN)", required = true) TagAssignmentAction action,

            @McpToolParam(description = "List of Target Controller IDs to assign or unassign", required = true) List<String> controllerIds,

            @McpToolParam(description = "On not found policy (default: FAIL)", required = false) OnNotFoundPolicy onNotFoundPolicy) {
        // Use default policy if not provided
        OnNotFoundPolicy policy = (onNotFoundPolicy != null) ? onNotFoundPolicy : OnNotFoundPolicy.FAIL;

        switch (action) {
            case ASSIGN:
                if (controllerIds == null || controllerIds.isEmpty())
                    throw new IllegalArgumentException("Controller IDs list is required for ASSIGN");
                mgmtTargetTagRestApi.assignTargets(targetTagId, controllerIds, policy);
                return "Targets assigned successfully to tag " + targetTagId;

            case UNASSIGN:
                if (controllerIds == null || controllerIds.isEmpty())
                    throw new IllegalArgumentException("Controller IDs list is required for UNASSIGN");
                mgmtTargetTagRestApi.unassignTargets(targetTagId, policy, controllerIds);
                return "Targets unassigned successfully from tag " + targetTagId;

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }
}

enum TargetTagCrudAction {
    GET,
    CREATE,
    UPDATE,
    DELETE
}

enum TagAssignmentAction {
    ASSIGN,
    UNASSIGN
}
