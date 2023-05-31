package ru.nsu.shatalov.timetable.generator;

import java.util.Arrays;
import java.util.List;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import ru.nsu.shatalov.timetable.model.object.constraint.Room;
import ru.nsu.shatalov.timetable.model.object.constraint.Subject;
import ru.nsu.shatalov.timetable.model.object.constraint.Teacher;
import ru.nsu.shatalov.timetable.model.enums.Day;

public class TimetableGenerator {

  public void generate(List<Subject> subjects, List<Room> rooms, List<Teacher> teachers) {
    int numberOfCourses = subjects.size();
    int numberOfRooms = rooms.size();
    int numberOfTimeSlots = 3;
    int numberOfGroups = 3;

    Day[] days = {Day.Monday, Day.Tuesday, Day.Wednesday, Day.Thursday, Day.Friday};

    int[] courseCapacities = {50, 20, 60, 40, 50};
    int[] courseRoomTypes = {3, 1, 2, 3, 3};
    int[] roomNumbers = new int[numberOfRooms];
    int[] roomCapacities = new int[numberOfRooms];
    int[] roomTypes = new int[numberOfRooms];
    int[] teacherArray = new int[teachers.size()];
    for (int i = 0; i < numberOfRooms; i++) {
      roomNumbers[i] = i;
      roomCapacities[i] = rooms.get(i).getType().getCapacity();
      roomTypes[i] = rooms.get(i).getType().toInt();
    }
    for (int i = 0; i < teachers.size(); i++) {
      teacherArray[i] = i;
    }

    // Model
    Model model = new Model("University Timetable");

    // Variables
    // g - group, i - subject, [g][i][0] - room, [g][i][1] - timeslot, [g][i][2] - Teacher,
    // [g][i][3] - day of week
    IntVar[][][] timetable = new IntVar[numberOfGroups][numberOfCourses][4];

    for (int g = 0; g < numberOfGroups; g++) {
      for (int i = 0; i < numberOfCourses; i++) {
        timetable[g][i][0] = model.intVar("Course_" + i + "_Room", roomNumbers);
        timetable[g][i][1] = model.intVar("Course_" + i + "_TimeSlot", 0, numberOfTimeSlots - 1);
        timetable[g][i][2] =
            model.intVar(
                "Course_" + subjects.get(i).getName() + "_Teacher", 0, teachers.size() - 1);
        timetable[g][i][3] = model.intVar("Course_" + i + "_Day", 0, days.length - 1);
      }
    }

    // Constraints
    for (int g = 0; g < numberOfGroups; g++) {
      for (int g2 = 0; g2 < numberOfGroups; g2++) {
        for (int i = 0; i < numberOfCourses; i++) {
          for (int j = 0; j < numberOfCourses; j++) {
            if (i != j || g != g2) {
              model
                  .or(
                      model.arithm(timetable[g][i][3], "!=", timetable[g2][j][3]),
                      model.arithm(timetable[g][i][1], "!=", timetable[g2][j][1]),
                      model.arithm(timetable[g][i][2], "!=", timetable[g2][j][2]))
                  .post();
            }
          }
        }
        // Additional constraint to ensure the same subject is not scheduled for the same group at
        // the same time
        for (int i = 0; i < numberOfCourses; i++) {
          for (int j = i + 1; j < numberOfCourses; j++) {
            model
                .or(
                    model.arithm(timetable[g][i][3], "!=", timetable[g][j][3]),
                    model.arithm(timetable[g][i][1], "!=", timetable[g][j][1]))
                .post();
          }
        }
      }
    }

    for (int g = 0; g < numberOfGroups; g++) {
      for (int i = 0; i < numberOfCourses; i++) {
        model.arithm(timetable[g][i][0], "<", numberOfRooms).post();
        model.arithm(timetable[g][i][1], "<", numberOfTimeSlots).post();
        model.arithm(timetable[g][i][2], "<", teachers.size()).post();
        model.arithm(timetable[g][i][3], "<", days.length).post();

        // Checking for correct room type.
        IntVar roomTypeVar = model.intVar(roomTypes);
        model.element(roomTypeVar, roomTypes, timetable[g][i][0]).post();
        model.arithm(roomTypeVar, "=", courseRoomTypes[i]).post();

        // Checking for right room capacity.
        IntVar roomCapacityVar = model.intVar(roomCapacities);
        model.element(roomCapacityVar, roomCapacities, timetable[g][i][0]).post();
        model.arithm(roomCapacityVar, ">=", courseCapacities[i]).post();

        // Finding qualified teacher.
        IntVar teacherVar = model.intVar(teacherArray);
        model.element(teacherVar, teacherArray, timetable[g][i][2]).post();

        for (int t = 0; t < teachers.size(); t++) {
          Teacher teacher = teachers.get(t);

          // Convert the working days of the teacher to their corresponding indices in the days
          // array
          int[] workingDays =
              teacher.getWorkingDays().stream()
                  .mapToInt(day -> Arrays.asList(days).indexOf(day))
                  .toArray();

          for (int s = 0; s < teacher.getSubjects().size(); s++) {
            if (teacher.getSubjects().get(s).equals(subjects.get(i))) {
              model.arithm(teacherVar, "=", t).post();
              model.member(timetable[g][i][3], workingDays).post();
            }
          }
        }
      }
    }

    // Solve and display
    /*    if (model.getSolver().solve()) {
      for (int g = 0; g < numberOfGroups; g++) {
        for (int i = 0; i < numberOfCourses; i++) {
          System.out.println(
              "Group: "
                  + g
                  + ", Subject "
                  + subjects.get(i).getName()
                  + " -> Room: "
                  + rooms.get(timetable[g][i][0].getValue()).getNumber()
                  + ", Time Slot: "
                  + timetable[g][i][1].getValue()
                  + ",  Teacher: "
                  + teachers.get(timetable[g][i][2].getValue()).getName()
                  + ", Day: "
                  + days[timetable[g][i][3].getValue()].name());
        }
      }
    } else {
      System.out.println("No solution found.");
    }*/
    Solver solver = model.getSolver();
    if (solver.solve()) {
      for (int d = 0; d < days.length; d++) {
        for (int g = 0; g < numberOfGroups; g++) {
          for (int i = 0; i < numberOfCourses; i++) {
            if (timetable[g][i][3].getValue() == d) {
              System.out.println(
                  "Day: "
                      + days[timetable[g][i][3].getValue()].name()
                      + ", Subject: "
                      + subjects.get(i).getName()
                      + " -> Room: "
                      + rooms.get(timetable[g][i][0].getValue()).getNumber()
                      + ", Time Slot: "
                      + timetable[g][i][1].getValue()
                      + ",  Teacher: "
                      + teachers.get(timetable[g][i][2].getValue()).getName()
                      + ", Group: "
                      + g);
            }
          }
        }
      }
    } else {
      System.out.println("No solution found.");
    }
  }
}
