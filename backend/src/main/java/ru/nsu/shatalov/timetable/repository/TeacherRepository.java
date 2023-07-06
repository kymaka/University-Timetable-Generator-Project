package ru.nsu.shatalov.timetable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nsu.shatalov.timetable.model.object.constraint.Teacher;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {}
