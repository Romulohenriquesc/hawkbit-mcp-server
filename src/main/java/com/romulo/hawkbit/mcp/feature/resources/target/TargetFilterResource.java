package com.romulo.hawkbit.mcp.feature.resources.target;

import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

@Component
public class TargetFilterResource {

    @McpResource(uri = "hawkbit://targets/filter", name = "Target Filter Schema", description = "Canonical schema for target filtering.", mimeType = "text/plain")
    public String getTargetFilterSchema() {
        return TargetFilterSchema.documentation();
    }
}