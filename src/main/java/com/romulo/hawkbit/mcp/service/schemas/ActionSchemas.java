package com.romulo.hawkbit.mcp.service.schemas;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

@Service
public class ActionSchemas {

    private static final String ACTION_SEARCH_FIELDS = "id, active, status, lastActionStatusCode, createdAt, createdBy, lastModifiedAt, lastModifiedBy, weight, externalRef";

    private static final String ACTION_TARGET_FIELDS = "target.controllerId, target.name, target.updateStatus, target.address";

    private static final String ACTION_DISTRIBUTION_SET_FIELDS = "distributionSet.id, distributionSet.name, distributionSet.version, distributionSet.type";

    private static final String ACTION_ROLLOUT_FIELDS = "rollout.id, rollout.name";

    private static final String ACTION_ROLLOUT_GROUP_FIELDS = "rolloutGroup.id, rolloutGroup.name";

    private static final String ACTION_TARGET_FIELDS_DESCRIPTION = """
            Target fields associated with the action.
            """;

    private static final String ACTION_DISTRIBUTION_SET_FIELDS_DESCRIPTION = """
            Distribution set fields associated with the action.
            """;

    private static final String ACTION_ROLLOUT_FIELDS_DESCRIPTION = """
            Rollout fields associated with the action.
            """;

    private static final String ACTION_ROLLOUT_GROUP_FIELDS_DESCRIPTION = """
            Rollout group fields associated with the action.
            """;

    private static final String AVAILABLE_ACTIONS_SEARCH_FIELDS = "Action fields: " + ACTION_SEARCH_FIELDS + "\n" +
            "Target fields(" + ACTION_TARGET_FIELDS_DESCRIPTION + "): " + ACTION_TARGET_FIELDS + "\n" +
            "Distribution Set fields(" + ACTION_DISTRIBUTION_SET_FIELDS_DESCRIPTION + "): "
            + ACTION_DISTRIBUTION_SET_FIELDS + "\n" +
            "Rollout fields(" + ACTION_ROLLOUT_FIELDS_DESCRIPTION + "): " + ACTION_ROLLOUT_FIELDS + "\n" +
            "Rollout Group fields(" + ACTION_ROLLOUT_GROUP_FIELDS_DESCRIPTION + "): " + ACTION_ROLLOUT_GROUP_FIELDS
            + "\n" +
            "example: status==ACTIVE;target.controllerId=='0001'";

    @McpTool(name = "getActionHistorySearchFields", description = "Action history search fields and their relationships")
    public static String getActionHistorySearchFields() {
        return AVAILABLE_ACTIONS_SEARCH_FIELDS;
    }
}
