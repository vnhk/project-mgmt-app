package com.bervan.projectmgmtapp.views;

import com.bervan.common.view.AbstractPageView;
import com.bervan.common.component.BervanButton;
import com.bervan.common.component.WysiwygTextArea;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.model.TaskRelation;
import com.bervan.projectmgmtapp.service.TaskService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractTaskDetailsView extends AbstractPageView implements HasUrlParameter<String> {
    public static final String ROUTE_NAME = "/project-management/task-details/";
    private final TaskService taskService;
    private final BervanLogger logger;
    private ProjectsPageLayout projectsPageLayout = new ProjectsPageLayout(ROUTE_NAME);

    public AbstractTaskDetailsView(TaskService taskService, BervanLogger logger) {
        this.taskService = taskService;
        this.logger = logger;
        add(projectsPageLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, String s) {
        String taskId = event.getRouteParameters().get("___url_parameter").orElse(UUID.randomUUID().toString());
        init(taskId);
    }

    private void init(String taskId) {
        Optional<Task> taskOptional = taskService.loadById(UUID.fromString(taskId));

        if (taskOptional.isEmpty()) {
            showErrorNotification("Task does not exist!");
        } else {  // Header Section
            Task task = taskOptional.get();
            H2 title = new H2(task.getName() + " - " + task.getNumber());
            Span status = new Span(task.getStatus());
            status.getElement().getStyle().set("background", "black")
                    .set("padding", "5px 10px")
                    .set("border-radius", "5px");
            HorizontalLayout header = new HorizontalLayout(title, status);
            header.setWidthFull();
            header.setJustifyContentMode(JustifyContentMode.BETWEEN);

            // Description Section
            BervanButton saveDescriptionButton = new BervanButton("Save", false);
            WysiwygTextArea description = new WysiwygTextArea("editor_task_details_" + task.getId(), task.getDescription(), true);
            description.setSwitchButtonPostAction(() -> {
                saveDescriptionButton.setVisible(!description.isViewMode());
            });

            saveDescriptionButton.addClickListener(e -> {
                Task data = taskOptional.get();
                data.setDescription(description.getValue());
                taskService.save(data);
            });

            // Subtasks Section
            H4 subtasksHeader = new H4("Subtasks");
            Grid<Task> subtasksGrid = new Grid<>(Task.class);
            List<Task> subtasks = task.getChildRelationships().stream().map(TaskRelation::getChild).collect(Collectors.toList());
            subtasksGrid.setItems(subtasks);
            subtasksGrid.setWidthFull();

            // Layout
            add(header, description, saveDescriptionButton, subtasksHeader, subtasksGrid);

            UUID projectId = task.getProject().getId();
            String projectNumber = task.getProject().getNumber();
            projectsPageLayout.updateButtonText(ROUTE_NAME, task.getType() + ": " + task.getNumber());
            projectsPageLayout.updateButtonText(AbstractProjectDetailsView.ROUTE_NAME, "Project: " + projectNumber);
            projectsPageLayout.updateNavigateToForButton(AbstractProjectDetailsView.ROUTE_NAME, AbstractProjectDetailsView.ROUTE_NAME + projectId);
        }
    }
}