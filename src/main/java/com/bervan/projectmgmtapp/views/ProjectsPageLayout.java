package com.bervan.projectmgmtapp.views;

import com.bervan.common.MenuNavigationComponent;

public class ProjectsPageLayout extends MenuNavigationComponent {
    public ProjectsPageLayout(String route, String... notVisibleButtons) {
        super(route, notVisibleButtons);

        addButtonIfVisible(menuButtonsRow, AbstractProjectListView.ROUTE_NAME, "Projects");
        addButtonIfVisible(menuButtonsRow, AbstractAllTasksListView.ROUTE_NAME, "All Tasks");
        addButtonIfVisible(menuButtonsRow, AbstractProjectDetailsView.ROUTE_NAME, "Project");
        addButtonIfVisible(menuButtonsRow, AbstractTaskDetailsView.ROUTE_NAME, "Task");

        add(menuButtonsRow);
    }
}
