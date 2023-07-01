package ru.nsu.shatalov.timetable.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.shatalov.timetable.dto.StudentGroupDTO;
import ru.nsu.shatalov.timetable.service.impl.GroupServiceImpl;

@RestController
@RequestMapping("/groups")
public class GroupController {

  private final GroupServiceImpl service;

  public GroupController(GroupServiceImpl service) {
    this.service = service;
  }

  @GetMapping("/all")
  public ResponseEntity<List<StudentGroupDTO>> getAllGroups() {
    return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentGroupDTO> getGroup(@PathVariable Long id) {
    StudentGroupDTO studentGroup = service.getById(id);
    if (studentGroup != null) {
      return new ResponseEntity<>(studentGroup, HttpStatus.OK);
    }
    return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
  }

  @PostMapping
  public ResponseEntity<StudentGroupDTO> createGroup(@RequestBody StudentGroupDTO studentGroup) {
    if (studentGroup != null) {
      StudentGroupDTO newStudentGroup = service.save(studentGroup);
      return new ResponseEntity<>(newStudentGroup, HttpStatus.CREATED);
    }
    return new ResponseEntity<>(null, HttpStatus.NOT_ACCEPTABLE);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentGroupDTO> updateGroup(
      @RequestBody StudentGroupDTO newStudentGroup, @PathVariable("id") Long id) {
    StudentGroupDTO updatedStudentGroup = service.update(newStudentGroup, id);
    if (newStudentGroup != null) {
      return new ResponseEntity<>(updatedStudentGroup, HttpStatus.CREATED);
    }
    return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  }
}