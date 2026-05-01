package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.model.FieldCustomMapper;
import com.bervan.projectmgmtapp.model.Task;
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
public final class TaskCreateRequest implements BaseDTO<UUID> {
    private String name;
    private String status;
    private String type;
    private String priority;
    private String description;
    private LocalDateTime dueDate;
    private String assignee;
    private Double estimatedHours;
    private String tags;

    @FieldCustomMapper(mapper = ToProjectMapper.class, targetFieldName = "project")
    private UUID projectId;

    @Override
    public void setId(UUID uuid) {
    }

    @Override
    public UUID getId() {
        return null;
    }

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return Task.class;
    }
}
