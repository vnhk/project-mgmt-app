package com.bervan.projectmgmtapp.views;

import com.bervan.common.component.BervanButton;
import com.bervan.common.component.WysiwygTextArea;
import com.bervan.common.config.BervanViewConfig;
import com.bervan.common.config.ClassViewAutoConfigColumn;
import com.bervan.common.view.AbstractPageView;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.service.ProjectService;
import com.bervan.projectmgmtapp.service.TaskService;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@CssImport("./bervan-project-mgmt.css")
public abstract class AbstractProjectDetailsView extends AbstractPageView implements HasUrlParameter<String> {
    public static final String ROUTE_NAME = AbstractProjectListView.ROUTE_NAME + "/";
    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectsPageLayout projectsPageLayout;
    private final BervanViewConfig bervanViewConfig;

    public AbstractProjectDetailsView(ProjectService projectService, TaskService taskService, BervanViewConfig bervanViewConfig) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.bervanViewConfig = bervanViewConfig;

        projectsPageLayout = new ProjectsPageLayout(ROUTE_NAME, AbstractTaskDetailsView.ROUTE_NAME);
        add(projectsPageLayout);
    }

    @Override
    public void setParameter(BeforeEvent event, String s) {
        String projectId = event.getRouteParameters().get("___url_parameter").orElse(UUID.randomUUID().toString());
        init(projectId);
    }

    private void init(String projectId) {
        getChildren().filter(c -> c != projectsPageLayout).toList().forEach(this::remove);

        Optional<Project> projectOpt = projectService.loadById(UUID.fromString(projectId));

        if (projectOpt.isEmpty()) {
            showErrorNotification("Project does not exist!");
            return;
        }

        Project project = projectOpt.get();
        projectsPageLayout.updateButtonText(ROUTE_NAME, "Project: " + project.getNumber());

        setWidthFull();

        // === Header Card ===
        Div headerCard = new Div();
        headerCard.addClassName("bervan-glass-card");
        headerCard.addClassName("pm-section");
        headerCard.getStyle().set("width", "100%");

        H2 title = new H2(project.getName());
        title.getStyle().set("margin", "0");

        Span numberSpan = new Span(project.getNumber());
        numberSpan.getStyle()
                .set("font-family", "var(--bervan-font-mono, monospace)")
                .set("color", "var(--bervan-text-secondary)")
                .set("font-size", "1.1rem")
                .set("font-weight", "600");

        HorizontalLayout titleRow = new HorizontalLayout(title, numberSpan);
        titleRow.setAlignItems(Alignment.BASELINE);
        titleRow.getStyle().set("gap", "var(--bervan-spacing-sm, 8px)");

        // Inline-editable status and priority
        InlineEditableField statusField = new InlineEditableField(
                "Status", project.getStatus(), InlineEditableField.FieldType.COMBOBOX,
                List.of("Open", "In Progress", "Done", "Canceled"),
                newValue -> {
                    Project p = projectService.loadById(project.getId()).orElse(project);
                    p.setStatus((String) newValue);
                    projectService.save(p);
                    showSuccessNotification("Status updated");
                }
        );

        InlineEditableField priorityField = new InlineEditableField(
                "Priority", project.getPriority(), InlineEditableField.FieldType.COMBOBOX,
                List.of("Low", "Medium", "High", "Critical"),
                newValue -> {
                    Project p = projectService.loadById(project.getId()).orElse(project);
                    p.setPriority((String) newValue);
                    projectService.save(p);
                    showSuccessNotification("Priority updated");
                }
        );

        HorizontalLayout badgesRow = new HorizontalLayout(statusField, priorityField);
        badgesRow.setAlignItems(Alignment.CENTER);

        headerCard.add(titleRow, badgesRow);

        // === Stats Row ===
        Set<Task> tasks = project.getTasks();
        long totalTasks = tasks != null ? tasks.stream().filter(t -> !t.isDeleted()).count() : 0;
        long openTasks = tasks != null ? tasks.stream().filter(t -> !t.isDeleted() && "Open".equals(t.getStatus())).count() : 0;
        long inProgressTasks = tasks != null ? tasks.stream().filter(t -> !t.isDeleted() && "In Progress".equals(t.getStatus())).count() : 0;
        long doneTasks = tasks != null ? tasks.stream().filter(t -> !t.isDeleted() && "Done".equals(t.getStatus())).count() : 0;
        long overdueTasks = tasks != null ? tasks.stream().filter(t ->
                !t.isDeleted() && !"Done".equals(t.getStatus()) && !"Canceled".equals(t.getStatus())
                        && t.getDueDate() != null && t.getDueDate().isBefore(LocalDateTime.now())
        ).count() : 0;

        HorizontalLayout statsRow = new HorizontalLayout();
        statsRow.addClassName("project-stats-row");
        statsRow.setWidthFull();

        statsRow.add(
                createStatCard(String.valueOf(totalTasks), "Total"),
                createStatCard(String.valueOf(openTasks), "Open"),
                createStatCard(String.valueOf(inProgressTasks), "In Progress"),
                createStatCard(String.valueOf(doneTasks), "Done"),
                createStatCard(String.valueOf(overdueTasks), "Overdue")
        );

        // === Description Section (compact, collapsible) ===
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
        ClassViewAutoConfigColumn config = bervanViewConfig.get("Project").get("description");
        WysiwygTextArea description = new WysiwygTextArea(
                "editor_project_details_" + project.getId(),
                project.getDescription(), true,
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
            Project latestProject = projectService.loadById(project.getId()).get();
            latestProject.setDescription(description.getValue());
            projectService.save(latestProject);
            showSuccessNotification("Description updated!");
        });

        descSection.add(descHeader, description, saveDescriptionButton);

        // === Tasks Section ===
        Div tasksSection = new Div();
        tasksSection.addClassName("pm-section");
        tasksSection.getStyle().set("width", "100%");

        Div tasksSectionHeader = new Div();
        tasksSectionHeader.addClassName("pm-section-header");
        H4 tasksTitle = new H4("Tasks");
        tasksTitle.addClassName("pm-section-title");
        tasksSectionHeader.add(tasksTitle);

        ProjectTaskListView taskListView = new ProjectTaskListView(
                taskService, projectsPageLayout, project, bervanViewConfig
        );

        tasksSection.add(tasksSectionHeader, taskListView);

        // === Layout ===
        add(headerCard, statsRow, descSection, tasksSection);
    }

    private Div createStatCard(String value, String label) {
        Div card = new Div();
        card.addClassName("project-stat-card");
        card.addClassName("bervan-glass-card");

        Div valueDiv = new Div();
        valueDiv.addClassName("stat-value");
        valueDiv.setText(value);

        Div labelDiv = new Div();
        labelDiv.addClassName("stat-label");
        labelDiv.setText(label);

        card.add(valueDiv, labelDiv);
        return card;
    }
}
