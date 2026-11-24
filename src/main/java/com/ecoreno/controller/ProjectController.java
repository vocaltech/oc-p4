package com.ecoreno.controller;

import com.ecoreno.model.Project;
import com.ecoreno.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "project-controller", description = "List, create and update renovation projects")
@SecurityRequirement(name = "basicAuth")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/auth/login")
    @Operation(summary = "Login")
    @SecurityRequirement // This endpoint is publicly available
    public String login() {
        return "This route is publicly available";
    }

    /**
     * List all projects
     * @return A list of all projects
     */
    @GetMapping("/demandes")
    @Operation(summary = "List all projects")
    @ApiResponse(
            responseCode = "200",
            description = "Liste des demandes",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))
    )
    @ApiResponse(
            responseCode = "401",
            description = "Unauthorized request",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
    )
    public List<Project> getProjects() {
        return projectService.findAll();
    }

    /**
     * Get a project details
     * @param id - The project ID
     * @return - The project details
     */
    @GetMapping("/demandes/{id}")
    public Project getProject(
            @PathVariable
            @Parameter(description = "The demand ID")
            Long id) {
        return projectService.findById(id);
    }

    @PostMapping("/demandes")
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@Valid @RequestBody Project project) {
        return projectService.save(project);
    }

    @PutMapping("/demandes/{id}")
    public Project updateProject(@PathVariable Long id, @Valid @RequestBody Project project) {
        return projectService.update(id, project);
    }
}
