package com.romulo.hawkbit.mcp.feature.resources.action;

import com.romulo.hawkbit.mcp.feature.enumeration.Operator;
import com.romulo.hawkbit.mcp.feature.enumeration.TargetUpdateStatus;

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

                UPDATE STATUSES:
                %s

                """.formatted(FIELDS, Operator.documentation(), TargetUpdateStatus.getStatuses());
    }
}
