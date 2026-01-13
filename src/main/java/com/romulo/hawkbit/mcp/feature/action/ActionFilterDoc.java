package com.romulo.hawkbit.mcp.feature.action;

import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

@Component
public class ActionFilterDoc {

    @McpResource(uri = "hawkbit://actions/filter", name = "Action Filter Schema", description = "Canonical schema for filtering target actions.", mimeType = "text/plain")
    public String getActionFilterSchema() {
        return ActionFilterSchema.documentation();
    }
}