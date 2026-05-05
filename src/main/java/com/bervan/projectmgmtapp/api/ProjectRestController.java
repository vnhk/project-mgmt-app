package com.bervan.projectmgmtapp.api;

import com.bervan.common.config.EntityConfigValidator;
import com.bervan.common.controller.BaseOwnedController;
import com.bervan.common.mapper.BervanDTOMapper;
import com.bervan.projectmgmtapp.model.Project;
import com.bervan.projectmgmtapp.model.Task;
import com.bervan.projectmgmtapp.service.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/project-management/projects")
public class ProjectRestController extends BaseOwnedController {

    protected ProjectRestController(ProjectService service, BervanDTOMapper mapper, EntityConfigValidator validator) {
        super(service, mapper, validator, "Project");
    }

    @GetMapping
    public ResponseEntity<Page<ProjectDto>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        return super.load(page, size, ProjectDto.class);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDto> getById(@PathVariable UUID id) {
        return super.getById(id, ProjectDto.class);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProjectDto req) {
        return super.create(req);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody ProjectDto req) {
        Optional<Project> existing = service.loadById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();

        Project p = existing.get();
        if (req.getName() != null) p.setName(req.getName());
        if (req.getStatus() != null) p.setStatus(req.getStatus());
        if (req.getPriority() != null) p.setPriority(req.getPriority());
        p.setDescription(req.getDescription());
        p.setModificationDate(LocalDateTime.now());

        Project saved = (Project) service.save(p);
        return ResponseEntity.ok(mapper.map(saved, ProjectDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        return super.delete(id);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ProjectStatsDto> getStats(@PathVariable UUID id) {
        Optional<Project> projectOpt = service.loadById(id);
        if (projectOpt.isEmpty()) return ResponseEntity.notFound().build();

        Project project = projectOpt.get();
        LocalDateTime now = LocalDateTime.now();
        int total = 0, open = 0, inProgress = 0, done = 0, overdue = 0;

        for (Task t : project.getTasks()) {
            if (Boolean.TRUE.equals(t.isDeleted())) continue;
            total++;
            String status = t.getStatus();
            if ("Open".equals(status)) open++;
            else if ("In Progress".equals(status)) inProgress++;
            else if ("Done".equals(status)) done++;
            if (t.getDueDate() != null && t.getDueDate().isBefore(now)
                    && !"Done".equals(status) && !"Canceled".equals(status)) {
                overdue++;
            }
        }

        return ResponseEntity.ok(new ProjectStatsDto(total, open, inProgress, done, overdue));
    }

    public record ProjectStatsDto(int total, int open, int inProgress, int done, int overdue) {
    }
}
