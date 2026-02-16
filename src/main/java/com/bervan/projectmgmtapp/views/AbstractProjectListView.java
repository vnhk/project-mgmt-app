package com.bervan.projectmgmtapp.views;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.BaseService;
import com.bervan.common.view.AbstractBervanTableView;
import com.bervan.projectmgmtapp.model.Project;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.util.UUID;

@CssImport("./bervan-project-mgmt.css")
public abstract class AbstractProjectListView extends AbstractBervanTableView<UUID, Project> {
    public static final String ROUTE_NAME = "/project-management/projects";

    public AbstractProjectListView(BaseService<UUID, Project> service, BervanViewConfig bervanViewConfig) {
        super(new ProjectsPageLayout(ROUTE_NAME, AbstractProjectDetailsView.ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME), service, bervanViewConfig, Project.class);
        renderCommonComponents();
    }

    @Override
    protected Grid<Project> getGrid() {
        Grid<Project> grid = new Grid<>(Project.class, false);
        buildGridAutomatically(grid);

        if (grid.getColumnByKey("name") != null) {
            grid.getColumnByKey("name").setRenderer(new ComponentRenderer<>(
                    project -> new Anchor(ROUTE_NAME + "/" + project.getId(), project.getName())
            ));
        }

        if (grid.getColumnByKey("status") != null) {
            grid.getColumnByKey("status").setRenderer(new ComponentRenderer<>(
                    project -> StatusBadgeHelper.createStatusBadge(project.getStatus())
            ));
        }
        if (grid.getColumnByKey("priority") != null) {
            grid.getColumnByKey("priority").setRenderer(new ComponentRenderer<>(
                    project -> StatusBadgeHelper.createPriorityBadge(project.getPriority())
            ));
        }

        return grid;
    }
}
