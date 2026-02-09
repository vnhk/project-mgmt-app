package com.bervan.projectmgmtapp.views;

import com.vaadin.flow.component.html.Span;

public class StatusBadgeHelper {

    public static Span createStatusBadge(String status) {
        Span badge = new Span(status);
        badge.addClassName("task-badge");
        if (status != null) {
            String cssClass = "status-" + status.toLowerCase().replace(" ", "-");
            badge.addClassName(cssClass);
        }
        return badge;
    }

    public static Span createPriorityBadge(String priority) {
        Span badge = new Span(priority);
        badge.addClassName("task-badge");
        if (priority != null) {
            String cssClass = "priority-" + priority.toLowerCase();
            badge.addClassName(cssClass);
        }
        return badge;
    }

    public static Span createMiniProgress(Integer percentage) {
        int pct = percentage != null ? percentage : 0;
        Span container = new Span();
        container.addClassName("mini-progress");
        Span fill = new Span();
        fill.addClassName("mini-progress-fill");
        fill.getStyle().set("width", pct + "%");
        container.getElement().appendChild(fill.getElement());
        return container;
    }
}
