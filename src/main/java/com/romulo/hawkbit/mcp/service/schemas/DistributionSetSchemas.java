package com.romulo.hawkbit.mcp.service.schemas;

import org.springaicommunity.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

@Service
public class DistributionSetSchemas {

        private static final String DISTRIBUTION_SET_SEARCH_FIELDS = "The given search parameter field {a} must be one of the following fields [id, name, description, createdat, lastmodifiedat, version, complete, metadata.keyName, valid, type.key, module.id, module.name, tag.name]";

        private static final String ASSIGNED_TARGETS_SEARCH_FIELDS = "The given search parameter field {a} must be one of the following fields [id, name, description, createdat, lastmodifiedat, controllerid, updatestatus, ipaddress, attribute.keyName, lastcontrollerrequestat, metadata.keyName, assignedds.name, assignedds.version, installedds.name, installedds.version, tag.name, targettype.key, targettype.name]";

        @McpTool(name = "getDistributionSetSearchFields", description = "Distribution set search fields and their relationships")
        public static String getDistributionSetSearchFields() {
                return DISTRIBUTION_SET_SEARCH_FIELDS;
        }

        @McpTool(name = "getAssignedTargetsSearchFields", description = "Assigned targets search fields and their relationships")
        public static String getAssignedTargetsSearchFields() {
                return ASSIGNED_TARGETS_SEARCH_FIELDS;
        }

}
