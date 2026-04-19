package com.maplewood.service;

import com.maplewood.dto.response.SectionResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.repository.CourseSectionRepository;
import com.maplewood.repository.SemesterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    private final CourseSectionRepository sectionRepository;
    private final SemesterRepository semesterRepository;

    public SectionService(CourseSectionRepository sectionRepository,
                          SemesterRepository semesterRepository) {
        this.sectionRepository = sectionRepository;
        this.semesterRepository = semesterRepository;
    }

    public List<SectionResponse> getActiveSemesterSections(Integer gradeLevel) {
        var semester = semesterRepository.findByIsActive(1)
                .orElseThrow(() -> new ResourceNotFoundException("No active semester found."));
        var sections = gradeLevel != null
                ? sectionRepository.findBySemesterIdAndGradeLevel(semester.getId(), gradeLevel)
                : sectionRepository.findBySemesterId(semester.getId());
        return sections.stream().map(SectionResponse::from).toList();
    }

    public SectionResponse getById(Long id) {
        return sectionRepository.findById(id)
                .map(SectionResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + id));
    }
}
