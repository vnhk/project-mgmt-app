package com.bervan.projectmgmtapp.views;

import com.bervan.common.MenuNavigationComponent;

public class ProjectsPageLayout extends MenuNavigationComponent {
    public ProjectsPageLayout(String route) {
        super(route);

        addButtonIfVisible(menuButtonsRow, AbstractProjectListView.ROUTE_NAME, "Projects");
        addButtonIfVisible(menuButtonsRow, AbstractAllTasksListView.ROUTE_NAME, "All Tasks");

        add(menuButtonsRow);
    }
}
