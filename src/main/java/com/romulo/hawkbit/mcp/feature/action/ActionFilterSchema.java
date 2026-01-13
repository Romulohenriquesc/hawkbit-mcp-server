package com.romulo.hawkbit.mcp.feature.action;

import com.romulo.hawkbit.mcp.feature.enumeration.Operator;

public class ActionFilterSchema {

    public static final String FIELDS = """
            id
            status
            detailstatus
            laststatuscode
            weight
            externalref

            target.controllerId
            target.name
            target.updateStatus
            target.address

            distributionset.id
            distributionset.name
            distributionset.version
            distributionset.type

            rollout.id
            rollout.name

            rolloutgroup.id
            rolloutgroup.name
            """;

    public static String documentation() {
        return """
                FIELDS:
                %s

                %s
                """.formatted(FIELDS, Operator.documentation());
    }
}
