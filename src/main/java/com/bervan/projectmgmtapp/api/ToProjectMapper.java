package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.DefaultCustomMapper;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ToProjectMapper implements DefaultCustomMapper<UUID, Project> {

    private final ProjectService projectService;

    public ToProjectMapper(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Override
    public Project map(UUID projectId) {
        return projectService.loadById(projectId).orElse(null);
    }

    @Override
    public Class<UUID> getFrom() {
        return UUID.class;
    }

    @Override
    public Class<Project> getTo() {
        return Project.class;
    }
}
