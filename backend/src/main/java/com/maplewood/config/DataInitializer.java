package com.maplewood.config;

import com.maplewood.domain.model.*;
import com.maplewood.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Seeds one CourseSection per Fall course for the active semester on startup.
 * Runs only if no sections exist for the active semester yet.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final SemesterRepository semesterRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final CourseSectionRepository sectionRepository;

    public DataInitializer(SemesterRepository semesterRepository,
                           CourseRepository courseRepository,
                           TeacherRepository teacherRepository,
                           ClassroomRepository classroomRepository,
                           CourseSectionRepository sectionRepository) {
        this.semesterRepository = semesterRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.classroomRepository = classroomRepository;
        this.sectionRepository = sectionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Optional<Semester> activeSemester = semesterRepository.findByIsActive(1);
        if (activeSemester.isEmpty()) {
            log.warn("No active semester found — skipping section seeding.");
            return;
        }

        Semester semester = activeSemester.get();

        // Fall semester (order=1) courses only
        List<Course> fallCourses = courseRepository.findAllWithDetails().stream()
                .filter(c -> c.getSemesterOrder() == semester.getOrderInYear())
                .toList();

        if (!sectionRepository.findBySemesterId(semester.getId()).isEmpty()) {
            log.info("Sections already seeded for {} {} — skipping.", semester.getName(), semester.getYear());
            return;
        }

        // Group teachers by specialization id
        Map<Long, List<Teacher>> teachersBySpec = new HashMap<>();
        for (Teacher t : teacherRepository.findAll()) {
            teachersBySpec.computeIfAbsent(t.getSpecialization().getId(), k -> new ArrayList<>()).add(t);
        }

        // Group classrooms by type pattern
        List<Classroom> standardRooms = new ArrayList<>();
        List<Classroom> labs = new ArrayList<>();
        List<Classroom> artRooms = new ArrayList<>();
        List<Classroom> musicRooms = new ArrayList<>();
        List<Classroom> gyms = new ArrayList<>();
        List<Classroom> compLabs = new ArrayList<>();

        for (Classroom c : classroomRepository.findAll()) {
            String name = c.getName();
            if (name.startsWith("Room")) standardRooms.add(c);
            else if (name.startsWith("Lab")) labs.add(c);
            else if (name.startsWith("Art")) artRooms.add(c);
            else if (name.startsWith("Music")) musicRooms.add(c);
            else if (name.startsWith("Gym")) gyms.add(c);
            else if (name.startsWith("CompLab")) compLabs.add(c);
        }

        // Rotating time slots: day pattern + start/end
        record TimeSlot(String days, String start, String end) {}
        List<TimeSlot> slots = List.of(
                new TimeSlot("MON,WED,FRI", "08:00", "09:00"),
                new TimeSlot("TUE,THU",     "08:00", "09:30"),
                new TimeSlot("MON,WED,FRI", "09:00", "10:00"),
                new TimeSlot("TUE,THU",     "09:30", "11:00"),
                new TimeSlot("MON,WED,FRI", "10:00", "11:00"),
                new TimeSlot("TUE,THU",     "11:00", "12:30"),
                new TimeSlot("MON,WED,FRI", "11:00", "12:00"),
                new TimeSlot("TUE,THU",     "13:00", "14:30"),
                new TimeSlot("MON,WED,FRI", "13:00", "14:00"),
                new TimeSlot("TUE,THU",     "14:30", "16:00"),
                new TimeSlot("MON,WED,FRI", "14:00", "15:00"),
                new TimeSlot("MON,WED,FRI", "15:00", "16:00")
        );

        // Track index per specialization for teacher rotation
        Map<Long, Integer> teacherIdx = new HashMap<>();
        // Track index per room group
        Map<String, Integer> roomIdx = new HashMap<>();
        int slotIdx = 0;

        for (Course course : fallCourses) {
            Long specId = course.getSpecialization().getId();
            List<Teacher> teachers = teachersBySpec.getOrDefault(specId, Collections.emptyList());
            if (teachers.isEmpty()) continue;

            int tIdx = teacherIdx.getOrDefault(specId, 0);
            Teacher teacher = teachers.get(tIdx % teachers.size());
            teacherIdx.put(specId, tIdx + 1);

            Classroom classroom = pickClassroom(specId, standardRooms, labs, artRooms, musicRooms, gyms, compLabs, roomIdx);
            TimeSlot slot = slots.get(slotIdx % slots.size());
            slotIdx++;

            CourseSection section = new CourseSection();
            section.setCourse(course);
            section.setSemester(semester);
            section.setTeacher(teacher);
            section.setClassroom(classroom);
            section.setDaysOfWeek(slot.days());
            section.setStartTime(slot.start());
            section.setEndTime(slot.end());

            sectionRepository.save(section);
            log.info("Seeded section: {} | {} | {} {}-{}", course.getCode(), teacher.getLastName(), slot.days(), slot.start(), slot.end());
        }

        log.info("Seeded {} sections for {} {}", fallCourses.size(), semester.getName(), semester.getYear());
    }

    private Classroom pickClassroom(Long specId,
                                     List<Classroom> standard, List<Classroom> labs,
                                     List<Classroom> art, List<Classroom> music,
                                     List<Classroom> gyms, List<Classroom> compLabs,
                                     Map<String, Integer> idx) {
        // Science
        if (specId == 3L) return rotate(labs, "lab", idx);
        // Arts
        if (specId == 5L) return rotate(art, "art", idx);
        // Music
        if (specId == 6L) return rotate(music, "music", idx);
        // PE
        if (specId == 7L) return rotate(gyms, "gym", idx);
        // CS
        if (specId == 8L) return rotate(compLabs, "comp", idx);
        // All others: standard room
        return rotate(standard, "std", idx);
    }

    private Classroom rotate(List<Classroom> list, String key, Map<String, Integer> idx) {
        int i = idx.getOrDefault(key, 0);
        Classroom c = list.get(i % list.size());
        idx.put(key, i + 1);
        return c;
    }
}
