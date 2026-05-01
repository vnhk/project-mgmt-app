package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.projectmgmtapp.model.Task;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Flat DTO for task list and update. projectId/projectNumber are set manually
 * in the controller — DTOMapper does not map renamed fields (project→projectId).
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class TaskDto implements BaseDTO<UUID> {
    private UUID id;
    private String name;
    private String number;
    private String status;
    private String type;
    private String priority;
    private String description;
    private LocalDateTime dueDate;
    private String assignee;
    private Double estimatedHours;
    private Integer completionPercentage;
    private String tags;
    private LocalDateTime modificationDate;
    private UUID projectId;
    private String projectNumber;

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
        return Task.class;
    }
}
