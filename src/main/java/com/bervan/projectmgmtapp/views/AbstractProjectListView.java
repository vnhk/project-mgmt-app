package com.bervan.projectmgmtapp.views;

import com.bervan.common.AbstractTableView;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Project;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.UUID;

public abstract class AbstractProjectListView extends AbstractTableView<UUID, Project> {
    public static final String ROUTE_NAME = "/project-management/projects";

    public AbstractProjectListView(BaseService<UUID, Project> service, BervanLogger log) {
        super(new ProjectsPageLayout(ROUTE_NAME, AbstractProjectDetailsView.ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME), service, log, Project.class);
        renderCommonComponents();
    }

    @Override
    protected Grid<Project> getGrid() {
        Grid<Project> grid = new Grid<>(Project.class, false);
        buildGridAutomatically(grid);

        return grid;
    }

    @Override
    protected void preColumnAutoCreation(Grid<Project> grid) {
        grid.addComponentColumn(entity -> {
                    Icon linkIcon = new Icon(VaadinIcon.LINK);
                    linkIcon.getStyle().set("cursor", "pointer");
                    return new Anchor(ROUTE_NAME + "/" + entity.getId(), new HorizontalLayout(linkIcon));
                }).setKey("link")
                .setResizable(true);
    }
}