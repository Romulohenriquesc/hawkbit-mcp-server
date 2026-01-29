package com.romulo.hawkbit.mcp.feature.enumeration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum TargetUpdateStatus {
    UNKNOWN, IN_SYNC, PENDING, ERROR, REGISTERED;

    public static String getStatuses() {
        return Stream.of(values()).map(Enum::name).collect(Collectors.joining(","));
    }
}
