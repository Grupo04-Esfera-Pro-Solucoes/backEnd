package com.esfera.g2.esferag2.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esfera.g2.esferag2.model.Task;
import com.esfera.g2.esferag2.repository.TaskRepository;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/all/{userId}")
    public List<Task> getAllTasks(@PathVariable Long userId) {
        return taskRepository.findByUserIdUser(userId);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id, @PathVariable Long idUser) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("todo-list");
        }
        return taskRepository.save(task);
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task taskDetails) {
        return taskRepository.findById(id)
                .map(task -> {
                    task.setName(taskDetails.getName());
                    task.setDescription(taskDetails.getDescription());
                    task.setDueDate(taskDetails.getDueDate());
                    task.setStatus(taskDetails.getStatus());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("Task not found"));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isPresent()) {
            Task task = optionalTask.get();
            task.setStatus(status);
            taskRepository.save(task);
            return ResponseEntity.ok(task);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            try {
                taskRepository.deleteById(id);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Erro ao deletar task");
            }
            return ResponseEntity.ok().build();
        }
    }
}
