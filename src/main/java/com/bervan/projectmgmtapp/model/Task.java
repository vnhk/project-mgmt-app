package com.bervan.projectmgmtapp.model;

import com.bervan.common.model.BervanBaseEntity;
import com.bervan.common.model.PersistableTableData;
import com.bervan.common.model.VaadinTableColumn;
import com.bervan.history.model.HistoryCollection;
import com.bervan.history.model.HistorySupported;
import com.bervan.ieentities.ExcelIEEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@HistorySupported
public class Task extends BervanBaseEntity<UUID> implements PersistableTableData<UUID>, ExcelIEEntity<UUID> {
    public static final String Task_status_columnName = "status";
    public static final String Task_type_columnName = "type";
    public static final String Task_name_columnName = "name";
    public static final String Task_description_columnName = "description";
    public static final String Task_priority_columnName = "priority";
    public static final String Task_dueDate_columnName = "dueDate";

    @Id
    private UUID id;
    @Size(min = 4, max = 200)
    @VaadinTableColumn(displayName = "Name", internalName = Task_name_columnName)
    private String name;
    @Size(min = 4, max = 30)
    private String number;
    private boolean deleted;

    @VaadinTableColumn(displayName = "Status", internalName = Task_status_columnName, strValues = {"Open", "In Progress", "Done", "Canceled"}, defaultValue = "Open")
    @Size(min = 4, max = 20)
    private String status;

    @VaadinTableColumn(displayName = "Type", internalName = Task_type_columnName, strValues = {"Task", "Bug", "Story", "Objective", "Feature"}, defaultValue = "Task")
    @Size(min = 4, max = 15)
    private String type;

    @VaadinTableColumn(displayName = "Priority", internalName = Task_priority_columnName, strValues = {"Low", "Medium", "High", "Critical"}, defaultValue = "Medium")
    @Size(min = 4, max = 15)
    private String priority;

    @Lob
    @Size(max = 5000000)
    @Column(columnDefinition = "MEDIUMTEXT")
    @VaadinTableColumn(displayName = "Description", internalName = Task_description_columnName, isWysiwyg = true)
    private String description;

    @VaadinTableColumn(displayName = "Due Date", internalName = Task_dueDate_columnName)
    private LocalDateTime dueDate;

    private LocalDateTime modificationDate;

    @ManyToOne
    @NotNull
    private Project project;

    @OneToMany(mappedBy = "child", fetch = FetchType.EAGER)
    private Set<TaskRelation> childRelationships = new HashSet<>();

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER)
    private Set<TaskRelation> parentRelationships = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @HistoryCollection(historyClass = HistoryTask.class)
    private Set<HistoryTask> history = new HashSet<>();

    public Set<TaskRelation> getRelationships() {
        Set<TaskRelation> r = new HashSet<>();
        r.addAll(childRelationships != null ? childRelationships : new ArrayList<>());
        r.addAll(parentRelationships != null ? parentRelationships : new ArrayList<>());
        return r;
    }

    public Task() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHistory(Set<HistoryTask> history) {
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @Override
    public String getTableFilterableColumnValue() {
        return number;
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

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Set<TaskRelation> getChildRelationships() {
        return childRelationships;
    }

    public void setChildRelationships(Set<TaskRelation> childRelationships) {
        this.childRelationships = childRelationships;
    }

    public Set<TaskRelation> getParentRelationships() {
        return parentRelationships;
    }

    public void setParentRelationships(Set<TaskRelation> parentRelationships) {
        this.parentRelationships = parentRelationships;
    }

    public Set<HistoryTask> getHistory() {
        return history;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
