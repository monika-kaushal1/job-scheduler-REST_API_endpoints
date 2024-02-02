package com.challenge.service.impl;

import com.challenge.repository.JobRepository;
import com.challenge.repository.ShiftRepository;
import com.challenge.entity.Job;
import com.challenge.entity.Shift;
import com.challenge.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@RequiredArgsConstructor
@Service
@Transactional
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ShiftRepository shiftRepository;

    @Override
    public Job createJob(UUID uuid, LocalDate date1, LocalDate date2) {
        Job job = Job.builder()
                .id(uuid)
                .companyId(UUID.randomUUID())
                .startTime(date1.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                .endTime(date2.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                .build();
        job.setShifts(LongStream.range(0, ChronoUnit.DAYS.between(date1, date2))
                .mapToObj(idx -> date1.plus(idx, ChronoUnit.DAYS))
                .map(date -> Shift.builder()
                        .id(UUID.randomUUID())
                        .job(job)
                        .startTime(date.atTime(8, 0, 0).toInstant(ZoneOffset.UTC))
                        .endTime(date.atTime(17, 0, 0).toInstant(ZoneOffset.UTC))
                        .build())
                .collect(Collectors.toList()));

        job.getShifts().forEach(s -> System.out.println(s.toString()));
        return jobRepository.save(job);
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteJob(UUID jobId) {
        // Check if the job exists
        if (!jobRepository.existsById(jobId)) {
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
        }

        try {
            // Delete associated shifts
            shiftRepository.deleteByJobId(jobId);

            // Delete the job
            jobRepository.deleteById(jobId);

            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes
            return new ResponseEntity<>("Failed to delete job and associated shifts", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<Shift> getShifts(UUID id) {
        return shiftRepository.findAllByJobId(id);
    }

    @Override
    public void bookTalent(UUID shiftId, UUID talent) {
        shiftRepository.findById(shiftId).map(shift -> shiftRepository.save(shift.setTalentId(talent)));

    }

    @Override
    public ResponseEntity<String> deleteShift(UUID jobId, UUID shiftId) {
        // Check if the job exists
        if (!jobRepository.existsById(jobId)) {
            return new ResponseEntity<>("Job not found", HttpStatus.NOT_FOUND);
        }
        // Check if the shift exists for the job
        if (!shiftRepository.existsById(shiftId)) {
            return new ResponseEntity<>("Shift not found for input jobId ", HttpStatus.NOT_FOUND);
        }
        try {
            // Delete the shift
            shiftRepository.deleteById(shiftId);

            //Delete entry from Job for associated shiftId
            // jobRepository.deleteShiftForJob(jobId, shiftId);

            return new ResponseEntity<>("Job deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging purposes
            return new ResponseEntity<>("Failed to delete job and associated shifts", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> reassignShift(UUID talentId) {
        //find and delete all shifts associated with talentId
        List<Shift> shifts = shiftRepository.findByTalentId(talentId);

        return null;
    }
}
