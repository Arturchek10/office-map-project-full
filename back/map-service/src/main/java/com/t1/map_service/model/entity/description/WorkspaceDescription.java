package com.t1.map_service.model.entity.description;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "workspace_desc")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class WorkspaceDescription extends Description {

    private Boolean haveComputer;

    // TODO БРОНИРОВАНИЕ
}
