/*
 * Scripted REST API resource: GET /api/<scope>/release/readiness?version=<v>
 *
 * Rule: a release is ready when every Release Phase flagged as a
 * "release readiness phase" for that release is in the complete state.
 *
 * Paste into: System Web Services > Scripted REST APIs > <your API> > Resources
 * (HTTP method: GET, relative path: /release/readiness).
 *
 * Table/field names and state values were taken from the lab8115 export.
 */
(function process(/*RESTAPIRequest*/ request, /*RESTAPIResponse*/ response) {

    var CONFIG = {
        VERSION_TABLE: 'cmdb_software_component_model',   // version records (sys_class_name in the export)
        VERSION_NAME_FIELD: 'version',                    // holds the version string, e.g. S24.0
        RELEASE_TABLE: 'sn_dpr_model_release',            // Release
        RELEASE_VERSION_FIELD: 'product_version',         // Release > Product version (reference, stores sys_id)
        PHASE_TABLE: 'sn_dpr_model_release_phase',        // Release Phase
        PHASE_RELEASE_FIELD: 'release',                   // Release Phase > Release
        PHASE_READINESS_FLAG: 'readiness_phase',          // Release Phase > Readiness phase (boolean)
        PHASE_STATE_FIELD: 'state',                       // Release Phase > State
        COMPLETE_STATE: 'completed'                       // value of the complete state (not 'complete')
    };

    var version = request.queryParams.version;
    if (!version) {
        response.setStatus(400);
        return { error: 'The version query parameter is required' };
    }

    // Product version is a reference, so resolve the version name to its sys_id first.
    var versionRecord = new GlideRecord(CONFIG.VERSION_TABLE);
    versionRecord.addQuery(CONFIG.VERSION_NAME_FIELD, version);
    versionRecord.setLimit(2);
    versionRecord.query();
    var versionIds = [];
    while (versionRecord.next()) {
        versionIds.push(versionRecord.getUniqueValue());
    }
    if (versionIds.length === 0) {
        response.setStatus(404);
        return { version: version, ready: false, state: 'not_found',
                 blockers: ['No product version named ' + version] };
    }
    if (versionIds.length > 1) {
        response.setStatus(409);
        return { version: version, ready: false, state: 'ambiguous',
                 blockers: ['More than one product version is named ' + version] };
    }

    var release = new GlideRecord(CONFIG.RELEASE_TABLE);
    release.addQuery(CONFIG.RELEASE_VERSION_FIELD, versionIds[0]);
    release.setLimit(2);
    release.query();
    if (!release.next()) {
        response.setStatus(404);
        return { version: version, ready: false, state: 'not_found',
                 blockers: ['No release found for version ' + version] };
    }
    if (release.next()) {
        response.setStatus(409);
        return { version: version, ready: false, state: 'ambiguous',
                 blockers: ['More than one release exists for version ' + version] };
    }

    var phases = new GlideRecord(CONFIG.PHASE_TABLE);
    phases.addQuery(CONFIG.PHASE_RELEASE_FIELD, release.getUniqueValue());
    phases.addQuery(CONFIG.PHASE_READINESS_FLAG, true);
    phases.query();

    var blockers = [];
    var phaseCount = 0;
    while (phases.next()) {
        phaseCount++;
        if (String(phases.getValue(CONFIG.PHASE_STATE_FIELD)) !== CONFIG.COMPLETE_STATE) {
            blockers.push(phases.getDisplayValue('name') + ' is ' +
                          phases.getDisplayValue(CONFIG.PHASE_STATE_FIELD));
        }
    }

    if (phaseCount === 0) {
        blockers.push('No release readiness phase is flagged for this release');
    }

    var ready = phaseCount > 0 && blockers.length === 0;

    return {
        version: version,
        ready: ready,
        state: ready ? 'complete' : 'in_progress',
        blockers: blockers
    };

})(request, response);
