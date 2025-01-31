package com.bervan.projectmgmtapp.views;

import com.bervan.common.MenuNavigationComponent;

public class ProjectsPageLayout extends MenuNavigationComponent {
    public ProjectsPageLayout(boolean detailsOpened, String projectNumber, String projectId) {
        super(AbstractProjectListView.ROUTE_NAME);

        addButtonIfVisible(menuButtonsRow, AbstractProjectListView.ROUTE_NAME, "List");
        if (detailsOpened) {
            addButtonIfVisible(menuButtonsRow, AbstractProjectListView.ROUTE_NAME + "/" + projectId, projectNumber);
        }

        add(menuButtonsRow);
    }
}
