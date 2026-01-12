package com.romulo.hawkbit.mcp.feature.enumeration;

import lombok.Getter;

@Getter
public enum Operator {
    EQUALS("==",
            "Exact match. Also supports '*' as wildcard (e.g., 'controllerid==*target*' for contains, 'controllerid==target*' for starts with)."),

    NOT_EQUALS("!=", "Not equals. Filters out targets that contain this exact value."),

    GREATER_OR_EQUAL("=ge=", "Greater than or equal to. Commonly used for timestamps or version numbers."),

    LESS_OR_EQUAL("=le=", "Less than or equal to. Commonly used for timestamps (e.g., finding overdue devices)."),

    IN("=in=",
            "In list. Matches if the value is present in the provided comma-separated list. Example: tag=in=(test,qa)."),

    OUT("=out=", "Out of list. Matches if the value is NOT present in the provided list. Example: tag=out=(test,qa)."),

    IS_NULL("=is=null", "Is Null. Matches targets where the field or attribute does not exist or is null."),

    IS_NOT_NULL("=not=null", "Is Not Null. Matches targets where the field or attribute exists and has any value."),

    AND(";", "Logical AND. Combines two conditions where both must be true. (Text alias 'and' is also supported)."),

    OR(",", "Logical OR. Combines two conditions where at least one must be true. (Text alias 'or' is also supported).");

    private final String symbol;
    private final String description;

    Operator(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public static String toDocumentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("| Operator | Symbol | Description |\n");
        sb.append("| :--- | :--- | :--- |\n");
        for (Operator op : values()) {
            sb.append(String.format("| %s | `%s` | %s |\n", op.name(), op.symbol, op.description));
        }
        return sb.toString();
    }
}
