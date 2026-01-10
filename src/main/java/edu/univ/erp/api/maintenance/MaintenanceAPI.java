package edu.univ.erp.api.maintenance;

import edu.univ.erp.access.AccessControl;

public class MaintenanceAPI {
    public boolean isMaintenanceMode() {
        return AccessControl.isMaintenanceMode();
    }

    public boolean isReadOnlyNow() {
        return AccessControl.isMaintenanceMode() && !AccessControl.canAccessAsAdmin();
    }
}

