package com.bervan.projectmgmtapp.views;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.model.SearchOperation;
import com.bervan.common.service.BaseService;
import com.bervan.common.view.AbstractBervanTableView;
import com.bervan.common.view.AbstractFiltersLayout;
import com.bervan.common.view.DefaultFilterValuesContainer;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.model.Task;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProjectTaskListView extends AbstractBervanTableView<UUID, Task> {
    private final Project project;

    public ProjectTaskListView(BaseService<UUID, Task> service, BervanLogger log, ProjectsPageLayout pageLayout, Project project, BervanViewConfig bervanViewConfig) {
        super(pageLayout, service, log, bervanViewConfig, Task.class);
        this.project = project;
        renderCommonComponents();
        tableToolbarActions.getEditItemDialog().setCustomizeSavingInEditFormFunction((Task task) -> {
            task.setProject(project);
            return task;
        });
    }

    @Override
    protected Grid<Task> getGrid() {
        Grid<Task> grid = new Grid<>(Task.class, false);
        buildGridAutomatically(grid);

        return grid;
    }

    @Override
    protected AbstractFiltersLayout<UUID, Task> buildFiltersLayout(Class<Task> taskClass) {
        try {
            Map<Field, Map<Object, Boolean>> checkboxDefaultValues = new HashMap<>();
            checkboxDefaultValues.put(Task.class.getDeclaredField("status"), Map.of("Canceled", false, "Done", false));
            return new AbstractFiltersLayout<>(tClass, applyFiltersButton, DefaultFilterValuesContainer
                    .builder()
                    .checkboxFiltersMapDefaultValues(checkboxDefaultValues)
                    .build(), bervanViewConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void preColumnAutoCreation(Grid<Task> grid) {
        grid.addComponentColumn(entity -> {
                    Icon linkIcon = new Icon(VaadinIcon.LINK);
                    linkIcon.getStyle().set("cursor", "pointer");
                    return new Anchor(AbstractTaskDetailsView.ROUTE_NAME + entity.getId(), new HorizontalLayout(linkIcon));
                }).setKey("link")
                .setWidth("6px")
                .setResizable(false);
    }

    @Override
    protected void customizePreLoad(SearchRequest request) {
        request.addCriterion("PROJECT_TASK_CRITERIA", Task.class,
                "project.id", SearchOperation.EQUALS_OPERATION, project.getId());
        super.customizePreLoad(request);
    }

    @Override
    protected Task preSaveActions(Task newItem) {
        newItem.setProject(project);
        return super.preSaveActions(newItem);
    }
}