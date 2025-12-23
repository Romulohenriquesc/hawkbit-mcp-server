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
import org.eclipse.hawkbit.mgmt.json.model.distributionset.MgmtDistributionSet;
import org.eclipse.hawkbit.mgmt.json.model.tag.MgmtTag;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtDistributionSetAssignments;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTargetAttributes;
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

    @McpTool(name = "getTarget", description = "Get a specific target by its controller ID")
    MgmtTarget getTarget(String controllerId) {
        return mgmtTargetRestApi.getTarget(controllerId).getBody();
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

    // Metadata tools

    @McpTool(name = "createMetadata", description = "Create metadata for a specific target by its controller ID")
    Object createMetadata(
            @McpToolParam(description = "The controller ID of the target", required = true) String controllerId,
            @McpToolParam(description = "List of metadata to create", required = true) List<MgmtMetadata> metadataRest,
            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("metadataToCreate", metadataRest);
            preview.put("target", controllerId);
            return preview;
        }

        mgmtTargetRestApi.createMetadata(controllerId, metadataRest);
        return Map.of("message", "Metadata created successfully", "target", controllerId);
    }

    @McpTool(name = "deleteMetadata", description = "Delete metadata for a specific target by its controller ID and metadata key")
    Object deleteMetadata(
            @McpToolParam(description = "The controller ID of the target", required = true) String controllerId,
            @McpToolParam(description = "The metadata key to delete", required = true) String metadataKey,
            @McpToolParam(description = "Set to true to persist changes. Default false (preview only).", required = false) Boolean confirm) {

        if (confirm == null || !confirm) {
            Map<String, Object> preview = new HashMap<>();
            preview.put("message", "PREVIEW MODE: No changes were made. Please confirm to proceed.");
            preview.put("metadataToDelete", metadataKey);
            preview.put("target", controllerId);
            return preview;
        }

        mgmtTargetRestApi.deleteMetadata(controllerId, metadataKey);
        return Map.of("message", "Metadata deleted successfully", "target", controllerId, "key", metadataKey);
    }

    @McpTool(name = "getMetadataValue", description = "Get metadata value for a specific target by its controller ID and metadata key")
    MgmtMetadata getMetadataValue(String controllerId, String metadataKey) {
        return mgmtTargetRestApi.getMetadataValue(controllerId, metadataKey).getBody();
    }

    @McpTool(name = "getMetadata", description = "Get metadata for a specific target by its controller ID")
    PagedList<MgmtMetadata> getMetadata(String controllerId) {
        return mgmtTargetRestApi.getMetadata(controllerId).getBody();
    }

    @McpTool(name = "updateMetadata", description = "Update metadata for a specific target by its controller ID and metadata key")
    void updateMetadata(String controllerId, String metadataKey, MgmtMetadataBodyPut metadata) {
        mgmtTargetRestApi.updateMetadata(controllerId, metadataKey, metadata);
    }

    // Attributes tools

    @McpTool(name = "getAttributes", description = "Get attributes for a specific target by its controller ID")
    MgmtTargetAttributes getAttributes(String controllerId) {
        return mgmtTargetRestApi.getAttributes(controllerId).getBody();
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

    // Tags tools

    @McpTool(name = "getTags", description = "Get tags for a specific target by its controller ID")
    List<MgmtTag> getTags(String controllerId) {
        return mgmtTargetRestApi.getTags(controllerId).getBody();
    }

    // Distribution Set tools

    @McpTool(name = "getInstalledDistributionSet", description = "Get installed distribution set for a specific target by its controller ID")
    MgmtDistributionSet getInstalledDistributionSet(String controllerId) {
        return mgmtTargetRestApi.getInstalledDistributionSet(controllerId).getBody();
    }

    @McpTool(name = "getAssignedDistributionSet", description = "Get assigned distribution set for a specific target by its controller ID")
    MgmtDistributionSet getAssignedDistributionSet(String controllerId) {
        return mgmtTargetRestApi.getAssignedDistributionSet(controllerId).getBody();
    }

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

    // Target Type tools

    @McpTool(name = "assignTargetType", description = "Assign target type for a specific target by its controller ID")
    void assignTargetType(String controllerId, MgmtId targetTypeId) {
        mgmtTargetRestApi.assignTargetType(controllerId, targetTypeId);
    }

    @McpTool(name = "unassignTargetType", description = "Unassign target type for a specific target by its controller ID")
    void unassignTargetType(String controllerId) {
        mgmtTargetRestApi.unassignTargetType(controllerId);
    }

    // Target autoconfirm tools

    @McpTool(name = "activateAutoConfirm", description = "Handles the POST request to activate auto-confirmation for a specific target. As a result all current active as well as future actions will automatically be confirmed by mentioning the initiator as triggered person. Actions will be automatically confirmed, as long as auto-confirmation is active.")
    void activateAutoConfirm(String controllerId, MgmtTargetAutoConfirmUpdate update) {
        mgmtTargetRestApi.activateAutoConfirm(controllerId, update);
    }

    @McpTool(name = "deactivateAutoConfirm", description = "Handles the POST request to deactivate auto-confirmation for a specific target. All active actions will remain unchanged while all future actions need to be confirmed, before processing with the deployment.")
    void deactivateAutoConfirm(String controllerId) {
        mgmtTargetRestApi.deactivateAutoConfirm(controllerId);
    }

}