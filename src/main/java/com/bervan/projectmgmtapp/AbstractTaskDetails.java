package com.bervan.projectmgmtapp;

import com.bervan.common.AbstractPageView;
import com.bervan.common.BervanButton;
import com.bervan.common.WysiwygTextArea;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaskDetails extends AbstractPageView {
    public static final String ROUTE_NAME = "/project-management/task-details";

    public AbstractTaskDetails() {
        // Mock data
        Story story = new Story("ST-123", "Implement login feature", "In Progress", "User should be able to log in using their credentials.");
        story.addSubtask("Create UI for login");
        story.addSubtask("Implement authentication logic");
        story.addComment("John Doe", "We need to confirm the OAuth provider.");

        // Header Section
        H2 title = new H2(story.getTitle());
        Span status = new Span(story.getStatus());
        status.getElement().getStyle().set("background", "black")
                .set("padding", "5px 10px")
                .set("border-radius", "5px");
        HorizontalLayout header = new HorizontalLayout(title, status);
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);

        // Description Section
        BervanButton saveDescriptionButton = new BervanButton("Save", false);
        WysiwygTextArea description = new WysiwygTextArea("editor_task_details_" + story.id, story.getDescription(), true);
        description.setSwitchButtonPostAction(() -> {
            saveDescriptionButton.setVisible(!description.isViewMode());
        });

        saveDescriptionButton.addClickListener(e -> {
            showWarningNotification("Saving is not implement yet!");
        });

        // Subtasks Section
        H4 subtasksHeader = new H4("Subtasks");
        Grid<String> subtasksGrid = new Grid<>(String.class);
        subtasksGrid.setItems(story.getSubtasks());
        subtasksGrid.setWidthFull();

        // Layout
        add(header, description, saveDescriptionButton, subtasksHeader, subtasksGrid);
    }

    private static class Story {
        private String id;
        private String title;
        private String status;
        private String description;
        private List<String> subtasks = new ArrayList<>();
        private List<Comment> comments = new ArrayList<>();

        public Story(String id, String title, String status, String description) {
            this.id = id;
            this.title = title;
            this.status = status;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getSubtasks() {
            return subtasks;
        }

        public List<Comment> getComments() {
            return comments;
        }

        public void addSubtask(String subtask) {
            subtasks.add(subtask);
        }

        public void addComment(String author, String text) {
            comments.add(new Comment(author, text));
        }
    }

    private static class Comment {
        private String author;
        private String text;

        public Comment(String author, String text) {
            this.author = author;
            this.text = text;
        }

        public String getAuthor() {
            return author;
        }

        public String getText() {
            return text;
        }
    }
}