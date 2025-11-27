package com.bervan.projectmgmtapp.service;

import com.bervan.common.search.SearchService;
import com.bervan.common.service.BaseService;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.repo.ProjectHistoryRepository;
import com.bervan.projectmgmtapp.repo.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProjectService extends BaseService<UUID, Project> {
    private final ProjectHistoryRepository historyRepository;
    private final TaskService taskService;

    public ProjectService(ProjectRepository repository, SearchService searchService, ProjectHistoryRepository historyRepository, TaskService taskService) {
        super(repository, searchService);
        this.historyRepository = historyRepository;
        this.taskService = taskService;
    }

    @Override
    public void delete(Project item) {
        for (Task task : item.getTasks()) {
            taskService.delete(task);
        }
        super.delete(item);
    }
}
