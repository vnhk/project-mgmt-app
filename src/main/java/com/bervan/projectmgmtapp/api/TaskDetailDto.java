package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.FieldMapperConfig;
import com.bervan.core.model.FieldMapperConfig;
import com.bervan.core.model.PostCustomMappers;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PostCustomMappers(mappers = {TaskToDetailsPostMapper.class})
public class TaskDetailDto {
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
    private List<TaskRelationDto> parentRelationships;
    @JsonIgnore
    private List<TaskRelationDto> childRelationships;
    private List<TaskRelationDto> relations;
}
