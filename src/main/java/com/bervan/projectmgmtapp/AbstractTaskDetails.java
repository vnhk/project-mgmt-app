package com.bervan.projectmgmtapp;

import com.bervan.common.AbstractPageView;
import com.bervan.common.BervanButton;
import com.bervan.common.WysiwygTextArea;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.model.TaskRelation;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractTaskDetails extends AbstractPageView {
    public static final String ROUTE_NAME = "/project-management/task-details";

    public AbstractTaskDetails() {
        // Mock data
        Task task = new Task();

        task.setName("Implement login feature");
        task.setNumber("ST-123");
        task.setStatus("In Progress");
        task.setPriority("Low");
        task.setDescription("User should be able to log in using their credentials.");

        // Header Section
        H2 title = new H2(task.getName());
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
            showWarningNotification("Saving is not implement yet!");
        });

        // Subtasks Section
        H4 subtasksHeader = new H4("Subtasks");
        Grid<Task> subtasksGrid = new Grid<>(Task.class);
        List<Task> subtasks = task.getChildRelationships().stream().map(TaskRelation::getChild).collect(Collectors.toList());
        subtasksGrid.setItems(subtasks);
        subtasksGrid.setWidthFull();

        // Layout
        add(header, description, saveDescriptionButton, subtasksHeader, subtasksGrid);
    }
}