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
import org.checkerframework.common.aliasing.qual.Unique;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@HistorySupported
public class Project extends BervanBaseEntity<UUID> implements PersistableTableData<UUID>, ExcelIEEntity<UUID> {
    public static final String Project_name_columnName = "name";
    public static final String Project_number_columnName = "number";
    public static final String Project_status_columnName = "status";
    public static final String Project_description_columnName = "description";
    public static final String Project_priority_columnName = "priority";

    @Id
    private UUID id;
    @Size(min = 4, max = 200)
    @VaadinTableColumn(displayName = "Name", internalName = Project_name_columnName)
    private String name;
    @Unique
    @Size(min = 2, max = 20)
    @VaadinTableColumn(displayName = "Number", internalName = Project_number_columnName)
    private String number;
    private boolean deleted;

    @VaadinTableColumn(displayName = "Status", internalName = Project_status_columnName, strValues = {"Open", "In Progress", "Done", "Canceled"}, defaultValue = "Open")
    @Size(min = 4, max = 20)
    private String status;

    @VaadinTableColumn(displayName = "Priority", internalName = Project_priority_columnName, strValues = {"Low", "Medium", "High", "Critical"})
    @Size(min = 4, max = 15)
    private String priority;

    @Lob
    @Size(max = 5000000)
    @Column(columnDefinition = "MEDIUMTEXT")
    @VaadinTableColumn(displayName = "Description", internalName = Project_description_columnName, isWysiwyg = true)
    private String description;

    private LocalDateTime modificationDate;

    @OneToMany(mappedBy = "project", fetch = FetchType.EAGER)
    @NotNull
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @HistoryCollection(historyClass = HistoryProject.class)
    private Set<HistoryProject> history = new HashSet<>();


    public Project() {

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

    public void setHistory(Set<HistoryProject> history) {
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Set<HistoryProject> getHistory() {
        return history;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }
}
