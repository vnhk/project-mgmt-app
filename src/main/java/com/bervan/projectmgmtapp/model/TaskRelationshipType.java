package com.bervan.projectmgmtapp.model;

public enum TaskRelationshipType {
    CHILD_SOLVES("Child Solves"), CHILD_IS_PART_OF("Child is part of");

    private final String displayName;

    TaskRelationshipType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getInternalName() {
        return name();
    }
}
