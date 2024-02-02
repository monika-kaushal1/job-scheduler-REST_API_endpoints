package com.challenge.service;

import com.challenge.entity.Shift;

import java.util.List;
import java.util.UUID;

public interface ShiftService {
    List<Shift> cancelShiftsForTalent(UUID talentId);
}
