package com.bervan.projectmgmtapp.views;

import com.bervan.common.AbstractTableView;
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
        grid.addComponentColumn(entity -> {
                    Icon linkIcon = new Icon(VaadinIcon.LINK);
                    linkIcon.getStyle().set("cursor", "pointer");
                    return new Anchor(AbstractTaskDetailsView.ROUTE_NAME + entity.getId(), new HorizontalLayout(linkIcon));
                }).setKey("link")
                .setResizable(true);
        buildGridAutomatically(grid);

        return grid;
    }

    @Override
    protected Task customizeSavingInCreateForm(Task newItem) {
        newItem.setProject(project);
        return super.customizeSavingInCreateForm(newItem);
    }
}