package com.romulo.hawkbit.mcp.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.rollout.MgmtRolloutResponseBody;
import org.eclipse.hawkbit.mgmt.json.model.rollout.MgmtRolloutRestRequestBodyPost;
import org.eclipse.hawkbit.mgmt.json.model.rollout.MgmtRolloutRestRequestBodyPut;
import org.eclipse.hawkbit.mgmt.json.model.rolloutgroup.MgmtRolloutGroupResponseBody;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRolloutRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class RolloutService {

    private final MgmtRolloutRestApi rolloutRestApi;

    public RolloutService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.rolloutRestApi = hawkbitClient.mgmtService(MgmtRolloutRestApi.class, tenant);
    }

    @McpTool(name = "getRollouts", description = "Get all Rollouts")
    PagedList<MgmtRolloutResponseBody> getRollouts(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return rolloutRestApi.getRollouts(rsqlParam, offset, limit, sortParam,
                MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE_DEFAULT).getBody();
    }

    @McpTool(name = "manageRollout", description = "Manages the lifecycle of a Rollout (Get single, Create, Update, Delete).")
    Object manageRollout(
            @McpToolParam(description = "The action to perform (GET, CREATE, UPDATE, DELETE)", required = true) RolloutCrudAction action,

            @McpToolParam(description = "Rollout ID (Required for GET, UPDATE, DELETE)", required = false) Long rolloutId,

            @McpToolParam(description = "Rollout creation body (Required for CREATE)", required = false) MgmtRolloutRestRequestBodyPost createBody,

            @McpToolParam(description = "Rollout update body (Required for UPDATE)", required = false) MgmtRolloutRestRequestBodyPut updateBody,

            @McpToolParam(description = "Set to true to persist changes (CREATE, UPDATE, DELETE). Default false (preview only).", required = false) Boolean confirm) {

        if (action != RolloutCrudAction.GET && (confirm == null || !confirm)) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("action", action);
            if (rolloutId != null)
                preview.put("rolloutId", rolloutId);
            if (createBody != null)
                preview.put("createBody", createBody);
            if (updateBody != null)
                preview.put("updateBody", updateBody);
            return preview;
        }

        switch (action) {
            case GET:
                if (rolloutId == null)
                    throw new IllegalArgumentException("Rollout ID is required for GET action");
                return rolloutRestApi.getRollout(rolloutId).getBody();

            case CREATE:
                if (createBody == null)
                    throw new IllegalArgumentException("Create body is required for CREATE action");
                return rolloutRestApi.create(createBody).getBody();

            case UPDATE:
                if (rolloutId == null || updateBody == null)
                    throw new IllegalArgumentException("Rollout ID and Update body are required for UPDATE action");
                return rolloutRestApi.update(rolloutId, updateBody).getBody();

            case DELETE:
                if (rolloutId == null)
                    throw new IllegalArgumentException("Rollout ID is required for DELETE action");
                rolloutRestApi.delete(rolloutId);
                return "Rollout " + rolloutId + " deleted successfully.";

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    @McpTool(name = "manageRolloutState", description = "Handles the lifecycle and state management of a Rollout. Use to start, pause, resume, approve, deny, or retry.")
    Object manageRolloutState(
            @McpToolParam(description = "ID of the Rollout", required = true) Long rolloutId,

            @McpToolParam(description = "The action to be executed on the Rollout (START, PAUSE, APPROVE, etc)", required = true) RolloutAction action,

            @McpToolParam(description = "Optional remark. Used only for APPROVE or DENY actions.", required = false) String remark,

            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("action", action);
            preview.put("rolloutId", rolloutId);
            if (remark != null)
                preview.put("remark", remark);
            return preview;
        }

        String finalRemark = (remark != null) ? remark : "";

        switch (action) {
            case START:
                rolloutRestApi.start(rolloutId);
                return "Rollout " + rolloutId + " started.";

            case PAUSE:
                rolloutRestApi.pause(rolloutId);
                return "Rollout " + rolloutId + " paused.";

            case RESUME:
                rolloutRestApi.resume(rolloutId);
                return "Rollout " + rolloutId + " resumed.";

            case TRIGGER_NEXT_GROUP:
                rolloutRestApi.triggerNextGroup(rolloutId);
                return "Next group processing triggered for Rollout " + rolloutId;

            case APPROVE:
                rolloutRestApi.approve(rolloutId, finalRemark);
                return "Rollout " + rolloutId + " approved.";

            case DENY:
                rolloutRestApi.deny(rolloutId, finalRemark);
                return "Rollout " + rolloutId + " denied.";

            case RETRY:
                return rolloutRestApi.retryRollout(rolloutId).getBody();

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    @McpTool(name = "getRolloutGroups", description = "Retrieves a paged list of all rollout groups (deploy groups) assigned to a specific rollout.")
    PagedList<MgmtRolloutGroupResponseBody> getRolloutGroups(
            @McpToolParam(description = "The ID of the Rollout", required = true) Long rolloutId,

            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,

            @McpToolParam(description = "Offset for pagination (default: 0)", required = true) int offset,

            @McpToolParam(description = "Limit for pagination (max: 50, default: 50)", required = true) int limit,

            @McpToolParam(description = "Sort parameter. Example: name:asc", required = false) String sortParam) {
        return rolloutRestApi.getRolloutGroups(rolloutId, rsqlParam, offset, limit, sortParam,
                MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE_DEFAULT).getBody();
    }

    @McpTool(name = "getRolloutGroup", description = "Retrieves the details of a single rollout group (deploy group) within a specific rollout.")
    MgmtRolloutGroupResponseBody getRolloutGroup(
            @McpToolParam(description = "The ID of the Rollout", required = true) Long rolloutId,

            @McpToolParam(description = "The ID of the Rollout Group", required = true) Long groupId) {
        return rolloutRestApi.getRolloutGroup(rolloutId, groupId).getBody();
    }

    @McpTool(name = "getRolloutGroupTargets", description = "Retrieves a paged list of all targets assigned to a specific rollout group.")
    PagedList<MgmtTarget> getRolloutGroupTargets(
            @McpToolParam(description = "The ID of the Rollout", required = true) Long rolloutId,

            @McpToolParam(description = "The ID of the Rollout Group", required = true) Long groupId,

            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,

            @McpToolParam(description = "Offset for pagination (default: 0)", required = true) int offset,

            @McpToolParam(description = "Limit for pagination (max: 50, default: 50)", required = true) int limit,

            @McpToolParam(description = "Sort parameter. Example: name:asc", required = false) String sortParam) {
        return rolloutRestApi.getRolloutGroupTargets(rolloutId, groupId, rsqlParam, offset, limit, sortParam).getBody();
    }

}

enum RolloutAction {
    START,
    PAUSE,
    RESUME,
    APPROVE,
    DENY,
    TRIGGER_NEXT_GROUP,
    RETRY
}

enum RolloutCrudAction {
    GET,
    CREATE,
    UPDATE,
    DELETE
}

enum RolloutGroupView {
    LIST_GROUPS,
    GET_GROUP,
    LIST_GROUP_TARGETS
}