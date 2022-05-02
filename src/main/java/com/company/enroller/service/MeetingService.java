package com.company.enroller.service;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service("meetingService")
@RequiredArgsConstructor
public class MeetingService {
    private final MeetingRepository meetingRepository;

    public ResponseEntity<?> addParticipantToMeeting(Optional<Participant> participantToAdd, Meeting meeting) {
        if (participantToAdd.isPresent()) {
            meeting.getParticipants().add(participantToAdd.get());
            meetingRepository.updateMeetingWithNewParticipant(meeting);
            return new ResponseEntity<>("Participant added", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Participant to add not found", HttpStatus.NOT_FOUND);
        }
    }

    public boolean checkIfMeetingIsPresent(Long id) {
        return meetingRepository.findMeetingById(id).isPresent();
    }

    public ResponseEntity<?> removeParticipantFromMeetingIfMeetingExists(Long meetingId, Participant participant) {
        Optional<Meeting> meeting = meetingRepository.findMeetingById(meetingId);
        if (meeting.isPresent()) {
            return removeParticipantIfIsIncludedInMeeting(meeting.get(), participant);
        }
        return new ResponseEntity<>("Meeting not found", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> removeParticipantIfIsIncludedInMeeting(Meeting meeting, Participant participant) {
        for (Participant participantToCheck : new HashSet<>(meeting.getParticipants())) {
            if (participantToCheck.getLogin().equals(participant.getLogin())) {
                return removeParticipant(meeting, participantToCheck);
            }
        }
        return new ResponseEntity<>("Participant not included in meeting", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> removeParticipant(Meeting meeting, Participant participant) {
        meeting.getParticipants().remove(participant);
        meetingRepository.updateMeeting(meeting);
        return new ResponseEntity<>("Participant removed", HttpStatus.OK);
    }
}
