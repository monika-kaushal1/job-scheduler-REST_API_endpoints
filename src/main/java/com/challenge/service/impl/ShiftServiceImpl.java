package com.challenge.service.impl;

import com.challenge.repository.JobRepository;
import com.challenge.repository.ShiftRepository;
import com.challenge.entity.Shift;
import com.challenge.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ShiftRepository shiftRepository;


    @Override
    @Transactional
    public List<Shift> cancelShiftsForTalent(UUID talentId) {
        List<Shift> shifts = shiftRepository.findByTalentId(talentId);

        if (shifts.isEmpty()) {
            // Return an empty list or handle accordingly
            return shifts;
        }
        // Delete existing shifts
        shiftRepository.deleteByTalentId(talentId);

        // Create replacement shifts with the same dates
        return createReplacementShifts(shifts);
    }

    // Helper method to create replacement shifts with the same dates
    private List<Shift> createReplacementShifts(List<Shift> shifts) {

        return shiftRepository.saveAll(shifts.stream().map(s -> {
            Shift replacementShift = new Shift();
            replacementShift.setId(UUID.randomUUID());
            replacementShift.setStartTime(s.getStartTime());
            replacementShift.setEndTime(s.getEndTime());
            replacementShift.setJob(s.getJob());
            return replacementShift;
        }).collect(Collectors.toList()));
    }
}



