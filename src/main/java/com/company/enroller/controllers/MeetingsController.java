package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.DatabaseConnector;
import com.company.enroller.persistence.MeetingRepository;
import com.company.enroller.persistence.ParticipantService;
import com.company.enroller.service.MeetingService;
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
    private final MeetingRepository meetingRepository;
    private final ParticipantService participantService;
    private final MeetingService meetingService;

    @GetMapping
    public ResponseEntity<?> getMeetings() {
        Collection<Meeting> meetings = meetingRepository.getAll();
        return new ResponseEntity<>(meetings, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeeting(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingRepository.findMeetingById(id);
        if (meeting.isPresent()) {
            return new ResponseEntity<>(meeting.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/participants/{id}")
    public ResponseEntity<?> getMeetingParticipants(@PathVariable Long id) {
        Optional<Meeting> meeting = meetingRepository.findMeetingById(id);
        if (meeting.isPresent()) {
            return new ResponseEntity<>(meeting.get().getParticipants(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> createNewMeeting(@RequestBody Meeting meeting) {
        meetingRepository.createNewMeeting(meeting);
        return new ResponseEntity<>("Meeting: " + meeting.getTitle() + " added", HttpStatus.OK);
    }

    @PutMapping("/{meetingId}")
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable Long meetingId,
                                                     @RequestParam String participantLogin) {
        Optional<Meeting> meetingOptional = meetingRepository.findMeetingById(meetingId);
        if (meetingOptional.isPresent()) {
            return meetingService.addParticipantToMeeting(participantService.findByLogin(participantLogin), meetingOptional.get());
        } else {
            return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMeeting(@PathVariable Long id) {
        Optional<Meeting> meetingToDelete = meetingRepository.findMeetingById(id);
        if (meetingToDelete.isPresent()) {
            meetingRepository.deleteMeeting(meetingToDelete.get());
            return new ResponseEntity<>("Meeting deleted", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Meeting to delete not found", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateMeeting(@RequestBody Meeting meeting) {
        if (meetingService.checkIfMeetingIsPresent(meeting.getId())) {
            DatabaseConnector.getInstance().closeSession();
            DatabaseConnector.getInstance().openSessionIfClosed();
            meetingRepository.updateMeeting(meeting);
            return new ResponseEntity<>("Meeting updated", HttpStatus.OK);
        } else {
            meetingRepository.createNewMeeting(meeting);
            return new ResponseEntity<>("Meeting not found. New meeting was added", HttpStatus.OK);
        }
    }

    @PutMapping("/deleteParticipant/{meetingId}")
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable Long meetingId, @RequestParam String participantLogin) {
        Optional<Participant> participantToRemove = participantService.findByLogin(participantLogin);
        if (participantToRemove.isPresent()) {
            return meetingService.removeParticipantFromMeetingIfMeetingExists(meetingId, participantToRemove.get());
        } else {
            return new ResponseEntity<>("Participant not exists", HttpStatus.NOT_FOUND);
        }
    }
}
