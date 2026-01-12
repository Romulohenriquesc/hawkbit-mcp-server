package com.romulo.hawkbit.mcp.service;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.softwaremoduletype.MgmtSoftwareModuleType;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtSoftwareModuleTypeRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class SoftwareModuleTypeService {

    private final MgmtSoftwareModuleTypeRestApi mgmtSoftwareModuleTypeRestApi;

    public SoftwareModuleTypeService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.mgmtSoftwareModuleTypeRestApi = hawkbitClient.mgmtService(MgmtSoftwareModuleTypeRestApi.class, tenant);
    }

    @McpTool(name = "getSoftwareModuleTypes", description = "Get all Software Module Types")
    PagedList<MgmtSoftwareModuleType> getSoftwareModuleTypes(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter. Available fields: [id, key, name, description, maxassignments]", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc. Can be use the same parameters from search filter.", required = false) String sortParam) {
        return mgmtSoftwareModuleTypeRestApi.getTypes(rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getSoftwareModuleType", description = "Get a specific Software Module Type")
    MgmtSoftwareModuleType getSoftwareModuleType(
            @McpToolParam(description = "Software Module Type ID", required = true) Long id) {
        return mgmtSoftwareModuleTypeRestApi.getSoftwareModuleType(id).getBody();
    }
}
