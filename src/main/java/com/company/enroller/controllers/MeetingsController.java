package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/meetings")
@RequiredArgsConstructor
public class MeetingsController {
    private final MeetingService meetingService;
    private final ParticipantService participantService;

    @GetMapping
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingService.findMeetingById(id);
        if (meeting.isPresent()) {
            return new ResponseEntity<>(meeting.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<?> getMeetingParticipants(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingService.findMeetingById(id);
        if (meeting.isPresent()) {
            return new ResponseEntity<>(meeting.get().getParticipants(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createNewMeeting(@RequestBody Meeting meeting) {
        meetingService.createNewMeeting(meeting);
        return new ResponseEntity<>("Meeting: " + meeting.getTitle() + " added", HttpStatus.OK);
    }

    @PutMapping("/{meetingTitle}/{meetingDate}")
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable String meetingTitle,
                                                     @PathVariable String meetingDate,
                                                     @RequestParam String participantLogin) {
        Optional<Meeting> meetingOptional = meetingService.findMeetingByTitleAndDate(meetingTitle, meetingDate);
        if (meetingOptional.isPresent()) {
            return meetingService.addParticipantToMeeting(participantService.findByLogin(participantLogin), meetingOptional.get());
        } else {
            return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable Long id) {
        Optional<Meeting> meetingToDelete = meetingService.findMeetingById(id);
        if (meetingToDelete.isPresent()) {
            meetingService.deleteMeeting(meetingToDelete.get());
            return new ResponseEntity<>("Meeting deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Meeting to delete not found", HttpStatus.NOT_FOUND);
        }
    }
}
