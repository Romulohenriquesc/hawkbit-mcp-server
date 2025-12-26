package com.romulo.hawkbit.mcp.service;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
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

    @McpTool(name = "manageTargetType", description = "Manages the lifecycle of Target Types (Create, Read, Update, Delete).")
    public Object manageTargetType(
            @McpToolParam(description = "Action to be performed", required = true) TargetTypeCrudAction action,

            @McpToolParam(description = "ID of the Target Type (Required for GET, UPDATE, DELETE)", required = false) Long targetTypeId,

            @McpToolParam(description = "List of objects for creation (Required for CREATE)", required = false) List<MgmtTargetTypeRequestBodyPost> createBody,

            @McpToolParam(description = "Object for update (Required for UPDATE)", required = false) MgmtTargetTypeRequestBodyPut updateBody) {
        switch (action) {
            case GET:
                if (targetTypeId == null)
                    throw new IllegalArgumentException("ID is required for GET");
                return mgmtTargetTypeRestApi.getTargetType(targetTypeId).getBody();

            case CREATE:
                if (createBody == null || createBody.isEmpty())
                    throw new IllegalArgumentException("Body is required for CREATE");
                return mgmtTargetTypeRestApi.createTargetTypes(createBody).getBody();

            case UPDATE:
                if (targetTypeId == null || updateBody == null)
                    throw new IllegalArgumentException("ID and Body are required for UPDATE");
                return mgmtTargetTypeRestApi.updateTargetType(targetTypeId, updateBody).getBody();

            case DELETE:
                if (targetTypeId == null)
                    throw new IllegalArgumentException("ID is required for DELETE");
                mgmtTargetTypeRestApi.deleteTargetType(targetTypeId);
                return "Target Type " + targetTypeId + " deleted successfully.";

            default:
                throw new IllegalArgumentException("Unsupported action");
        }
    }

    @McpTool(name = "manageTargetTypeCompatibility", description = "Manages the compatibility between Target Types and Distribution Set Types.")
    public Object manageTargetTypeCompatibility(
            @McpToolParam(description = "ID of the Target Type", required = true) Long targetTypeId,

            @McpToolParam(description = "Action of compatibility (LIST, ADD, REMOVE)", required = true) CompatibilityAction action,

            @McpToolParam(description = "List of assignments to add (Required for ADD)", required = false) List<MgmtDistributionSetTypeAssignment> assignments,

            @McpToolParam(description = "ID of the Distribution Set Type to remove (Required for REMOVE)", required = false) Long distributionSetTypeId) {
        switch (action) {
            case LIST:
                return mgmtTargetTypeRestApi.getCompatibleDistributionSets(targetTypeId).getBody();

            case ADD:
                if (assignments == null || assignments.isEmpty()) {
                    throw new IllegalArgumentException("List of assignments is required for ADD");
                }
                mgmtTargetTypeRestApi.addCompatibleDistributionSets(targetTypeId, assignments);
                return "Compatibilities added successfully.";

            case REMOVE:
                if (distributionSetTypeId == null) {
                    throw new IllegalArgumentException("distributionSetTypeId is required for REMOVE");
                }
                mgmtTargetTypeRestApi.removeCompatibleDistributionSet(targetTypeId, distributionSetTypeId);
                return "Compatibility removed successfully.";

            default:
                throw new IllegalArgumentException("Unsupported action");
        }
    }

}

enum TargetTypeCrudAction {
    GET,
    CREATE,
    UPDATE,
    DELETE
}

enum CompatibilityAction {
    LIST,
    ADD,
    REMOVE
}