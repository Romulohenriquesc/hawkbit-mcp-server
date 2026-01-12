package com.romulo.hawkbit.mcp.feature.target;

import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Service;

import com.romulo.hawkbit.mcp.feature.enumeration.Operator;

@Service
public class TargetSchemas {

        public static final String TARGET_SEARCH_FIELDS = "id, name, description, createdat, lastmodifiedat, controllerid, updatestatus(UNKNOWN, IN_SYNC, PENDING, ERROR, REGISTERED), ipaddress, lastcontrollerrequestat";

        private static final String TARGET_ATTRIBUTES_FIELDS = "attribute.keyName";

        private static final String TARGET_METADATA_FIELDS = "metadata.keyName";

        private static final String TARGET_TAG_FIELDS = "tag.name";

        private static final String TARGET_TYPE_FIELDS = "targettype.key, targettype.name";

        private static final String TARGET_DISTRIBUTION_SET_FIELDS = "assignedds.name, assignedds.version, installedds.name, installedds.version";

        private static final String TARGET_ATTRIBUTES_FIELDS_DESCRIPTION = """
                        Attributes are data about the target provided exclusively by the target.
                        """;

        private static final String TARGET_METADATA_FIELDS_DESCRIPTION = """
                        Metadata are data about the target provided exclusively by management.
                        """;

        private static final String TARGET_TAG_FIELDS_DESCRIPTION = """
                        Tags are used to group targets (devices).
                        """;

        private static final String TARGET_TYPE_FIELDS_DESCRIPTION = """
                         Target Type classifies targets into types.
                        """;

        private static final String TARGET_DISTRIBUTION_SET_FIELDS_DESCRIPTION = """
                        Distribution set represents the distribution that is installed or assigned to the target.
                        """;

        private static final String AVAILABLE_TARGETS_SEARCH_FIELDS = "Target fields: " + TARGET_SEARCH_FIELDS + "\n" +
                        "Attributes fields(" + TARGET_ATTRIBUTES_FIELDS_DESCRIPTION + "): " + TARGET_ATTRIBUTES_FIELDS
                        + "\n" +
                        "Metadata fields(" + TARGET_METADATA_FIELDS_DESCRIPTION + "): " + TARGET_METADATA_FIELDS + "\n"
                        +
                        "Target Tag fields(" + TARGET_TAG_FIELDS_DESCRIPTION + "): " + TARGET_TAG_FIELDS + "\n" +
                        "Target Type fields(" + TARGET_TYPE_FIELDS_DESCRIPTION + "): " + TARGET_TYPE_FIELDS + "\n" +
                        "Distribution set fields(" + TARGET_DISTRIBUTION_SET_FIELDS_DESCRIPTION + "): "
                        + TARGET_DISTRIBUTION_SET_FIELDS
                        + "example: controllerid==target-0001';name==target-0001',assignedds.version==1.0.0";

        @McpResource(uri = "hawkbit://targets/filter-manual", name = "Target Filtering Manual", description = "Documentation of available fields and operators for filtering targets (FIQL).", mimeType = "text/plain")
        public String getTargetFilterManual() {
                return """
                                # TARGET FILTER FIELDS
                                %s

                                # FILTER OPERATORS
                                %s
                                """.formatted(AVAILABLE_TARGETS_SEARCH_FIELDS, Operator.toDocumentation());
        }

}
