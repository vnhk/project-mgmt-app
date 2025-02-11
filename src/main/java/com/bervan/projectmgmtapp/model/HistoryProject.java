package com.bervan.projectmgmtapp.model;

import com.bervan.common.model.BervanHistoryEntity;
import com.bervan.common.model.PersistableTableData;
import com.bervan.history.model.HistoryField;
import com.bervan.history.model.HistoryOwnerEntity;
import com.bervan.history.model.HistorySupported;
import com.bervan.ieentities.ExcelIEEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@HistorySupported
public class HistoryProject extends BervanHistoryEntity<UUID> implements PersistableTableData<UUID>, ExcelIEEntity<UUID> {
    @Id
    private UUID id;
    private LocalDateTime updateDate;

    @HistoryField
    private String name;

    @HistoryField
    private String number;

    @HistoryField
    private String status;

    @HistoryField
    private String priority;

    @Lob
    @Size(max = 5000000)
    @Column(columnDefinition = "MEDIUMTEXT")
    @HistoryField
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @HistoryOwnerEntity
    private Project historyOwner;

    public String getName() {
        return name;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTableFilterableColumnValue() {
        return number;
    }

    @Override
    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    @Override
    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID uuid) {
        this.id = uuid;
    }

    public Project getHistoryOwner() {
        return historyOwner;
    }

    public void setHistoryOwner(Project historyOwner) {
        this.historyOwner = historyOwner;
    }
}