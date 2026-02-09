package com.bervan.projectmgmtapp.views;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Map;

public class TaskTypeIconHelper {

    private static final Map<String, VaadinIcon> ICON_MAP = Map.of(
            "Task", VaadinIcon.CHECK_SQUARE_O,
            "Bug", VaadinIcon.BUG,
            "Story", VaadinIcon.BOOK,
            "Feature", VaadinIcon.LIGHTBULB,
            "Objective", VaadinIcon.BULLSEYE
    );

    private static final Map<String, String> COLOR_MAP = Map.of(
            "Task", "var(--bervan-info, #3b82f6)",
            "Bug", "var(--bervan-danger, #ef4444)",
            "Story", "var(--bervan-success, #10b981)",
            "Feature", "var(--bervan-accent, #22d3ee)",
            "Objective", "var(--bervan-warning, #f59e0b)"
    );

    public static Icon createIcon(String type) {
        VaadinIcon vaadinIcon = ICON_MAP.getOrDefault(type, VaadinIcon.QUESTION);
        Icon icon = vaadinIcon.create();
        icon.getStyle().set("color", COLOR_MAP.getOrDefault(type, "var(--bervan-text-secondary)"));
        icon.setSize("18px");
        icon.addClassName("task-type-icon");
        return icon;
    }

    public static Icon createLargeIcon(String type) {
        Icon icon = createIcon(type);
        icon.setSize("28px");
        return icon;
    }
}
