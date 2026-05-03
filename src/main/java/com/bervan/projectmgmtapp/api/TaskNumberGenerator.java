package com.bervan.projectmgmtapp.api;

import com.bervan.core.model.PostMapper;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TaskNumberGenerator implements PostMapper<TaskCreateRequest, Task> {

    private final TaskService taskService;

    public TaskNumberGenerator(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public void map(TaskCreateRequest taskCreateRequest, Task task) {
        Set<Task> tasks = task.getProject().getTasks();
        Set<String> numbers = tasks.stream().map(Task::getNumber).collect(Collectors.toSet());
        int maxNumber = numbers.stream().map(e -> e.replace(task.getProject().getNumber() + "-", ""))
                .mapToInt(Integer::parseInt).max().orElse(0);
        task.setNumber(task.getProject().getNumber() + String.format("-%04d", maxNumber + 1));
    }

    @Override
    public Class<TaskCreateRequest> getFromType() {
        return TaskCreateRequest.class;
    }

    @Override
    public Class<Task> getToType() {
        return Task.class;
    }
}
