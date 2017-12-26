package com.example.springbootkotlinjwtauth.task

import org.springframework.security.access.annotation.Secured
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.util.Assert
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/tasks")
class TaskController(private val taskRepository: TaskRepository) {

  @PreAuthorize("hasAuthority('ROLE_DEVELOPERS')")
  @GetMapping
  fun getTasks() = taskRepository.findAll()

  @PostMapping
  fun addTask(@RequestBody task: Task) {
    taskRepository.save(task)
  }

  @PutMapping("/{id}")
  fun editTask(@PathVariable id: Long, @RequestBody task: Task) {
    taskRepository.findById(id).map {
      it.description = task.description
      taskRepository.save(it)
    }.orElseThrow { RuntimeException("Task not found") }
  }

  @DeleteMapping("/{id}")
  fun deleteTask(@PathVariable id: Long) {
    taskRepository.deleteById(id)
  }
}