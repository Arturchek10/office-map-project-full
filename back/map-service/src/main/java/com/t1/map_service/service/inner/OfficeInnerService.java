package com.t1.map_service.service.inner;

import com.t1.map_service.model.entity.Office;

public interface OfficeInnerService {

    Office getEntityById(Long officeId);

    Office getEntityByIdWithFloorsOrdered(Long officeId);

    boolean existsEntityById(Long officeId);

}
