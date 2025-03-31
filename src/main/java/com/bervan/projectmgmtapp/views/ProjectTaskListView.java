package com.bervan.projectmgmtapp.views;

import com.bervan.common.AbstractTableView;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.model.SearchOperation;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.model.Task;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.UUID;

public class ProjectTaskListView extends AbstractTableView<UUID, Task> {
    private final Project project;

    public ProjectTaskListView(BaseService<UUID, Task> service, BervanLogger log, ProjectsPageLayout pageLayout, Project project) {
        super(pageLayout, service, log, Task.class);
        this.project = project;
        renderCommonComponents();
    }

    @Override
    protected Grid<Task> getGrid() {
        Grid<Task> grid = new Grid<>(Task.class, false);
        buildGridAutomatically(grid);

        return grid;
    }

    @Override
    protected void preColumnAutoCreation(Grid<Task> grid) {
        grid.addComponentColumn(entity -> {
                    Icon linkIcon = new Icon(VaadinIcon.LINK);
                    linkIcon.getStyle().set("cursor", "pointer");
                    return new Anchor(AbstractTaskDetailsView.ROUTE_NAME + entity.getId(), new HorizontalLayout(linkIcon));
                }).setKey("link")
                .setWidth("10px")
                .setResizable(false);
    }

    @Override
    protected void buildFiltersMenu() {
        super.buildFiltersMenu();

        //default - only open tasks
        filtersMap.keySet().stream().filter(e -> e.getName().equals("status"))
                .forEach(e -> {
                    filtersMap.get(e)
                            .get("Canceled").setValue(false);
                    filtersMap.get(e)
                            .get("Done").setValue(false);
                });

    }

    @Override
    protected void customizePreLoad(SearchRequest request) {
        request.addCriterion("PROJECT_TASK_CRITERIA", Task.class,
                "project.id", SearchOperation.EQUALS_OPERATION, project.getId());
        super.customizePreLoad(request);
    }

    @Override
    protected Task customizeSavingInCreateForm(Task newItem) {
        newItem.setProject(project);
        return super.customizeSavingInCreateForm(newItem);
    }
}