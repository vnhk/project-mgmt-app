package com.bervan.projectmgmtapp.views;

import com.bervan.common.component.BervanButton;
import com.bervan.common.component.BervanButtonStyle;
import com.bervan.common.component.InlineEditableField;
import com.bervan.common.component.WysiwygTextArea;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.config.ClassViewAutoConfigColumn;
import com.bervan.common.view.AbstractPageView;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.service.TaskService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;

import java.util.*;
import java.util.stream.Collectors;

@CssImport("./bervan-project-mgmt.css")
public abstract class AbstractTaskDetailsView extends AbstractPageView implements HasUrlParameter<String> {
    public static final String ROUTE_NAME = "/project-management/task-details/";
    private final TaskService taskService;
    private final BervanViewConfig bervanViewConfig;
    private final ProjectsPageLayout projectsPageLayout = new ProjectsPageLayout(ROUTE_NAME);

    public AbstractTaskDetailsView(TaskService taskService, BervanViewConfig bervanViewConfig) {
        this.taskService = taskService;
        this.bervanViewConfig = bervanViewConfig;
        add(projectsPageLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, String s) {
        String taskId = event.getRouteParameters().get("___url_parameter").orElse(UUID.randomUUID().toString());
        init(taskId);
    }

    private void init(String taskId) {
        // Remove all components except the page layout (first child)
        getChildren().filter(c -> c != projectsPageLayout).toList().forEach(this::remove);

        Optional<Task> taskOptional = taskService.loadById(UUID.fromString(taskId));

        if (taskOptional.isEmpty()) {
            showErrorNotification("Task does not exist!");
            return;
        }

        Task task = taskOptional.get();
        UUID projectId = task.getProject().getId();
        String projectNumber = task.getProject().getNumber();
        projectsPageLayout.updateButtonText(ROUTE_NAME, task.getType() + ": " + task.getNumber());
        projectsPageLayout.updateButtonText(AbstractProjectDetailsView.ROUTE_NAME, "Project: " + projectNumber);
        projectsPageLayout.updateNavigateToForButton(AbstractProjectDetailsView.ROUTE_NAME, AbstractProjectDetailsView.ROUTE_NAME + projectId);

        setWidthFull();

        // === Header Card ===
        Div headerCard = new Div();
        headerCard.addClassName("bervan-glass-card");
        headerCard.addClassName("pm-section");
        headerCard.getStyle().set("width", "100%");

        Div headerContent = new Div();
        headerContent.addClassName("task-detail-header");

        // Left: type icon + number + name
        HorizontalLayout headerLeft = new HorizontalLayout();
        headerLeft.addClassName("task-detail-header-left");
        headerLeft.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon typeIcon = TaskTypeIconHelper.createLargeIcon(task.getType());

        Span numberSpan = new Span(task.getNumber());
        numberSpan.addClassName("task-number");

        H2 nameTitle = new H2(task.getName());
        nameTitle.getStyle().set("margin", "0").set("font-size", "1.3rem");

        headerLeft.add(typeIcon, numberSpan, nameTitle);

        // Right: status + priority badges (inline-editable)
        HorizontalLayout headerRight = new HorizontalLayout();
        headerRight.addClassName("task-detail-header-right");
        headerRight.setAlignItems(FlexComponent.Alignment.CENTER);

        InlineEditableField statusField = new InlineEditableField(
                "Status", task.getStatus(), InlineEditableField.FieldType.COMBOBOX,
                List.of("Open", "In Progress", "Done", "Canceled"),
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setStatus((String) newValue);
                    taskService.save(t);
                    showSuccessNotification("Status updated");
                }
        );

        InlineEditableField priorityField = new InlineEditableField(
                "Priority", task.getPriority(), InlineEditableField.FieldType.COMBOBOX,
                List.of("Low", "Medium", "High", "Critical"),
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setPriority((String) newValue);
                    taskService.save(t);
                    showSuccessNotification("Priority updated");
                }
        );

        headerRight.add(statusField, priorityField);

        headerContent.add(headerLeft, headerRight);

        // Breadcrumb
        Div breadcrumb = new Div();
        breadcrumb.addClassName("pm-breadcrumb");
        Anchor projectLink = new Anchor(AbstractProjectDetailsView.ROUTE_NAME + projectId, projectNumber);
        breadcrumb.add(projectLink, new Span(" / "), new Span(task.getNumber()));

        headerCard.add(headerContent, breadcrumb);

        // === Metadata Grid ===
        Div metadataSection = new Div();
        metadataSection.addClassName("bervan-glass");
        metadataSection.addClassName("pm-section");
        metadataSection.getStyle().set("width", "100%");

        Div metadataGrid = new Div();
        metadataGrid.addClassName("task-metadata-grid");

        // Left column
        Div leftCol = new Div();

        InlineEditableField assigneeField = new InlineEditableField(
                "Assignee", task.getAssignee(), InlineEditableField.FieldType.TEXT,
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setAssignee(newValue != null ? newValue.toString() : null);
                    taskService.save(t);
                }
        );

        InlineEditableField dueDateField = new InlineEditableField(
                "Due Date", task.getDueDate(), InlineEditableField.FieldType.DATE,
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setDueDate(newValue != null ? (java.time.LocalDateTime) newValue : null);
                    taskService.save(t);
                }
        );

        InlineEditableField estimatedHoursField = new InlineEditableField(
                "Estimated Hours", task.getEstimatedHours(), InlineEditableField.FieldType.NUMBER,
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setEstimatedHours(newValue != null ? ((Number) newValue).doubleValue() : null);
                    taskService.save(t);
                }
        );

        InlineEditableField typeField = new InlineEditableField(
                "Type", task.getType(), InlineEditableField.FieldType.COMBOBOX,
                List.of("Task", "Bug", "Story", "Feature", "Objective"),
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setType((String) newValue);
                    taskService.save(t);
                }
        );

        leftCol.add(assigneeField, dueDateField, estimatedHoursField, typeField);

        // Right column
        Div rightCol = new Div();

        // Completion percentage with progress bar
        int pct = task.getCompletionPercentage() != null ? task.getCompletionPercentage() : 0;
        Div progressSection = new Div();
        progressSection.addClassName("task-metadata-item");

        Span progressLabel = new Span("Progress");
        progressLabel.addClassName("field-label");
        progressLabel.getStyle()
                .set("font-size", "var(--bervan-font-size-xs, 0.75rem)")
                .set("color", "var(--bervan-text-tertiary)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em")
                .set("font-weight", "600");

        ProgressBar progressBar = new ProgressBar(0, 100, pct);
        progressBar.setWidthFull();
        progressBar.getStyle().set("margin", "4px 0");

        InlineEditableField progressField = new InlineEditableField(
                "Completion %", pct, InlineEditableField.FieldType.NUMBER,
                newValue -> {
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    int val = newValue != null ? ((Number) newValue).intValue() : 0;
                    val = Math.max(0, Math.min(100, val));
                    t.setCompletionPercentage(val);
                    taskService.save(t);
                    progressBar.setValue(val);
                }
        );

        progressSection.add(progressLabel, progressBar, progressField);

        // Tags
        Div tagsSection = new Div();
        tagsSection.addClassName("task-metadata-item");
        buildTagsSection(tagsSection, task);

        rightCol.add(progressSection, tagsSection);

        metadataGrid.add(leftCol, rightCol);
        metadataSection.add(metadataGrid);

        // === Description Section ===
        Div descSection = new Div();
        descSection.addClassName("bervan-glass");
        descSection.addClassName("pm-section");
        descSection.getStyle().set("width", "100%");

        Div descHeader = new Div();
        descHeader.addClassName("pm-section-header");
        H4 descTitle = new H4("Description");
        descTitle.addClassName("pm-section-title");
        descHeader.add(descTitle);

        BervanButton saveDescriptionButton = new BervanButton("Save", false);
        ClassViewAutoConfigColumn config = bervanViewConfig.get("Task").get("description");
        WysiwygTextArea description = new WysiwygTextArea(
                "editor_task_details_" + task.getId(),
                task.getDescription(), true,
                config.isRequired(), config.getMin(), config.getMax()
        );
        description.setSwitchButtonPostAction(() -> {
            saveDescriptionButton.setVisible(!description.isViewMode());
        });

        saveDescriptionButton.addClickListener(e -> {
            description.validate();
            if (description.isInvalid()) {
                return;
            }
            Task data = taskService.loadById(task.getId()).orElse(task);
            data.setDescription(description.getValue());
            taskService.save(data);
            showSuccessNotification("Description updated!");
        });

        descSection.add(descHeader, description, saveDescriptionButton);

        // === Relations Section ===
        Div relationsSection = new Div();
        relationsSection.addClassName("bervan-glass");
        relationsSection.addClassName("pm-section");
        relationsSection.getStyle().set("width", "100%");

        Div relationsHeader = new Div();
        relationsHeader.addClassName("pm-section-header");
        H4 relationsTitle = new H4("Relations");
        relationsTitle.addClassName("pm-section-title");
        relationsHeader.add(relationsTitle);

        TaskRelationsPanel relationsPanel = new TaskRelationsPanel(task, taskService);

        relationsSection.add(relationsHeader, relationsPanel);

        // === Layout ===
        add(headerCard, metadataSection, descSection, relationsSection);
    }

    private void buildTagsSection(Div container, Task task) {
        container.removeAll();

        Span tagsLabel = new Span("Tags");
        tagsLabel.getStyle()
                .set("font-size", "var(--bervan-font-size-xs, 0.75rem)")
                .set("color", "var(--bervan-text-tertiary)")
                .set("text-transform", "uppercase")
                .set("letter-spacing", "0.05em")
                .set("font-weight", "600")
                .set("margin-bottom", "4px")
                .set("display", "block");
        container.add(tagsLabel);

        Div chipsContainer = new Div();
        chipsContainer.addClassName("tag-chips-container");

        String tags = task.getTags();
        if (tags != null && !tags.isBlank()) {
            List<String> tagList = Arrays.stream(tags.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            for (String tag : tagList) {
                Span chip = new Span();
                chip.addClassName("tag-chip");

                Span tagText = new Span(tag);
                Span removeBtn = new Span("×");
                removeBtn.addClassName("remove-tag");
                removeBtn.addClickListener(e -> {
                    List<String> updated = Arrays.stream(task.getTags().split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty() && !s.equals(tag))
                            .collect(Collectors.toList());
                    Task t = taskService.loadById(task.getId()).orElse(task);
                    t.setTags(String.join(", ", updated));
                    taskService.save(t);
                    task.setTags(t.getTags());
                    buildTagsSection(container, task);
                });

                chip.getElement().appendChild(tagText.getElement());
                chip.getElement().appendChild(removeBtn.getElement());
                chipsContainer.add(chip);
            }
        }

        // Add tag input
        TextField addTagField = new TextField();
        addTagField.setPlaceholder("Add tag...");
        addTagField.setWidth("100px");
        addTagField.getStyle().set("font-size", "var(--bervan-font-size-xs, 0.75rem)");

        addTagField.addKeyPressListener(com.vaadin.flow.component.Key.ENTER, e -> {
            String newTag = addTagField.getValue().trim();
            if (!newTag.isEmpty()) {
                Task t = taskService.loadById(task.getId()).orElse(task);
                String currentTags = t.getTags();
                String updatedTags = (currentTags != null && !currentTags.isBlank())
                        ? currentTags + ", " + newTag
                        : newTag;
                t.setTags(updatedTags);
                taskService.save(t);
                task.setTags(updatedTags);
                addTagField.clear();
                buildTagsSection(container, task);
            }
        });

        chipsContainer.add(addTagField);
        container.add(chipsContainer);
    }
}
