package com.bervan.projectmgmtapp.views;

import com.bervan.common.AbstractPageView;
import com.bervan.common.BervanButton;
import com.bervan.common.WysiwygTextArea;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.service.ProjectService;
import com.bervan.projectmgmtapp.service.TaskService;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractProjectDetailsView extends AbstractPageView implements HasUrlParameter<String> {
    public static final String ROUTE_NAME = AbstractProjectListView.ROUTE_NAME + "/";
    private final ProjectService projectService;
    private final TaskService taskService;
    private final BervanLogger logger;
    private final ProjectsPageLayout projectsPageLayout;

    public AbstractProjectDetailsView(ProjectService projectService, TaskService taskService, BervanLogger logger) {
        this.projectService = projectService;
        this.logger = logger;
        this.taskService = taskService;

        projectsPageLayout = new ProjectsPageLayout(ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME);
        add(projectsPageLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, String s) {
        String projectId = event.getRouteParameters().get("___url_parameter").orElse(UUID.randomUUID().toString());
        init(projectId);
    }

    private void init(String projectId) {
        Optional<Project> project = projectService.loadById(UUID.fromString(projectId));

        if (project.isEmpty()) {
            showErrorNotification("Project does not exist!");
        } else {
            projectsPageLayout.updateButtonText(ROUTE_NAME, "Project: " + project.get().getNumber());

            // Header Section
            H2 title = new H2(project.get().getName() + " - " + project.get().getNumber());
            Span status = new Span(project.get().getStatus());
            status.getElement().getStyle().set("background", "black")
                    .set("padding", "5px 10px")
                    .set("border-radius", "5px");
            HorizontalLayout header = new HorizontalLayout(title, status);
            header.setWidthFull();
            header.setJustifyContentMode(JustifyContentMode.BETWEEN);

            // Description Section
            BervanButton saveDescriptionButton = new BervanButton("Save", false);
            WysiwygTextArea description = new WysiwygTextArea("editor_project_details_" + project.get().getId(), project.get().getDescription(), true);
            description.setSwitchButtonPostAction(() -> {
                saveDescriptionButton.setVisible(!description.isViewMode());
            });

            saveDescriptionButton.addClickListener(e -> {
                Project latestProject = projectService.loadById(project.get().getId()).get();
                latestProject.setDescription(description.getValue());
                projectService.save(latestProject);

                showSuccessNotification("Description updated!");
            });

            // Tasks
            H4 subtasksHeader = new H4("Tasks:");
            ProjectTaskListView taskDetailsView
                    = new ProjectTaskListView(taskService, logger, projectsPageLayout, project.get());

            // Layout
            add(header, description, saveDescriptionButton, subtasksHeader, taskDetailsView);
        }
    }
}