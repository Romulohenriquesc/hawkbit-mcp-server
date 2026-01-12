package com.romulo.hawkbit.mcp.feature.target;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class TargetMcpService {

    private final MgmtTargetRestApi targetApi;

    public TargetMcpService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.targetApi = hawkbitClient.mgmtService(MgmtTargetRestApi.class, tenant);
    }

    @McpTool(name = "listTargets", description = """
            Search for targets (devices) with pagination.
            Supports complex FIQL queries.

            AVAILABLE FIELDS FOR FILTERING:
            """ + TargetSchemas.TARGET_SEARCH_FIELDS
            + """

                    For advanced fields or sort parameters (attributes, metadata, tags, targettype, installedds, assignedds) and operators,
                    read the resource: hawkbit://targets/filter-manual
                    """)
    PagedList<MgmtTarget> getTargets(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter.  Ex: 'updatestatus==ERROR'.", required = false) String rsqlParam,
            @McpToolParam(description = "page", required = true) int page,
            @McpToolParam(description = "size. Max value: 50", required = true) int size,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return targetApi.getTargets(rsqlParam, page, size, sortParam).getBody();
    }
}
