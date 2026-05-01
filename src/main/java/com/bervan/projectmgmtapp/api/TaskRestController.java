package com.bervan.projectmgmtapp.api;

import com.bervan.common.config.EntityConfigValidator;
import com.bervan.common.controller.BaseOwnedController;
import com.bervan.common.mapper.BervanDTOMapper;
import com.bervan.common.search.SearchRequest;
import com.bervan.common.search.model.SearchOperation;
import com.bervan.common.service.AuthService;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.model.TaskRelation;
import com.bervan.projectmgmtapp.model.TaskRelationshipType;
import com.bervan.projectmgmtapp.repo.TaskRelationRepository;
import com.bervan.projectmgmtapp.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/project-management/tasks")
public class TaskRestController extends BaseOwnedController {

    private final TaskService taskService;
    private final TaskRelationRepository taskRelationRepository;

    protected TaskRestController(TaskService taskService, BervanDTOMapper mapper,
                                  EntityConfigValidator validator,
                                  TaskRelationRepository taskRelationRepository) {
        super(taskService, mapper, validator, "Task");
        this.taskService = taskService;
        this.taskRelationRepository = taskRelationRepository;
    }

    @GetMapping
    public ResponseEntity<Page<TaskDto>> list(
            @RequestParam(required = false) UUID projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "number") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        SearchRequest searchRequest = new SearchRequest();
        if (projectId != null) {
            searchRequest.addCriterion("PROJECT", Task.class, "project.id", SearchOperation.EQUALS_OPERATION, projectId);
        }
        return super.load(searchRequest, page, size, TaskDto.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDetailDto> getById(@PathVariable UUID id) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Task> taskOpt = taskService.loadById(id);
        if (taskOpt.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toTaskDetailDto(taskOpt.get()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TaskSearchResultDto>> search(
            @RequestParam UUID projectId,
            @RequestParam(defaultValue = "") String q
    ) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Task> tasks = taskService.searchByProject(projectId, q);
        List<TaskSearchResultDto> results = tasks.stream()
                .map(t -> new TaskSearchResultDto(t.getId(), t.getNumber(), t.getName(), t.getStatus(), t.getType()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody TaskCreateRequest req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (req.getProjectId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Task model = (Task) mapper.map(req);
        List<EntityConfigValidator.FieldError> errors = validator.validateCreate("Task", model);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }
        Task saved = taskService.save(model);
        return ResponseEntity.ok(mapper.map(saved, TaskDto.class));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody TaskDto req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Task> existing = taskService.loadById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Task t = existing.get();
        if (req.getName() != null) t.setName(req.getName());
        if (req.getStatus() != null) t.setStatus(req.getStatus());
        if (req.getType() != null) t.setType(req.getType());
        if (req.getPriority() != null) t.setPriority(req.getPriority());
        t.setDescription(req.getDescription());
        t.setDueDate(req.getDueDate());
        t.setAssignee(req.getAssignee());
        t.setEstimatedHours(req.getEstimatedHours());
        if (req.getCompletionPercentage() != null) t.setCompletionPercentage(req.getCompletionPercentage());
        t.setTags(req.getTags());
        t.setModificationDate(LocalDateTime.now());

        Task saved = taskService.save(t);
        return ResponseEntity.ok(mapper.map(saved, TaskDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return super.delete(id);
    }

    @PostMapping("/{id}/relations")
    public ResponseEntity<TaskRelationDto> addRelation(
            @PathVariable UUID id,
            @RequestBody AddRelationRequest req
    ) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Task> parentOpt = taskService.loadById(req.parentTaskId());
        Optional<Task> childOpt = taskService.loadById(req.childTaskId());
        if (parentOpt.isEmpty() || childOpt.isEmpty()) return ResponseEntity.notFound().build();

        Task parent = parentOpt.get();
        Task child = childOpt.get();

        TaskRelation relation = new TaskRelation();
        relation.setId(UUID.randomUUID());
        relation.setParent(parent);
        relation.setChild(child);
        relation.setType(TaskRelationshipType.valueOf(req.type()));

        parent.getParentRelationships().add(relation);
        taskService.save(parent);

        boolean isParent = parent.getId().equals(id);
        Task related = isParent ? child : parent;
        TaskRelationDto dto = new TaskRelationDto(
                relation.getId(),
                isParent ? "PARENT" : "CHILD",
                relation.getType().name(),
                isParent ? relation.getType().getDisplayName() : relation.getType().getInverseDisplayName(),
                related.getId(), related.getNumber(), related.getName(), related.getStatus(), related.getType()
        );
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{taskId}/relations/{relationId}")
    public ResponseEntity<Void> deleteRelation(
            @PathVariable UUID taskId,
            @PathVariable UUID relationId
    ) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<TaskRelation> relOpt = taskRelationRepository.findById(relationId);
        if (relOpt.isEmpty()) return ResponseEntity.notFound().build();

        TaskRelation rel = relOpt.get();
        rel.setDeleted(true);
        taskRelationRepository.save(rel);
        return ResponseEntity.noContent().build();
    }

    private TaskDetailDto toTaskDetailDto(Task task) {
        List<TaskRelationDto> relations = new ArrayList<>();

        for (TaskRelation rel : task.getParentRelationships()) {
            if (Boolean.TRUE.equals(rel.isDeleted())) continue;
            Task child = rel.getChild();
            relations.add(new TaskRelationDto(
                    rel.getId(), "PARENT",
                    rel.getType().name(), rel.getType().getDisplayName(),
                    child.getId(), child.getNumber(), child.getName(), child.getStatus(), child.getType()
            ));
        }

        for (TaskRelation rel : task.getChildRelationships()) {
            if (Boolean.TRUE.equals(rel.isDeleted())) continue;
            Task parent = rel.getParent();
            relations.add(new TaskRelationDto(
                    rel.getId(), "CHILD",
                    rel.getType().name(), rel.getType().getInverseDisplayName(),
                    parent.getId(), parent.getNumber(), parent.getName(), parent.getStatus(), parent.getType()
            ));
        }

        return new TaskDetailDto(
                task.getId(), task.getName(), task.getNumber(),
                task.getStatus(), task.getType(), task.getPriority(),
                task.getDescription(), task.getDueDate(),
                task.getAssignee(), task.getEstimatedHours(),
                task.getCompletionPercentage(), task.getTags(),
                task.getModificationDate(),
                task.getProject() != null ? task.getProject().getId() : null,
                task.getProject() != null ? task.getProject().getNumber() : null,
                task.getProject() != null ? task.getProject().getName() : null,
                relations
        );
    }

    public record TaskDetailDto(
            UUID id, String name, String number,
            String status, String type, String priority,
            String description, LocalDateTime dueDate,
            String assignee, Double estimatedHours,
            Integer completionPercentage, String tags,
            LocalDateTime modificationDate,
            UUID projectId, String projectNumber, String projectName,
            List<TaskRelationDto> relations
    ) {
    }

    public record TaskRelationDto(
            UUID id, String direction, String type, String displayName,
            UUID relatedTaskId, String relatedTaskNumber, String relatedTaskName,
            String relatedTaskStatus, String relatedTaskType
    ) {
    }

    public record TaskSearchResultDto(UUID id, String number, String name, String status, String type) {
    }

    public record AddRelationRequest(UUID parentTaskId, UUID childTaskId, String type) {
    }
}
