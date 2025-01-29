package com.bervan.projectmgmtapp;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTaskDetails extends VerticalLayout {
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
        Paragraph description = new Paragraph(story.getDescription());
        description.getElement().getStyle().set("padding", "10px").set("border", "1px solid #ddd").set("border-radius", "5px");

        // Subtasks Section
        H4 subtasksHeader = new H4("Subtasks");
        Grid<String> subtasksGrid = new Grid<>(String.class);
        subtasksGrid.setItems(story.getSubtasks());
        subtasksGrid.setWidthFull();

        // Comments Section
        H4 commentsHeader = new H4("Comments");
        VerticalLayout commentsSection = new VerticalLayout();
        for (Comment comment : story.getComments()) {
            Div commentBox = new Div();
            commentBox.getElement().getStyle().set("padding", "10px").set("border", "1px solid #ddd").set("border-radius", "5px").set("margin-bottom", "5px");
            commentBox.add(new Span(comment.getAuthor() + ": " + comment.getText()));
            commentsSection.add(commentBox);
        }

        // Add Comment Section
        TextField commentField = new TextField();
        commentField.setPlaceholder("Add a comment");
        Button addCommentButton = new Button("Submit", event -> {
            if (!commentField.getValue().trim().isEmpty()) {
                story.addComment("You", commentField.getValue());
                Div commentBox = new Div(new Span("You: " + commentField.getValue()));
                commentBox.getElement().getStyle().set("padding", "10px").set("border", "1px solid #ddd").set("border-radius", "5px").set("margin-bottom", "5px");
                commentsSection.add(commentBox);
                commentField.clear();
            }
        });
        HorizontalLayout commentInputLayout = new HorizontalLayout(commentField, addCommentButton);

        // Layout
        add(header, description, subtasksHeader, subtasksGrid, commentsHeader, commentsSection, commentInputLayout);
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