package com.bervan.projectmgmtapp.views;

import com.bervan.common.AbstractTableView;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Task;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.UUID;

public class AbstractAllTasksListView extends AbstractTableView<UUID, Task> {
    public static final String ROUTE_NAME = "/project-management/all-tasks";

    public AbstractAllTasksListView(BaseService<UUID, Task> service, BervanLogger log) {
        super(new ProjectsPageLayout(ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME,
                AbstractProjectDetailsView.ROUTE_NAME
        ), service, log, Task.class);
        renderCommonComponents();

        addButton.setVisible(false);
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
                .setResizable(true);
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
}