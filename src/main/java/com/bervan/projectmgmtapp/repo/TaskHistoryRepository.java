package com.bervan.projectmgmtapp.repo;

import com.bervan.history.model.BaseRepository;
import com.bervan.projectmgmtapp.model.HistoryTask;

import java.util.UUID;

public interface TaskHistoryRepository extends BaseRepository<HistoryTask, UUID> {
}
