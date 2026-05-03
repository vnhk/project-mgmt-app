package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.BaseDTO;
import com.bervan.core.model.BaseModel;
import com.bervan.projectmgmtapp.model.TaskRelation;
import com.bervan.projectmgmtapp.model.TaskRelationshipType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRelationDto implements BaseDTO<UUID> {
    private UUID id;
    private String direction;
    private TaskRelationshipType type;
    private String displayName;
    private UUID relatedTaskId;
    private String relatedTaskNumber;
    private String relatedTaskName;
    private String relatedTaskStatus;
    private String relatedTaskType;
    @JsonIgnore
    private TaskDto parent;
    @JsonIgnore
    private TaskDto child;

    @Override
    public Class<? extends BaseModel<UUID>> dtoTarget() {
        return TaskRelation.class;
    }
}
