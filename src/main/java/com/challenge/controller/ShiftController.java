package com.challenge.controller;

import com.challenge.dto.ResponseDto;
import com.challenge.entity.Shift;
import com.challenge.service.JobService;
import com.challenge.service.ShiftService;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping(path = "/shift")
@RequiredArgsConstructor
public class  ShiftController {

    @Autowired
    private JobService jobService;

    @Autowired
    private ShiftService shiftService;

    @GetMapping(path = "/{jobId}")
    @ResponseBody
    public ResponseDto<GetShiftsResponse> getShifts(@PathVariable("jobId") UUID uuid) {
        List<ShiftResponse> shiftResponses = jobService.getShifts(uuid).stream()
                .map(shift -> ShiftResponse.builder()
                        .id(shift.getId())
                        .talentId(shift.getTalentId())
                        .jobId(shift.getJob().getId())
                        .start(shift.getStartTime())
                        .end(shift.getEndTime())
                        .build())
                .collect(Collectors.toList());
        return ResponseDto.<GetShiftsResponse>builder()
                .data(GetShiftsResponse.builder()
                        .shifts(shiftResponses)
                        .build())
                .build();
    }

    @PatchMapping(path = "/{id}/book")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void bookTalent(@PathVariable("id") UUID shiftId, @RequestBody @Valid ShiftController.BookTalentRequestDto dto) {
        System.out.println("Given Shift Id  " + shiftId);
        jobService.bookTalent(shiftId, dto.talent);
    }

    @DeleteMapping("/{jobId}/{shiftId}")
    public ResponseEntity<String> deleteShift(@PathVariable("jobId") UUID jobId, @PathVariable("shiftId") UUID shiftId) {
        return jobService.deleteShift(jobId, shiftId);
    }

    @DeleteMapping("/cancelForTalent/{talentId}")
    public ResponseEntity<List<Shift>> reAssignShift(@PathVariable("talentId") UUID talentId) {
        List<Shift> replacementShifts = shiftService.cancelShiftsForTalent(talentId);
        if (replacementShifts.isEmpty()) {
            return ResponseEntity.ok().body(replacementShifts);
        }

        return ResponseEntity.ok().body(replacementShifts);
    }


    @NoArgsConstructor
    @Data
    private static class BookTalentRequestDto {
        UUID talent;
    }

    @Builder
    @Data
    private static class GetShiftsResponse {
        List<ShiftResponse> shifts;
    }

    @Builder
    @Data
    private static class ShiftResponse {
        UUID id;
        UUID talentId;
        UUID jobId;
        Instant start;
        Instant end;
    }
}
