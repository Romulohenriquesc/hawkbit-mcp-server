package com.romulo.hawkbit.mcp.service;

import org.eclipse.hawkbit.mgmt.rest.api.MgmtActionRestApi;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class ActionService {

    private final MgmtActionRestApi actionRestApi;

    public ActionService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.actionRestApi = hawkbitClient.mgmtService(MgmtActionRestApi.class, tenant);
    }

    @McpTool(name = "queryActions", description = "Retrieves information about Actions (Search/List or Get Single Details).")
    public Object queryActions(
            @McpToolParam(description = "The type of query to perform (SEARCH or GET_DETAILS)", required = true) ActionQueryType queryType,

            @McpToolParam(description = "Action ID (Required for GET_DETAILS)", required = false) Long actionId,

            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter (Available for SEARCH)", required = false) String rsqlParam,

            @McpToolParam(description = "Offset for pagination (default: 0)", required = false) Integer offset,

            @McpToolParam(description = "Limit for pagination (max: 50, default: 50)", required = false) Integer limit,

            @McpToolParam(description = "Sort parameter. Example: id:desc", required = false) String sortParam) {

        int finalOffset = (offset != null) ? offset : 0;
        int finalLimit = (limit != null) ? limit : 50;

        switch (queryType) {
            case SEARCH:
                return actionRestApi.getActions(rsqlParam, finalOffset, finalLimit, sortParam,
                        MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE_DEFAULT).getBody();

            case GET_DETAILS:
                if (actionId == null) {
                    throw new IllegalArgumentException("Action ID is required for GET_DETAILS");
                }
                return actionRestApi.getAction(actionId).getBody();

            default:
                throw new IllegalArgumentException("Unsupported query type: " + queryType);
        }
    }
}

enum ActionQueryType {
    SEARCH,
    GET_DETAILS
}
