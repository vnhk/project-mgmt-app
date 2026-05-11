# project-mgmt-app

Project management module with projects, tasks, typed task relations, inline editing, and status/priority tracking.

## Features

- **Projects**: Name, number, status, priority, description; aggregated task statistics
- **Tasks**: Name, number (auto-generated `{projectNumber}-{random}`), status, type, priority, due date, assignee, estimated hours, completion %, tags
- **Task relations**: Typed directional relationships between tasks (blocks, requires, subtask, etc.)
- **Inline editing**: Click-to-edit fields for name, status, priority, dates, numbers
- **Status badges**: Colored pill badges for status and priority

## Task Relation Types

| Type | Parent sees | Child sees |
|------|-------------|------------|
| `CHILD_IS_PART_OF` | "Parent of" | "Is part of" |
| `BLOCKS` | "Blocks" | "Is blocked by" |
| `REQUIRES` | "Requires" | "Is required by" |
| `RELATES_TO` | "Relates to" | "Relates to" |
| `CHILD_SOLVES` | "Solved by" | "Solves" |

## Key Views

| View | Purpose |
|------|---------|
| `AbstractProjectDetailsView` | Project header, stats, task list |
| `AbstractTaskDetailsView` | Task header, metadata, description, relations |
| `TaskRelationsPanel` | Grouped relation display, add/remove relations |
| `ProjectTaskListView` | Task table filtered by project |

## Architecture

- `Task` — `parentRelationships` (this task is parent) + `childRelationships` (this task is child); both cascade PERSIST+MERGE
- `TaskRelation` — no `@GeneratedValue`; always set `relation.setId(UUID.randomUUID())` before persist
- Relations persisted via CASCADE on Task — no dedicated repository
- `InlineEditableField` — click-to-edit with TEXT, COMBOBOX, DATE, NUMBER modes
- CSS: `bervan-project-mgmt.css` in `common` (`.pm-section`, `.task-badge`, `.relation-group`, `.inline-editable-field`)

## Build

```bash
mvn clean install -DskipTests
```

Part of the `my-tools` multi-module Maven project. Requires `common` to be built first.
