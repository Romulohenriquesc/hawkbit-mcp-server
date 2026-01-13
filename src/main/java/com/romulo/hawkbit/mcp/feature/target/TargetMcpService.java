package com.romulo.hawkbit.mcp.feature.target;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.MgmtMetadata;
import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.tag.MgmtTag;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTargetAttributes;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtTargetRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class TargetMcpService {

        private final MgmtTargetRestApi targetApi;

        public TargetMcpService(final HawkbitClient hawkbitClient, final Tenant tenant) {
                this.targetApi = hawkbitClient.mgmtService(MgmtTargetRestApi.class, tenant);
        }

        @McpTool(name = "listTargets", description = """
                        Search for targets (devices) with pagination.
                        Supports FIQL filters.

                        Filter schema:
                        hawkbit://targets/filter
                        """)
        PagedList<MgmtTarget> getTargets(
                        @McpToolParam(description = "FIQL filter expression. Example: updatestatus==ERROR", required = false) String rsqlParam,

                        @McpToolParam(description = "Page number (0-based).", required = true) int page,

                        @McpToolParam(description = "Page size (max 50).", required = true) int size,

                        @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
                return targetApi.getTargets(rsqlParam, page, size, sortParam).getBody();
        }

        @McpTool(name = "getTargetById", description = """
                        Retrieve a single target (device) by its unique ID.

                        Returns the full target representation, including status,
                        target type, poll status and HAL links.
                        """)
        public MgmtTarget getTargetById(
                        @McpToolParam(description = "Controller ID of the target (unique identifier used for all target interactions).", required = true) String controllerId) {
                return targetApi.getTarget(controllerId).getBody();
        }

        @McpTool(name = "getTargetMetadata", description = """
                        Retrieve metadata for a specific target.
                        Use this tool only after identifying a target of interest.
                        For filtering targets by metadata, use listTargets with metadata.<key> filters.
                        """)
        public PagedList<MgmtMetadata> getTargetMetadata(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId) {
                return targetApi.getMetadata(controllerId).getBody();
        }

        @McpTool(name = "getTargetMetadataValue", description = """
                        Retrieve a single metadata entry for a specific target and metadata key.

                        This tool returns only one metadata key/value pair and should be preferred
                        over listing all metadata when the key is known.
                        """)
        public MgmtMetadata getTargetMetadataValue(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId,

                        @McpToolParam(description = "Metadata key to retrieve the value for.", required = true) String metadataKey) {
                return targetApi.getMetadataValue(controllerId, metadataKey).getBody();
        }

        @McpTool(name = "getTargetAttributes", description = """
                        Retrieve runtime attributes reported by a target itself via the DDI API.

                        Attributes represent device-reported characteristics and state, and are
                        read-only from the management perspective.

                        Use this tool only after identifying a specific target.
                        For filtering targets by attributes, use listTargets with attribute.<key> filters.
                        """)
        public MgmtTargetAttributes getTargetAttributes(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId) {
                return targetApi.getAttributes(controllerId).getBody();
        }

        @McpTool(name = "getTagsByTarget", description = """
                        Retrieve all tags assigned to a specific target.

                        Tags are used to group and categorize targets.
                        Use this tool only after identifying a specific target.
                        For filtering targets by tag, use listTargets with tag.name filters.
                        """)
        public List<MgmtTag> getTargetTags(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId) {
                return targetApi.getTags(controllerId).getBody();
        }

}
