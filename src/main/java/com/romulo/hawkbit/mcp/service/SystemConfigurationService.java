package com.romulo.hawkbit.mcp.service;

import org.eclipse.hawkbit.mgmt.rest.api.MgmtTenantManagementRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class SystemConfigurationService {

    private final MgmtTenantManagementRestApi mgmtTenantManagementRestApi;

    public SystemConfigurationService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.mgmtTenantManagementRestApi = hawkbitClient.mgmtService(MgmtTenantManagementRestApi.class, tenant);
    }

    @McpTool(name = "getSystemConfigurations", description = "Get tenant system configurations. Returns all configurations (map) if no key is provided, or a single configuration value object if a key is provided.")
    public Object getSystemConfigurations(
            @McpToolParam(description = "Configuration Key Name (e.g., 'repository.quota.bytes'). If provided, returns the value for this specific key. If null, returns all configurations.", required = false) String keyName) {

        if (keyName != null && !keyName.isBlank()) {
            return mgmtTenantManagementRestApi.getTenantConfigurationValue(keyName).getBody();
        } else {
            return mgmtTenantManagementRestApi.getTenantConfiguration().getBody();
        }
    }
}
