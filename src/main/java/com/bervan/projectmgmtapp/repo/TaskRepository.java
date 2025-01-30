package com.bervan.projectmgmtapp.repo;

import com.bervan.history.model.BaseRepository;
import com.bervan.projectmgmtapp.model.Task;

import java.util.UUID;

public interface TaskRepository extends BaseRepository<Task, UUID> {

}
