package com.bervan.projectmgmtapp.views;

import com.bervan.common.component.BervanButton;
import com.bervan.common.component.BervanButtonStyle;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.model.TaskRelation;
import com.bervan.projectmgmtapp.model.TaskRelationshipType;
import com.bervan.projectmgmtapp.service.TaskService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.util.*;
import java.util.stream.Collectors;

public class TaskRelationsPanel extends VerticalLayout {

    private final Task currentTask;
    private final TaskService taskService;
    private final VerticalLayout relationsContent;

    public TaskRelationsPanel(Task currentTask, TaskService taskService) {
        this.currentTask = currentTask;
        this.taskService = taskService;

        setPadding(false);
        setSpacing(false);

        relationsContent = new VerticalLayout();
        relationsContent.setPadding(false);
        relationsContent.setSpacing(false);

        refreshRelations();
        add(relationsContent);
        add(buildAddRelationRow());
    }

    private void refreshRelations() {
        relationsContent.removeAll();

        // Reload task from DB to get fresh relationships
        Task freshTask = taskService.loadById(currentTask.getId()).orElse(currentTask);

        // Collect all relations grouped by display label
        Map<String, List<RelationEntry>> grouped = new LinkedHashMap<>();

        // parentRelationships: current task is parent
        Set<TaskRelation> parentRels = freshTask.getParentRelationships();
        if (parentRels != null) {
            for (TaskRelation rel : parentRels) {
                if (rel.isDeleted() || rel.getChild().isDeleted()) continue;
                String label = rel.getType().getDisplayName();
                grouped.computeIfAbsent(label, k -> new ArrayList<>())
                        .add(new RelationEntry(rel, rel.getChild(), false));
            }
        }

        // childRelationships: current task is child
        Set<TaskRelation> childRels = freshTask.getChildRelationships();
        if (childRels != null) {
            for (TaskRelation rel : childRels) {
                if (rel.isDeleted() || rel.getParent().isDeleted()) continue;
                String label = rel.getType().getInverseDisplayName();
                grouped.computeIfAbsent(label, k -> new ArrayList<>())
                        .add(new RelationEntry(rel, rel.getParent(), true));
            }
        }

        if (grouped.isEmpty()) {
            Span empty = new Span("No relations yet");
            empty.addClassName("relation-empty");
            relationsContent.add(empty);
            return;
        }

        for (Map.Entry<String, List<RelationEntry>> entry : grouped.entrySet()) {
            relationsContent.add(buildRelationGroup(entry.getKey(), entry.getValue()));
        }
    }

    private Div buildRelationGroup(String label, List<RelationEntry> entries) {
        Div group = new Div();
        group.addClassName("relation-group");

        // Header
        Div header = new Div();
        header.addClassName("relation-group-header");

        Icon toggleIcon = VaadinIcon.CHEVRON_DOWN.create();
        toggleIcon.addClassName("relation-group-toggle");
        toggleIcon.setSize("16px");

        Span labelSpan = new Span(label);
        labelSpan.addClassName("relation-group-label");

        Span countBadge = new Span(String.valueOf(entries.size()));
        countBadge.addClassName("relation-group-count");

        header.add(toggleIcon, labelSpan, countBadge);

        // Rows container
        Div rows = new Div();
        for (RelationEntry re : entries) {
            rows.add(buildRelationRow(re));
        }

        header.addClickListener(e -> {
            boolean hidden = !rows.isVisible();
            rows.setVisible(hidden);
            if (hidden) {
                toggleIcon.getClassNames().remove("collapsed");
            } else {
                toggleIcon.addClassName("collapsed");
            }
        });

        group.add(header, rows);
        return group;
    }

    private HorizontalLayout buildRelationRow(RelationEntry entry) {
        Task relatedTask = entry.relatedTask;
        HorizontalLayout row = new HorizontalLayout();
        row.addClassName("relation-row");
        row.setWidthFull();
        row.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon typeIcon = TaskTypeIconHelper.createIcon(relatedTask.getType());

        Span number = new Span(relatedTask.getNumber());
        number.addClassName("relation-task-number");

        Span name = new Span(relatedTask.getName());
        name.addClassName("relation-task-name");

        Span statusBadge = StatusBadgeHelper.createStatusBadge(relatedTask.getStatus());

        // Remove button
        Span removeBtn = new Span("×");
        removeBtn.addClassName("relation-remove-btn");
        removeBtn.getElement().setAttribute("title", "Remove relation");
        removeBtn.addClickListener(e -> {
            removeRelation(entry.relation, entry.isInverse);
        });

        row.add(typeIcon, number, name, statusBadge, removeBtn);
        row.setFlexGrow(1, name);

        row.addClickListener(e -> {
            if (!e.getSource().equals(removeBtn)) {
                UI.getCurrent().navigate(AbstractTaskDetailsView.ROUTE_NAME + relatedTask.getId());
            }
        });

        return row;
    }

    private void removeRelation(TaskRelation relation, boolean isInverse) {
        relation.setDeleted(true);
        if (isInverse) {
            // Relation is on the other task's parentRelationships - save that task
            Task otherTask = relation.getParent();
            taskService.save(otherTask);
        } else {
            taskService.save(currentTask);
        }
        refreshRelations();
    }

    private HorizontalLayout buildAddRelationRow() {
        HorizontalLayout addRow = new HorizontalLayout();
        addRow.addClassName("relation-add-row");
        addRow.setAlignItems(FlexComponent.Alignment.CENTER);
        addRow.setWidthFull();

        // Relation type selector
        ComboBox<RelationOption> typeCombo = new ComboBox<>();
        typeCombo.setPlaceholder("Relation type...");
        typeCombo.setWidth("180px");
        typeCombo.setItems(buildRelationOptions());
        typeCombo.setItemLabelGenerator(RelationOption::getLabel);

        // Task search combo
        ComboBox<Task> taskCombo = new ComboBox<>();
        taskCombo.setPlaceholder("Search task...");
        taskCombo.setWidthFull();
        taskCombo.setItemLabelGenerator(t -> t.getNumber() + " - " + t.getName());
        taskCombo.setAllowCustomValue(true);

        UUID projectId = currentTask.getProject().getId();
        UUID currentId = currentTask.getId();

        // Load items with lazy data provider
        taskCombo.setItems(query -> {
            String filter = query.getFilter().orElse("");
            List<Task> results = taskService.searchByProject(projectId, filter).stream()
                    .filter(t -> !t.getId().equals(currentId))
                    .collect(Collectors.toList());
            int end = Math.min(query.getOffset() + query.getLimit(), results.size());
            int start = Math.min(query.getOffset(), end);
            return results.subList(start, end).stream();
        });

        // Link button
        BervanButton linkBtn = new BervanButton(new Icon(VaadinIcon.LINK),
                event -> {
                    RelationOption selectedType = typeCombo.getValue();
                    Task selectedTask = taskCombo.getValue();
                    if (selectedType == null || selectedTask == null) return;
                    createRelation(selectedType, selectedTask);
                    taskCombo.clear();
                }, BervanButtonStyle.PRIMARY);
        linkBtn.getElement().setAttribute("title", "Link existing task");

        // New task button
        BervanButton newBtn = new BervanButton(new Icon(VaadinIcon.PLUS),
                event -> {
                    RelationOption selectedType = typeCombo.getValue();
                    if (selectedType == null) return;
                    openNewTaskDialog(selectedType);
                }, BervanButtonStyle.PRIMARY);
        newBtn.getElement().setAttribute("title", "Create new task and link");

        addRow.add(typeCombo, taskCombo, linkBtn, newBtn);
        addRow.setFlexGrow(1, taskCombo);
        return addRow;
    }

    private void createRelation(RelationOption option, Task targetTask) {
        TaskRelation relation = new TaskRelation();
        relation.setId(UUID.randomUUID());
        relation.setType(option.getRelationType());

        if (option.isInverse()) {
            // Current task is child, target is parent
            relation.setParent(targetTask);
            relation.setChild(currentTask);
            targetTask.getParentRelationships().add(relation);
            taskService.save(targetTask);
        } else {
            // Current task is parent, target is child
            relation.setParent(currentTask);
            relation.setChild(targetTask);
            currentTask.getParentRelationships().add(relation);
            taskService.save(currentTask);
        }

        refreshRelations();
    }

    private void openNewTaskDialog(RelationOption selectedType) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Create new task & link as: " + selectedType.getLabel());
        dialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);

        TextField nameField = new TextField("Name");
        nameField.setWidthFull();
        nameField.setMinLength(4);

        ComboBox<String> taskTypeCombo = new ComboBox<>("Type");
        taskTypeCombo.setItems("Task", "Bug", "Story", "Feature", "Objective");
        taskTypeCombo.setValue("Task");
        taskTypeCombo.setWidthFull();

        ComboBox<String> priorityCombo = new ComboBox<>("Priority");
        priorityCombo.setItems("Low", "Medium", "High", "Critical");
        priorityCombo.setValue("Medium");
        priorityCombo.setWidthFull();

        content.add(nameField, taskTypeCombo, priorityCombo);
        dialog.add(content);

        BervanButton saveBtn = new BervanButton("Create & Link", event -> {
            String name = nameField.getValue();
            if (name == null || name.trim().length() < 4) return;

            Task newTask = new Task();
            newTask.setName(name.trim());
            newTask.setStatus("Open");
            newTask.setType(taskTypeCombo.getValue());
            newTask.setPriority(priorityCombo.getValue());
            newTask.setProject(currentTask.getProject());
            newTask.setCompletionPercentage(0);
            taskService.save(newTask);

            createRelation(selectedType, newTask);
            dialog.close();
        });

        BervanButton cancelBtn = new BervanButton("Cancel", event -> dialog.close());

        dialog.getFooter().add(cancelBtn, saveBtn);
        dialog.open();
        nameField.focus();
    }

    private List<RelationOption> buildRelationOptions() {
        List<RelationOption> options = new ArrayList<>();
        for (TaskRelationshipType type : TaskRelationshipType.values()) {
            // Direct: current task is parent
            options.add(new RelationOption(type, type.getDisplayName(), false));
            // Inverse: current task is child (skip if symmetric like RELATES_TO)
            if (!type.getDisplayName().equals(type.getInverseDisplayName())) {
                options.add(new RelationOption(type, type.getInverseDisplayName(), true));
            }
        }
        return options;
    }

    private static class RelationEntry {
        final TaskRelation relation;
        final Task relatedTask;
        final boolean isInverse;

        RelationEntry(TaskRelation relation, Task relatedTask, boolean isInverse) {
            this.relation = relation;
            this.relatedTask = relatedTask;
            this.isInverse = isInverse;
        }
    }

    private static class RelationOption {
        private final TaskRelationshipType relationType;
        private final String label;
        private final boolean inverse;

        RelationOption(TaskRelationshipType relationType, String label, boolean inverse) {
            this.relationType = relationType;
            this.label = label;
            this.inverse = inverse;
        }

        public TaskRelationshipType getRelationType() {
            return relationType;
        }

        public String getLabel() {
            return label;
        }

        public boolean isInverse() {
            return inverse;
        }
    }
}
