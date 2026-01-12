package com.romulo.hawkbit.mcp.feature.target;

import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

import com.romulo.hawkbit.mcp.feature.enumeration.Operator;

@Component
public class TargetFilterDoc {

    @McpResource(uri = "hawkbit://targets/filter-operators", name = "Target Filter Operators", description = "List of valid operators for filtering targets.", mimeType = "text/plain")
    public String getFilterOperators() {
        return Operator.toDocumentation();
    }
}
