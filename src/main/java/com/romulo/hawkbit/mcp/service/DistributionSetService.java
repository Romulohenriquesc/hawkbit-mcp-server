package com.romulo.hawkbit.mcp.service;

import org.eclipse.hawkbit.mgmt.json.model.MgmtMetadata;
import org.eclipse.hawkbit.mgmt.json.model.PagedList;
import org.eclipse.hawkbit.mgmt.json.model.distributionset.MgmtDistributionSet;
import org.eclipse.hawkbit.mgmt.json.model.softwaremodule.MgmtSoftwareModule;
import org.eclipse.hawkbit.mgmt.json.model.target.MgmtTarget;
import org.eclipse.hawkbit.mgmt.json.model.targetfilter.MgmtTargetFilterQuery;
import org.eclipse.hawkbit.mgmt.rest.api.MgmtDistributionSetRestApi;
import org.eclipse.hawkbit.sdk.HawkbitClient;
import org.eclipse.hawkbit.sdk.Tenant;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class DistributionSetService {

    private final MgmtDistributionSetRestApi distributionSetRestApi;

    public DistributionSetService(final HawkbitClient hawkbitClient, final Tenant tenant) {
        this.distributionSetRestApi = hawkbitClient.mgmtService(MgmtDistributionSetRestApi.class, tenant);
    }

    @McpTool(name = "getDistributionSets", description = "Get all Distribution Sets")
    PagedList<MgmtDistributionSet> getDistributionSets(
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter. Only if necessary, consult getDistributionSetSearchFields with the available fields.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc. Can be use the same sort parameter as the getDistributionSetSearchFields.", required = false) String sortParam) {
        return distributionSetRestApi.getDistributionSets(rsqlParam, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getDistributionSet", description = "Get a Distribution Set by ID")
    MgmtDistributionSet getDistributionSet(
            @McpToolParam(description = "Distribution Set ID", required = true) Long id) {
        return distributionSetRestApi.getDistributionSet(id).getBody();
    }

    @McpTool(name = "getDistributionSetMetadata", description = "Get a paged list of meta data for a distribution set")
    PagedList<MgmtMetadata> getDistributionSetMetadata(
            @McpToolParam(description = "Distribution Set ID", required = true) Long distributionSetId) {
        return distributionSetRestApi.getMetadata(distributionSetId).getBody();
    }

    @McpTool(name = "getAssignedTargetsByDistributionSet", description = "Return assigned targets to a specific distribution set")
    PagedList<MgmtTarget> getAssignedTargetsByDistributionSet(
            @McpToolParam(description = "Distribution Set ID", required = true) Long distributionSetId,
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter. Only if necessary, consult getAssignedTargetsSearchFields with the available fields.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc. Can be use the same sort parameter as the getAssignedTargetsSearchFields.", required = false) String sortParam) {
        return distributionSetRestApi.getAssignedTargets(distributionSetId, rsqlParam, offset, limit, sortParam)
                .getBody();
    }

    @McpTool(name = "getAssignedSoftwareModules", description = "Return the assigned software modules of a specific distribution set")
    PagedList<MgmtSoftwareModule> getAssignedSoftwareModules(
            @McpToolParam(description = "Distribution Set ID", required = true) Long distributionSetId,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return distributionSetRestApi.getAssignedSoftwareModules(distributionSetId, offset, limit, sortParam).getBody();
    }

    @McpTool(name = "getDistributionSetStatistics", description = "Get statistics for Distribution Sets. Use action to specify the type of statistics.")
    Object getDistributionSetStatistics(
            @McpToolParam(description = "The type of statistics to retrieve (SINGLE, SINGLE_ROLLOUTS, SINGLE_ACTIONS, SINGLE_AUTO_ASSIGNMENTS)", required = true) StatisticsAction action,
            @McpToolParam(description = "Distribution Set ID (Required for SINGLE types)", required = true) Long distributionSetId) {

        switch (action) {
            case SINGLE:
                return distributionSetRestApi.getStatisticsForDistributionSet(distributionSetId).getBody();

            case SINGLE_ROLLOUTS:
                return distributionSetRestApi.getRolloutsCountByStatusForDistributionSet(distributionSetId).getBody();

            case SINGLE_ACTIONS:
                return distributionSetRestApi.getActionsCountByStatusForDistributionSet(distributionSetId)
                        .getBody();

            case SINGLE_AUTO_ASSIGNMENTS:
                return distributionSetRestApi.getAutoAssignmentsCountForDistributionSet(distributionSetId)
                        .getBody();

            default:
                throw new IllegalArgumentException("Unsupported action: " + action);
        }
    }

    @McpTool(name = "getInstalledTargets", description = "Return installed targets of a single distribution set")
    PagedList<MgmtTarget> getInstalledTargets(
            @McpToolParam(description = "Distribution Set ID", required = true) Long distributionSetId,
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter. Uses the same filter fields as Targets.", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return distributionSetRestApi.getInstalledTargets(distributionSetId, rsqlParam, offset, limit, sortParam)
                .getBody();
    }

    @McpTool(name = "getAutoAssignTargetFilterQueries", description = "Return target filter queries that have the given distribution set as auto assign DS")
    PagedList<MgmtTargetFilterQuery> getAutoAssignTargetFilterQueries(
            @McpToolParam(description = "Distribution Set ID", required = true) Long distributionSetId,
            @McpToolParam(description = "Feed Item Query Language (FIQL) search filter. Available fields: [id, name, autoassigndistributionset.name, autoassigndistributionset.version]", required = false) String rsqlParam,
            @McpToolParam(description = "Offset", required = true) int offset,
            @McpToolParam(description = "Limit. Max value: 50", required = true) int limit,
            @McpToolParam(description = "Sort parameter. Example: name:asc.", required = false) String sortParam) {
        return distributionSetRestApi
                .getAutoAssignTargetFilterQueries(distributionSetId, rsqlParam, offset, limit, sortParam).getBody();
    }

}

enum StatisticsAction {
    SINGLE,
    SINGLE_ROLLOUTS,
    SINGLE_ACTIONS,
    SINGLE_AUTO_ASSIGNMENTS
}
