package com.ecoreno.service;

import com.ecoreno.exception.ResourceNotFoundException;
import com.ecoreno.model.Project;
import com.ecoreno.model.ProjectStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProjectServiceTests {

    private ProjectService service;

    @BeforeEach
    void setUp() {
        service = new ProjectService();
    }

    @Test
    void givenProjects_whenFindAll_thenReturnPreloadedProjects() {
        assertThat(service.findAll()).hasSize(2);
    }

    @Test
    void givenProject_whenFindById_thenReturnProject() {
        Project project = service.findById(1L);

        assertThat(project.getId()).isEqualTo(1L);
        assertThat(project.getName()).isEqualTo("Rénovation cuisine");
    }

    @Test
    void givenProject_whenFindByIdAndNotFound_thenThrowResourceNotFoundException() {
        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Projet introuvable avec l'id 99");
    }

    @Test
    void givenProject_whenSave_thenAssignIdAndAddProject() {
        Project newProject = new Project(null, "Isolation double vitrage", "Sophie", 6000, "Travaux complémentaires", "Très bon", ProjectStatus.PLANNED);
        Project saved = service.save(newProject);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Isolation double vitrage");
        assertThat(service.findAll()).hasSize(3);
    }

    @Test
    void givenProject_whenUpdate_thenModifyExistingProject() {
        Project updated = new Project(1L, "Rénovation cuisine v2", "Claire", 9000, "Nouvelle description", "Moyenne émission", ProjectStatus.COMPLETED);
        Project result = service.update(1L, updated);

        assertThat(result.getName()).isEqualTo("Rénovation cuisine v2");
        assertThat(result.getBudget()).isEqualTo(9000);
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.COMPLETED);
    }

    @Test
    void givenProject_whenUpdateAndNotFound_thenThrowResourceNotFoundException() {
        Project updated = new Project(1L, "Nom", "Proprietaire", 1000, "Desc", "Impact", ProjectStatus.PLANNED);
        assertThatThrownBy(() -> service.update(99L, updated))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
