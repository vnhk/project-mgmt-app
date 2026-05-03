package com.bervan.projectmgmtapp.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class TaskRelationDto {
    private UUID id;
    private String direction;
    private String type;
    private String displayName;
    @JsonIgnore
    private TaskDto parent;
    @JsonIgnore
    private TaskDto child;
    private UUID relatedTaskId;
    private String relatedTaskNumber;
    private String relatedTaskName;
    private String relatedTaskStatus;
    private String relatedTaskType;

    public TaskRelationDto(
            UUID id,
            String direction,
            String type,
            String displayName,
            UUID relatedTaskId,
            String relatedTaskNumber,
            String relatedTaskName,
            String relatedTaskStatus,
            String relatedTaskType
    ) {
        this.id = id;
        this.direction = direction;
        this.type = type;
        this.displayName = displayName;
        this.relatedTaskId = relatedTaskId;
        this.relatedTaskNumber = relatedTaskNumber;
        this.relatedTaskName = relatedTaskName;
        this.relatedTaskStatus = relatedTaskStatus;
        this.relatedTaskType = relatedTaskType;
    }

    public UUID id() {
        return id;
    }

    public String direction() {
        return direction;
    }

    public String type() {
        return type;
    }

    public String displayName() {
        return displayName;
    }

    public UUID relatedTaskId() {
        return relatedTaskId;
    }

    public String relatedTaskNumber() {
        return relatedTaskNumber;
    }

    public String relatedTaskName() {
        return relatedTaskName;
    }

    public String relatedTaskStatus() {
        return relatedTaskStatus;
    }

    public String relatedTaskType() {
        return relatedTaskType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TaskRelationDto) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.direction, that.direction) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.displayName, that.displayName) &&
                Objects.equals(this.relatedTaskId, that.relatedTaskId) &&
                Objects.equals(this.relatedTaskNumber, that.relatedTaskNumber) &&
                Objects.equals(this.relatedTaskName, that.relatedTaskName) &&
                Objects.equals(this.relatedTaskStatus, that.relatedTaskStatus) &&
                Objects.equals(this.relatedTaskType, that.relatedTaskType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, direction, type, displayName, relatedTaskId, relatedTaskNumber, relatedTaskName, relatedTaskStatus, relatedTaskType);
    }

    @Override
    public String toString() {
        return "TaskRelationDto[" +
                "id=" + id + ", " +
                "direction=" + direction + ", " +
                "type=" + type + ", " +
                "displayName=" + displayName + ", " +
                "relatedTaskId=" + relatedTaskId + ", " +
                "relatedTaskNumber=" + relatedTaskNumber + ", " +
                "relatedTaskName=" + relatedTaskName + ", " +
                "relatedTaskStatus=" + relatedTaskStatus + ", " +
                "relatedTaskType=" + relatedTaskType + ']';
    }

}
