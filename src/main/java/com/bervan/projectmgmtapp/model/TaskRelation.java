package com.bervan.projectmgmtapp.model;

import com.bervan.common.model.BervanBaseEntity;
import com.bervan.ieentities.ExcelIEEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TaskRelation extends BervanBaseEntity<UUID> implements ExcelIEEntity<UUID> {

    @Id
    private UUID id;

    @ManyToOne
    @NotNull
    private Task parent;

    @ManyToOne
    @NotNull
    private Task child;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskRelationshipType type;

    private boolean deleted;
    private LocalDateTime modificationDate;

    public TaskRelation() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    @Override
    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public TaskRelationshipType getType() {
        return type;
    }

    public void setType(TaskRelationshipType type) {
        this.type = type;
    }

    public Task getChild() {
        return child;
    }

    public void setChild(Task child) {
        this.child = child;
    }

    public Task getParent() {
        return parent;
    }

    public void setParent(Task parent) {
        this.parent = parent;
    }
}
