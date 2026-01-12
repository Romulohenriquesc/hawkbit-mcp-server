package com.romulo.hawkbit.mcp.service;

import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.softwaremodule.MgmtSoftwareModule;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtRestConstants;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtSoftwareModuleRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class SoftwareModuleService {

    private final MgmtSoftwareModuleRestApi mgmtSoftwareModuleRestApi;

    public SoftwareModuleService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.mgmtSoftwareModuleRestApi = hawkbitClient.mgmtService(MgmtSoftwareModuleRestApi.class, tenant);
    }

    @McpTool(name = "getSoftwareModules", description = "Get all Software Modules")
    PagedList<MgmtSoftwareModule> getSoftwareModules(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter. Available fields: [id, name, description, version, metadata.keyName, type.key]", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc. Can be use the same parameters from search filter.", required = false) String sortParam) {
        return mgmtSoftwareModuleRestApi.getSoftwareModules(rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getSoftwareModuleById", description = "Get Software Module by ID")
    MgmtSoftwareModule getSoftwareModuleById(
            @McpToolParam(description = "Software Module ID", required = true) Long id) {
        return mgmtSoftwareModuleRestApi.getSoftwareModule(id).getBody();
    }

    @McpTool(name = "getSoftwareModuleMetadata", description = "Get metadata for a Software Module. Returns a list if no key is provided, or a single value if key is provided.")
    public Object getSoftwareModuleMetadata(
            @McpToolParam(description = "Software Module ID", required = true) Long softwareModuleId,
            @McpToolParam(description = "Metadata key. If provided, returns value for this key. If null, returns all metadata.", required = false) String key) {
        if (key != null) {
            return mgmtSoftwareModuleRestApi.getMetadataValue(softwareModuleId, key).getBody();
        } else {
            return mgmtSoftwareModuleRestApi.getMetadata(softwareModuleId).getBody();
        }
    }

    @McpTool(name = "getSoftwareModuleArtifacts", description = "Get artifacts details for a Software Module. Returns a list if no artifactId is provided, or a single artifact detail if ID is provided.")
    public Object getSoftwareModuleArtifacts(
            @McpToolParam(description = "Software Module ID", required = true) Long softwareModuleId,
            @McpToolParam(description = "Artifact ID. If provided, returns the specific artifact details. If null, returns the list of artifacts.", required = false) Long artifactId) {

        if (artifactId != null) {

            return mgmtSoftwareModuleRestApi.getArtifact(softwareModuleId, artifactId, null).getBody();
        } else {

            return mgmtSoftwareModuleRestApi.getArtifacts(
                    softwareModuleId,
                    MgmtRestConstants.REQUEST_PARAMETER_REPRESENTATION_MODE_DEFAULT,
                    null).getBody();
        }
    }
}
