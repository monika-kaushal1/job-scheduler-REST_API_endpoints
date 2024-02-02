package com.challenge.service;

import com.challenge.entity.Job;
import com.challenge.entity.Shift;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface JobService {
    Job createJob(UUID uuid, LocalDate date1, LocalDate date2);

    ResponseEntity<String> deleteJob(UUID jobId);

    List<Shift> getShifts(UUID id);

    void bookTalent(UUID shiftId, UUID talent);

    ResponseEntity<String> deleteShift(UUID jobId, UUID shiftId);

    ResponseEntity<String> reassignShift(UUID talentId);
}
