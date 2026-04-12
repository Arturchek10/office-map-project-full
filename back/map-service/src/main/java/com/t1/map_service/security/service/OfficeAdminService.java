package com.t1.map_service.security.service;

import com.t1.map_service.dto.office_admin.PromoteProjectAdminRequest;
import com.t1.map_service.dto.office_admin.RevokeProjectAdminRequest;

public interface OfficeAdminService {

    void promoteProjectAdmin(Long officeId, PromoteProjectAdminRequest request);

    void revokeProjectAdmin(Long officeId, RevokeProjectAdminRequest request);
}
