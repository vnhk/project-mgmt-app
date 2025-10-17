package com.bervan.projectmgmtapp.views;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.BaseService;
import com.bervan.common.view.AbstractBervanTableView;
import com.bervan.common.view.AbstractFiltersLayout;
import com.bervan.common.view.DefaultFilterValuesContainer;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Task;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbstractAllTasksListView extends AbstractBervanTableView<UUID, Task> {
    public static final String ROUTE_NAME = "/project-management/all-tasks";

    public AbstractAllTasksListView(BaseService<UUID, Task> service, BervanLogger log, BervanViewConfig bervanViewConfig) {
        super(new ProjectsPageLayout(ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME,
                AbstractProjectDetailsView.ROUTE_NAME
        ), service, log, bervanViewConfig, Task.class);
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
                .setWidth("6px")
                .setResizable(false);
    }

    @Override
    protected List<String> getFieldsToFetchForTable() {
        List<String> columnsToFetchForTable = super.getFieldsToFetchForTable();
        columnsToFetchForTable.add("project");
        return columnsToFetchForTable;
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
}