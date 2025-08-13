package com.stacklog.class_service.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stacklog.class_service.model.entities.Semester;
import com.stacklog.class_service.model.entities.Semester.Quarter;
import com.stacklog.class_service.model.service.SemesterService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/semester")
public class SemesterRestController {

    @Autowired
    SemesterService semesterService;

    @GetMapping("")
    public ResponseEntity<List<Semester>> getAllSemester(@RequestHeader("Authorization") String token,
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "quarter", required = false) String quarter) {
        List<Semester> semesters = new ArrayList<>();
        if (year == null && quarter == null) {
            semesters = semesterService.getAllByUserId(token);
        } else {
            semesters = semesterService.getAllByYearAndQuarter(year, quarter, token);
        }
        return ResponseEntity.ok().body(semesters);
    }

    @PostMapping("")
    public ResponseEntity<Semester> saveSemester(@RequestHeader("Authorization") String token,
            @RequestBody SemesterDTO semesterDTO) {
        Semester semester = new Semester();
        semester.setSemesterId(semesterDTO.getSemesterId());
        semester.setSemesterName(semesterDTO.getSemesterYear() + "-" + semesterDTO.getQuarter());
        semester.setSemesterStartDate(semesterDTO.getSemesterStartDate());
        semester.setSemesterEndDate(semesterDTO.getSemesterEndDate());
        semester.setSemesterYear(semesterDTO.getSemesterYear());
        semester.setQuarter(semesterDTO.getQuarter());
        semester = semesterService.save(semester, token);
        if (semester == null) {
            return ResponseEntity.badRequest().body(null);
        }
        return ResponseEntity.ok().body(semester);
    }
    

}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class SemesterDTO {
    private String semesterId;
    private Integer semesterYear;
    private LocalDate semesterStartDate;
    private LocalDate semesterEndDate;
    private Quarter quarter;
}
