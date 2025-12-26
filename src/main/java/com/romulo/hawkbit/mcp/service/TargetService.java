package com.romulo.hawkbit.mcp.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.hawkbit.mgmt.json.model.MgmtId;
import org.eclipse.hawkbit.mgmt.json.model.MgmtMetadata;
import org.eclipse.hawkbit.mgmt.json.model.MgmtMetadataBodyPut;
import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtAction;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtActionConfirmationRequestBodyPut;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtActionRequestBodyPut;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtActionStatus;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtDistributionSetAssignments;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTargetAutoConfirmUpdate;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTargetRequestBody;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class TargetService {

    private final MgmtTargetRestApi mgmtTargetRestApi;

    TargetService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.mgmtTargetRestApi = hawkbitClient.mgmtService(MgmtTargetRestApi.class, tenant);
    }

    @McpTool(name = "getTargetDetails", description = "Get detailed information about a Target. Can include attributes, tags, and distribution sets in the same call.")
    public Map<String, Object> getTargetDetails(
            @McpToolParam(description = "Controller ID of the Target", required = true) String controllerId,

            @McpToolParam(description = "Include target attributes?", required = false) boolean includeAttributes,

            @McpToolParam(description = "Include target tags?", required = false) boolean includeTags,

            @McpToolParam(description = "Include Distribution Sets (installed and assigned)?", required = false) boolean includeDistributionSets) {
        Map<String, Object> result = new HashMap<>();

        result.put("target", mgmtTargetRestApi.getTarget(controllerId).getBody());

        if (includeAttributes) {
            try {
                result.put("attributes", mgmtTargetRestApi.getAttributes(controllerId).getBody());
            } catch (Exception e) {
                result.put("attributes_error", "Failed to load attributes: " + e.getMessage());
            }
        }

        if (includeTags) {
            try {
                result.put("tags", mgmtTargetRestApi.getTags(controllerId).getBody());
            } catch (Exception e) {
                result.put("tags_error", e.getMessage());
            }
        }

        if (includeDistributionSets) {
            try {
                Map<String, Object> dsInfo = new HashMap<>();
                dsInfo.put("assigned", mgmtTargetRestApi.getAssignedDistributionSet(controllerId).getBody());
                dsInfo.put("installed", mgmtTargetRestApi.getInstalledDistributionSet(controllerId).getBody());
                result.put("distributionSets", dsInfo);
            } catch (Exception e) {
                result.put("distributionSets_error", e.getMessage());
            }
        }

        return result;
    }

    @McpTool(name = "manageTargetMetadata", description = "Manage metadata for a Target (CRUD operations).")
    public Object manageTargetMetadata(
            @McpToolParam(description = "Controller ID of the Target", required = true) String controllerId,

            @McpToolParam(description = "Action to be performed", required = true) MetadataAction action,

            @McpToolParam(description = "Metadata key (Required for GET_SINGLE, UPDATE, DELETE)", required = false) String key,

            @McpToolParam(description = "List of metadata for creation (Required for CREATE)", required = false) List<MgmtMetadata> metadataList,

            @McpToolParam(description = "Metadata body for update (Required for UPDATE)", required = false) MgmtMetadataBodyPut metadataBody,

            @McpToolParam(description = "Confirm write operation (CREATE, UPDATE, DELETE)", required = false) Boolean confirm) {
        // Preview logic for write operations
        if (isWriteAction(action) && (confirm == null || !confirm)) {
            return Map.of("status", "PREVIEW", "action", action, "target", controllerId, "key", key);
        }

        switch (action) {
            case GET_ALL:
                return mgmtTargetRestApi.getMetadata(controllerId).getBody();

            case GET_SINGLE:
                if (key == null)
                    throw new IllegalArgumentException("Key is required for GET_SINGLE");
                return mgmtTargetRestApi.getMetadataValue(controllerId, key).getBody();

            case CREATE:
                if (metadataList == null)
                    throw new IllegalArgumentException("List of metadata is required for CREATE");
                mgmtTargetRestApi.createMetadata(controllerId, metadataList);
                return "Metadata created successfully.";

            case UPDATE:
                if (key == null || metadataBody == null)
                    throw new IllegalArgumentException("Key and Body are required for UPDATE");
                mgmtTargetRestApi.updateMetadata(controllerId, key, metadataBody);
                return "Metadata " + key + " updated.";

            case DELETE:
                if (key == null)
                    throw new IllegalArgumentException("Key is required for DELETE");
                mgmtTargetRestApi.deleteMetadata(controllerId, key);
                return "Metadata " + key + " deleted.";

            default:
                throw new IllegalArgumentException("Unsupported action");
        }
    }

    private boolean isWriteAction(MetadataAction action) {
        return action == MetadataAction.CREATE || action == MetadataAction.UPDATE || action == MetadataAction.DELETE;
    }

    // Target tools

    @McpTool(name = "getTargets", description = "Get all targets")
    PagedList<MgmtTarget> getTargets(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.  Only if necessary, consult getTargetSearchFields with the available fields.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc. Can be use the same sort parameter as the getTargetSearchFields.", required = false) String sortParam) {
        return mgmtTargetRestApi.getTargets(rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "createTargets", description = "Create new targets. Fill only mandatory fields.")
    Object createTargets(
            @McpToolParam(description = "List of targets to create") List<MgmtTargetRequestBody> targets,
            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("targetsToCreate", targets);
            preview.put("count", targets.size());
            return preview;
        }

        return mgmtTargetRestApi.createTargets(targets).getBody();
    }

    @McpTool(name = "deleteTarget", description = "Delete a specific target by its controller ID")
    Object deleteTarget(
            @McpToolParam(description = "The controller ID of the target", required = true) String controllerId,
            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("targetToDelete", controllerId);
            return preview;
        }

        return mgmtTargetRestApi.deleteTarget(controllerId).getBody();
    }

    // Actions tools

    @McpTool(name = "getActionHistory", description = "Get action history for a specific target by its controller ID")
    PagedList<MgmtAction> getActionHistory(String controllerId,
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.  Only if necessary, consult getActionHistorySearchFields with the available fields.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc. Can be use the same sort parameter as the getActionHistorySearchFields.", required = false) String sortParam) {
        return mgmtTargetRestApi.getActionHistory(controllerId, rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getAction", description = "Get action by id of a specific target")
    MgmtAction getAction(String controllerId, Long actionId) {
        return mgmtTargetRestApi.getAction(controllerId, actionId).getBody();
    }

    @McpTool(name = "cancelAction", description = "Cancel action by id of a specific target. Cancels an active action, only active actions can be deleted.")
    Object cancelAction(
            @McpToolParam(description = "The controller ID of the target", required = true) String controllerId,
            @McpToolParam(description = "The action ID to cancel", required = true) Long actionId,
            @McpToolParam(description = "optional parameter, which indicates a force cancel.", required = false) Boolean force,
            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("actionToCancel", actionId);
            preview.put("target", controllerId);
            preview.put("force", force);
            return preview;
        }

        mgmtTargetRestApi.cancelAction(controllerId, actionId, force);
        return Map.of("message", "Action canceled successfully", "actionId", actionId);
    }

    @McpTool(name = "updateAction", description = "Update action by id of a specific target")
    MgmtAction updateAction(String controllerId, Long actionId, MgmtActionRequestBodyPut actionUpdate) {
        return mgmtTargetRestApi.updateAction(controllerId, actionId, actionUpdate).getBody();
    }

    @McpTool(name = "updateActionConfirmation", description = """
            Either confirm or deny an action which is waiting for confirmation.
            The action will be transferred into the RUNNING state in case confirming it.
            The action will remain in WAITING_FOR_CONFIRMATION state in case denying it.
            """)
    void updateActionConfirmation(String controllerId, Long actionId,
            MgmtActionConfirmationRequestBodyPut actionConfirmation) {
        mgmtTargetRestApi.updateActionConfirmation(controllerId, actionId, actionConfirmation);
    }

    @McpTool(name = "getActionStatusList", description = "Handles the GET request of retrieving a specific action on a specific target.")
    PagedList<MgmtActionStatus> getActionStatusList(String targetId, Long actionId,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: id:desc", required = false) String sortParam) {
        return mgmtTargetRestApi.getActionStatusList(targetId, actionId, offset, limit, sortParam).getBody();
    }

    // Distribution Set tools

    @McpTool(name = "assignDistributionSet", description = "Assign distribution set for a specific target by its controller ID")
    Object assignDistributionSet(String controllerId,
            MgmtDistributionSetAssignments dsAssignments,
            @McpToolParam(description = """
                    Offline update (set param to true) that is only reported but not managed by the service, e.g.
                    defaults set in factory, manual updates or migrations from other update systems. A completed action
                    is added to the history of the target(s). Target is set to IN_SYNC state as both assigned and
                    installed DS are set. Note: only executed if the target has currently no running update""", required = false) Boolean offline,
            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("distributionSetAssignments", dsAssignments);
            preview.put("target", controllerId);
            preview.put("offline", offline);
            return preview;
        }

        return mgmtTargetRestApi.postAssignedDistributionSet(controllerId, dsAssignments, offline).getBody();
    }

    // Target Type Tools

    @McpTool(name = "manageTargetType", description = "Manage target type for a specific target by its controller ID(ASSIGN or UNASSIGN)")
    String manageTargetType(
            @McpToolParam(description = "Controller ID of the Target", required = true) String controllerId,

            @McpToolParam(description = "Action to be performed (ASSIGN or UNASSIGN)", required = true) TargetTypeAction action,

            @McpToolParam(description = "ID of the Target Type. Required only if the action is ASSIGN.", required = false) MgmtId targetTypeId) {
        switch (action) {
            case ASSIGN:
                if (targetTypeId == null) {
                    throw new IllegalArgumentException("targetTypeId is required for the ASSIGN action");
                }
                mgmtTargetRestApi.assignTargetType(controllerId, targetTypeId);
                return "Target Type assigned successfully to target " + controllerId;

            case UNASSIGN:
                mgmtTargetRestApi.unassignTargetType(controllerId);
                return "Target Type unassigned successfully from target " + controllerId;

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    // Target Auto-Confirm Tools

    @McpTool(name = "manageTargetAutoConfirm", description = "Manage the status of Auto-Confirmation of a target (Activate or Deactivate)")
    String manageTargetAutoConfirm(
            @McpToolParam(description = "Controller ID of the Target", required = true) String controllerId,

            @McpToolParam(description = "Action to be performed (ACTIVATE or DEACTIVATE)", required = true) AutoConfirmAction action,

            @McpToolParam(description = "Configuration object for auto-confirmation. Required only if the action is ACTIVATE.", required = false) MgmtTargetAutoConfirmUpdate update) {
        switch (action) {
            case ACTIVATE:
                if (update == null) {
                    throw new IllegalArgumentException("The 'update' object is required for the ACTIVATE action");
                }
                mgmtTargetRestApi.activateAutoConfirm(controllerId, update);
                return "Auto-Confirmation ACTIVATED for target " + controllerId;

            case DEACTIVATE:
                mgmtTargetRestApi.deactivateAutoConfirm(controllerId);
                return "Auto-Confirmation DEACTIVATED for target " + controllerId;

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

}

enum MetadataAction {
    GET_ALL,
    GET_SINGLE,
    CREATE,
    UPDATE,
    DELETE
}

enum TargetTypeAction {
    ASSIGN,
    UNASSIGN
}

enum AutoConfirmAction {
    ACTIVATE,
    DEACTIVATE
}