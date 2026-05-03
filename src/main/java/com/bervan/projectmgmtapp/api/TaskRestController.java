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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
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

        return super.getById(id, TaskDetailDto.class);
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
        return super.create(req, TaskDetailDto.class);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchUpdate(@PathVariable UUID id, @RequestBody TaskDto req) {
        if (AuthService.getLoggedUserId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<Task> existing = taskService.loadById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        req.setId(id);
        return super.patchUpdate(req);
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
                relation.getType(),
                isParent ? relation.getType().getDisplayName() : relation.getType().getInverseDisplayName(),
                related.getId(), related.getNumber(), related.getName(), related.getStatus(), related.getType(), null, null
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
}
