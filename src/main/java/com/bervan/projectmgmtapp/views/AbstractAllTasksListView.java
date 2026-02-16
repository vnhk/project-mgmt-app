package com.bervan.projectmgmtapp.views;

import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.service.BaseService;
import com.bervan.common.view.AbstractBervanTableView;
import com.bervan.common.view.AbstractFiltersLayout;
import com.bervan.common.view.DefaultFilterValuesContainer;
import com.bervan.projectmgmtapp.model.Task;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@CssImport("./bervan-project-mgmt.css")
public class AbstractAllTasksListView extends AbstractBervanTableView<UUID, Task> {
    public static final String ROUTE_NAME = "/project-management/all-tasks";

    public AbstractAllTasksListView(BaseService<UUID, Task> service, BervanViewConfig bervanViewConfig) {
        super(new ProjectsPageLayout(ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME,
                AbstractProjectDetailsView.ROUTE_NAME
        ), service, bervanViewConfig, Task.class);
        renderCommonComponents();
        newItemButton.setVisible(false);
    }

    @Override
    protected Grid<Task> getGrid() {
        Grid<Task> grid = new Grid<>(Task.class, false);
        buildGridAutomatically(grid);

        if (grid.getColumnByKey("name") != null) {
            grid.getColumnByKey("name").setRenderer(new ComponentRenderer<>(
                    entity -> new Anchor(AbstractTaskDetailsView.ROUTE_NAME + entity.getId(), entity.getName())
            ));
        }

        customizeTaskColumns(grid);

        return grid;
    }

    @Override
    protected void preColumnAutoCreation(Grid<Task> grid) {
        grid.addComponentColumn(entity -> TaskTypeIconHelper.createIcon(entity.getType()))
                .setKey("typeIcon")
                .setHeader("")
                .setFlexGrow(0)
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

    static void customizeTaskColumns(Grid<Task> grid) {
        if (grid.getColumnByKey("status") != null) {
            grid.getColumnByKey("status").setRenderer(new ComponentRenderer<>(
                    task -> StatusBadgeHelper.createStatusBadge(task.getStatus())
            ));
        }
        if (grid.getColumnByKey("priority") != null) {
            grid.getColumnByKey("priority").setRenderer(new ComponentRenderer<>(
                    task -> StatusBadgeHelper.createPriorityBadge(task.getPriority())
            ));
        }
        if (grid.getColumnByKey("completionPercentage") != null) {
            grid.getColumnByKey("completionPercentage").setRenderer(new ComponentRenderer<>(task -> {
                HorizontalLayout layout = new HorizontalLayout();
                layout.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.CENTER);
                layout.setSpacing(false);
                layout.getStyle().set("gap", "6px");
                int pct = task.getCompletionPercentage() != null ? task.getCompletionPercentage() : 0;
                layout.add(StatusBadgeHelper.createMiniProgress(pct));
                Span label = new Span(pct + "%");
                label.getStyle().set("font-size", "var(--bervan-font-size-xs, 0.75rem)")
                        .set("color", "var(--bervan-text-secondary)");
                layout.add(label);
                return layout;
            })).setHeader("Progress").setWidth("110px");
        }
        // Hide type text column since we have icon column
        if (grid.getColumnByKey("type") != null) {
            grid.getColumnByKey("type").setVisible(false);
        }
        // Hide estimatedHours from table view
        if (grid.getColumnByKey("estimatedHours") != null) {
            grid.getColumnByKey("estimatedHours").setVisible(false);
        }
    }
}
