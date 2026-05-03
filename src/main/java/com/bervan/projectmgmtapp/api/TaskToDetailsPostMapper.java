package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.PostMapper;
import com.bervan.projectmgmtapp.model.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskToDetailsPostMapper implements PostMapper<Task, TaskDetailDto> {
    @Override
    public void map(Task task, TaskDetailDto taskDetailDto) {
        List<TaskRelationDto> relations = taskDetailDto.getRelations();
        if (relations == null) {
            relations = new ArrayList<>();
        }

        for (TaskRelationDto parentRelationship : taskDetailDto.getParentRelationships()) {
            parentRelationship.setDirection("CHILD");
            TaskDto child = parentRelationship.getChild();
            parentRelationship.setRelatedTaskId(child.getId());
            parentRelationship.setRelatedTaskNumber(child.getNumber());
            parentRelationship.setRelatedTaskName(child.getName());
            parentRelationship.setRelatedTaskStatus(child.getStatus());
            parentRelationship.setRelatedTaskType(child.getType());

            relations.add(parentRelationship);
        }

        for (TaskRelationDto childRelationship : taskDetailDto.getChildRelationships()) {
            childRelationship.setDirection("PARENT");
            TaskDto parent = childRelationship.getParent();
            childRelationship.setRelatedTaskId(parent.getId());
            childRelationship.setRelatedTaskNumber(parent.getNumber());
            childRelationship.setRelatedTaskName(parent.getName());
            childRelationship.setRelatedTaskStatus(parent.getStatus());
            childRelationship.setRelatedTaskType(parent.getType());
            relations.add(childRelationship);
        }
    }

    @Override
    public Class<Task> getFromType() {
        return Task.class;
    }

    @Override
    public Class<TaskDetailDto> getToType() {
        return TaskDetailDto.class;
    }
}
