package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.projectmgmtapp.model.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class ProjectDto implements BaseDTO<UUID> {
    private UUID id;
    private String name;
    private String number;
    private String status;
    private String priority;
    private String description;
    private LocalDateTime modificationDate;

    @Override
    public void setId(UUID uuid) {
        this.id = uuid;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return Project.class;
    }
}
