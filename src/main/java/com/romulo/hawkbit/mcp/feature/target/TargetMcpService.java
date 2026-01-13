package com.romulo.hawkbit.mcp.feature.target;

import java.util.List;

import org.eclipse.hawkbit.mgmt.json.model.MgmtMetadata;
import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtAction;
import org.eclipse.hawkbit.mgmt.json.model.action.MgmtActionStatus;
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

                        @McpToolParam(description = "Page offset (zero-based).", required = true) int offset,

                        @McpToolParam(description = "Page size (max 50).", required = true) int size,

                        @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
                return targetApi.getTargets(rsqlParam, offset, size, sortParam).getBody();
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

        @McpTool(name = "listTargetActions", description = """
                        Retrieve the action history for a specific target.

                        Supports pagination, sorting and FIQL-based filtering on actions.

                        For supported filter fields and operators, see:
                        hawkbit://actions/filter
                        """)
        public PagedList<MgmtAction> listTargetActions(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId,

                        @McpToolParam(description = "FIQL filter expression for actions (optional).", required = false) String rsqlParam,

                        @McpToolParam(description = "Page offset (zero-based).", required = true) int offset,

                        @McpToolParam(description = "Page size (max 50).", required = true) int size,

                        @McpToolParam(description = "Sort parameter (e.g. id:DESC).", required = false) String sort) {
                return targetApi
                                .getActionHistory(controllerId, rsqlParam, offset, size, sort)
                                .getBody();
        }

        @McpTool(name = "listTargetActionStatus", description = """
                        Retrieve the status history of a specific action on a specific target.

                        This endpoint returns the chronological list of intermediate and reported
                        statuses generated during the execution of an update action, including
                        system-generated events and device feedbacks.
                        """)
        public PagedList<MgmtActionStatus> listActionStatus(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId,

                        @McpToolParam(description = "ID of the action.", required = true) Long actionId,

                        @McpToolParam(description = "Page offset (zero-based).", required = true) int offset,

                        @McpToolParam(description = "Page size.", required = true) int size,

                        @McpToolParam(description = "Sort parameter (e.g. timestamp:ASC).", required = false) String sort) {
                return targetApi
                                .getActionStatusList(controllerId, actionId, offset, size, sort)
                                .getBody();
        }

        @McpTool(name = "getTargetActionById", description = """
                        Retrieve a specific action for a specific target.

                        This endpoint returns the current, aggregated state of the action,
                        including its status, detailed status, rollout context and the
                        last reported status code from the device.

                        For the chronological execution history of this action,
                        use the listActionStatus tool.
                        """)
        public MgmtAction getTargetAction(
                        @McpToolParam(description = "Controller ID of the target.", required = true) String controllerId,

                        @McpToolParam(description = "ID of the action.", required = true) Long actionId) {
                return targetApi
                                .getAction(controllerId, actionId)
                                .getBody();
        }

}
