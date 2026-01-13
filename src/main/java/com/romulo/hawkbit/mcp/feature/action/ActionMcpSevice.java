package com.romulo.hawkbit.mcp.feature.action;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtAction;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtActionRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class ActionMcpSevice {
    private final MgmtActionRestApi actionApi;

    public ActionMcpSevice(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.actionApi = hawkbitClient.mgmtService(MgmtActionRestApi.class, tenant);
    }

    @McpTool(name = "listActions", description = """
            Retrieve actions across all targets.

            Supports pagination, sorting and FIQL-based filtering.

            This is a global view of actions and may return actions
            belonging to multiple targets.

            For supported filter fields and operators, see:
            hawkbit://actions/filter
            """)
    public PagedList<MgmtAction> listActions(
            @McpToolParam(description = "FIQL filter expression for actions (optional).", required = false) String rsqlParam,

            @McpToolParam(description = "Page offset (zero-based).", required = true) int offset,

            @McpToolParam(description = "Page size (max 50).", required = true) int size,

            @McpToolParam(description = "Sort parameter (e.g. id:DESC).", required = false) String sort) {
        return actionApi.getActions(rsqlParam, offset, size, sort, "compact").getBody();
    }

    @McpTool(name = "getActionById", description = """
            Retrieve a single action by its ID (global scope).

            This endpoint returns the aggregated state of the action
            independently of the target it belongs to.

            The response includes status, detailed status, rollout context,
            force type and HAL links to the related target and distribution set.

            Use this tool when the action ID is already known and the target
            context is not required.
            """)
    public MgmtAction getActionById(
            @McpToolParam(description = "ID of the action.", required = true) Long actionId) {
        return actionApi.getAction(actionId).getBody();
    }

}
