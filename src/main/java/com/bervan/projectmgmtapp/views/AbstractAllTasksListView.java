package com.bervan.projectmgmtapp.views;

import com.bervan.common.AbstractTableView;
import com.bervan.common.service.BaseService;
import com.bervan.core.model.BervanLogger;
import com.bervan.projectmgmtapp.model.Task;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AbstractAllTasksListView extends AbstractTableView<UUID, Task> {
    public static final String ROUTE_NAME = "/project-management/all-tasks";

    public AbstractAllTasksListView(BaseService<UUID, Task> service, BervanLogger log) {
        super(new ProjectsPageLayout(ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME,
                AbstractProjectDetailsView.ROUTE_NAME
        ), service, log, Task.class);
        renderCommonComponents();

        addButton.setVisible(false);

        updateFilterMenu(filtersLayout.getCheckboxFiltersMap());
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
    protected List<String> getFieldsToFetchForTable() {
        List<String> columnsToFetchForTable = super.getFieldsToFetchForTable();
        columnsToFetchForTable.add("project");
        return columnsToFetchForTable;
    }

    protected void updateFilterMenu(Map<Field, Map<Object, Checkbox>> checkboxFiltersMap) {
        //default - only open tasks
        checkboxFiltersMap.keySet().stream().filter(e -> e.getName().equals("status"))
                .forEach(e -> {
                    checkboxFiltersMap.get(e)
                            .get("Canceled").setValue(false);
                    checkboxFiltersMap.get(e)
                            .get("Done").setValue(false);
                });

    }
}