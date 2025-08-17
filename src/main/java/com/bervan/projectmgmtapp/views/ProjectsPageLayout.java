package com.bervan.projectmgmtapp.views;

import com.bervan.common.MenuNavigationComponent;
import com.vaadin.flow.component.icon.VaadinIcon;

public class ProjectsPageLayout extends MenuNavigationComponent {
    public ProjectsPageLayout(String route, String... notVisibleButtons) {
        super(route, notVisibleButtons);

        addButtonIfVisible(menuButtonsRow, AbstractProjectListView.ROUTE_NAME, "Projects", VaadinIcon.HOME.create());
        addButtonIfVisible(menuButtonsRow, AbstractAllTasksListView.ROUTE_NAME, "All Tasks", VaadinIcon.HOME.create());
        addButtonIfVisible(menuButtonsRow, AbstractProjectDetailsView.ROUTE_NAME, "Project", VaadinIcon.HOME.create());
        addButtonIfVisible(menuButtonsRow, AbstractTaskDetailsView.ROUTE_NAME, "Task", VaadinIcon.HOME.create());

        add(menuButtonsRow);
    }
}
