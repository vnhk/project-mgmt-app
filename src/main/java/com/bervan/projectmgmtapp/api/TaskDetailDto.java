package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.core.model.FieldMapperConfig;
import com.bervan.core.model.PostCustomMappers;
import com.bervan.projectmgmtapp.model.Task;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PostCustomMappers(mappers = {TaskToDetailsPostMapper.class})
public class TaskDetailDto implements BaseDTO<UUID> {
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
    @FieldMapperConfig(targetFieldNames = "project.id")
    private UUID projectId;
    @FieldMapperConfig(targetFieldNames = "project.number")
    private String projectNumber;
    @FieldMapperConfig(targetFieldNames = "project.name")
    private String projectName;
    @JsonIgnore
    private Set<TaskRelationDto> parentRelationships;
    @JsonIgnore
    private Set<TaskRelationDto> childRelationships;
    private List<TaskRelationDto> relations;

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return Task.class;
    }
}
