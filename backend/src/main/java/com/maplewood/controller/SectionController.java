package com.maplewood.controller;

import com.maplewood.dto.response.SectionResponse;
import com.maplewood.service.SectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @GetMapping
    public ResponseEntity<List<SectionResponse>> getAll(
            @RequestParam(required = false) Integer gradeLevel) {
        return ResponseEntity.ok(sectionService.getActiveSemesterSections(gradeLevel));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sectionService.getById(id));
    }
}
