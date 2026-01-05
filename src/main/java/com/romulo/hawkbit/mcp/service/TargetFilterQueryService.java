package com.romulo.hawkbit.mcp.service;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtDistributionSetAutoAssignment;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQuery;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQueryRequestBody;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetFilterQueryRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class TargetFilterQueryService {

    private final MgmtTargetFilterQueryRestApi targetFilterRestApi;

    public TargetFilterQueryService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.targetFilterRestApi = hawkbitClient.mgmtService(MgmtTargetFilterQueryRestApi.class, tenant);
    }

    @McpTool(name = "getTargetFilters", description = "Get all target filter queries with filtering and pagination.")
    PagedList<MgmtTargetFilterQuery> getTargetFilters(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset for pagination (default: 0)", required = true) int offset,
            @McpToolParam(description = "Limit for pagination (max: 50, default: 50)", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return targetFilterRestApi.getFilters(rsqlParam, offset, limit, sortParam,
                MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE_DEFAULT).getBody();
    }

    @McpTool(name = "manageTargetFilter", description = "Manages the lifecycle of a Target Filter Query (Get single, Create, Update, Delete).")
    public Object manageTargetFilter(
            @McpToolParam(description = "The action to perform (GET, CREATE, UPDATE, DELETE)", required = true) TargetFilterCrudAction action,

            @McpToolParam(description = "Filter ID (Required for GET, UPDATE, DELETE)", required = false) Long filterId,

            @McpToolParam(description = "Filter body (Required for CREATE and UPDATE)", required = false) MgmtTargetFilterQueryRequestBody filterBody) {
        switch (action) {
            case GET:
                if (filterId == null)
                    throw new IllegalArgumentException("Filter ID is required for GET action");
                return targetFilterRestApi.getFilter(filterId).getBody();

            case CREATE:
                if (filterBody == null)
                    throw new IllegalArgumentException("Filter body is required for CREATE action");
                return targetFilterRestApi.createFilter(filterBody).getBody();

            case UPDATE:
                if (filterId == null || filterBody == null)
                    throw new IllegalArgumentException("Filter ID and Body are required for UPDATE action");
                return targetFilterRestApi.updateFilter(filterId, filterBody).getBody();

            case DELETE:
                if (filterId == null)
                    throw new IllegalArgumentException("Filter ID is required for DELETE action");
                targetFilterRestApi.deleteFilter(filterId);
                return "Target Filter " + filterId + " deleted successfully.";

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    @McpTool(name = "manageTargetFilterAutoAssignment", description = "Manages the Distribution Set Auto-Assignment for a Target Filter.")
    public Object manageTargetFilterAutoAssignment(
            @McpToolParam(description = "Filter ID", required = true) Long filterId,

            @McpToolParam(description = "The action to perform (GET, ASSIGN, UNASSIGN)", required = true) AutoAssignmentAction action,

            @McpToolParam(description = "Auto Assignment details (Required for ASSIGN)", required = false) MgmtDistributionSetAutoAssignment assignmentBody) {
        switch (action) {
            case GET:
                return targetFilterRestApi.getAssignedDistributionSet(filterId).getBody();

            case ASSIGN:
                if (assignmentBody == null)
                    throw new IllegalArgumentException("Assignment body is required for ASSIGN action");
                return targetFilterRestApi.postAssignedDistributionSet(filterId, assignmentBody).getBody();

            case UNASSIGN:
                targetFilterRestApi.deleteAssignedDistributionSet(filterId);
                return "Auto-assignment removed successfully from filter " + filterId;

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }
}

enum TargetFilterCrudAction {
    GET,
    CREATE,
    UPDATE,
    DELETE
}

enum AutoAssignmentAction {
    GET,
    ASSIGN,
    UNASSIGN
}
