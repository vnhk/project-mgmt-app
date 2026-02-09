package com.bervan.projectmgmtapp.service;

import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.SearchService;
import com.bervan.common.search.model.Operator;
import com.bervan.common.search.model.SearchOperation;
import com.bervan.common.service.BaseService;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.repo.TaskHistoryRepository;
import com.bervan.projectmgmtapp.repo.TaskRepository;
import com.google.common.base.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TaskService extends BaseService<UUID, Task> {
    private final TaskHistoryRepository historyRepository;

    public TaskService(TaskRepository repository, SearchService searchService, TaskHistoryRepository historyRepository) {
        super(repository, searchService);
        this.historyRepository = historyRepository;
    }

    @Override
    public Task save(Task data) {
        if (Strings.isNullOrEmpty(data.getNumber())) {
            data.setNumber(generateTaskNumber(data));
        }
        return super.save(data);
    }

    public List<Task> searchByProject(UUID projectId, String textFilter) {
        SearchRequest request = new SearchRequest();
        request.addCriterion("PROJECT_FILTER", Task.class,
                "project.id", SearchOperation.EQUALS_OPERATION, projectId);
        if (textFilter != null && !textFilter.trim().isEmpty()) {
            String filter = "%" + textFilter.trim() + "%";
            request.addCriterion("TEXT_FILTER", Operator.OR_OPERATOR, Task.class,
                    "number", SearchOperation.LIKE_OPERATION, filter);
            request.addCriterion("TEXT_FILTER", Operator.OR_OPERATOR, Task.class,
                    "name", SearchOperation.LIKE_OPERATION, filter);
        }
        return load(request, Pageable.ofSize(50)).stream()
                .filter(t -> t.isDeleted() == null || !t.isDeleted())
                .collect(Collectors.toList());
    }

    private String generateTaskNumber(Task data) {
        int counter = 0;
        int maxCounter = 1000;
        String number;
        Random random = new Random();

        do {
            String randomDigits = String.format("%04d", random.nextInt(10000));
            if (random.nextBoolean()) {
                randomDigits += random.nextInt(10);
            }

            number = data.getProject().getNumber() + "-" + randomDigits;
            counter++;
        } while (((TaskRepository) repository).existsByNumber(number) && counter < maxCounter);

        if (counter >= maxCounter) {
            throw new RuntimeException("Unable to generate a unique task number after " + maxCounter + " attempts");
        }

        return number;
    }
}
