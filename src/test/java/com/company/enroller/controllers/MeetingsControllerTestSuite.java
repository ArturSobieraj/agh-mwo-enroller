package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingRepository;
import com.company.enroller.persistence.ParticipantService;
import com.company.enroller.service.MeetingService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(MeetingsController.class)
public class MeetingsControllerTestSuite {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MeetingService meetingService;

    @MockBean
    private MeetingRepository meetingRepository;

    @MockBean
    private ParticipantService participantService;

    private Participant participant;
    private Meeting meeting;
    private String inputMeetingJSON;

    @Before
    public void setTestData() {
        participant = new Participant();
        participant.setLogin("test");
        participant.setPassword("test");
        meeting = new Meeting();
        meeting.setId(1);
        meeting.setTitle("testTitle");
        meeting.setDescription("testDescription");
        meeting.setDate("testDate");
        inputMeetingJSON = "{\"id\": 1, \"title\":\"testTitle\", \"description\":\"testDescription\", \"date\":\"testDate\"}";
    }

    @Test
    public void testAllMeetings() throws Exception {
        Collection<Meeting> meetings = Collections.singleton(meeting);
        given(meetingRepository.getAll()).willReturn(meetings);

        mvc.perform(get("/meetings").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int) meeting.getId())));
    }

    @Test
    public void testGetSingleMeeting() throws Exception {
        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.of(meeting));
        mvc.perform(get("/meetings/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) meeting.getId())));

        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.empty());
        mvc.perform(get("/meetings/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(meetingRepository, times(2)).findMeetingById(1L);
    }

    @Test
    public void testGetMeetingParticipant() throws Exception {
        meeting.setParticipants(new HashSet<>(Collections.singleton(participant)));
        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.of(meeting));
        mvc.perform(get("/meetings/participants/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].login", is(participant.getLogin())));
    }

    @Test
    public void testCreateMeeting() throws Exception {
        doNothing().when(meetingRepository).createNewMeeting(meeting);
        mvc.perform(post("/meetings").content(inputMeetingJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddParticipantToMeeting() throws Exception {
        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.of(meeting));
        given(meetingService.addParticipantToMeeting(any(), any())).willReturn(new ResponseEntity<>(HttpStatus.OK));
        mvc.perform(put("/meetings/1?participantLogin=user1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.empty());
        mvc.perform(put("/meetings/1?participantLogin=user1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(meetingRepository, times(2)).findMeetingById(1L);
        verify(meetingService, times(1)).addParticipantToMeeting(any(), any());
    }

    @Test
    public void testDeleteMeeting() throws Exception {
        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.of(meeting));
        doNothing().when(meetingRepository).deleteMeeting(meeting);
        mvc.perform(delete("/meetings/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(meetingRepository.findMeetingById(1L)).willReturn(Optional.empty());
        mvc.perform(delete("/meetings/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(meetingRepository, times(2)).findMeetingById(1L);
        verify(meetingRepository, times(1)).deleteMeeting(meeting);
    }

    @Test
    public void testUpdateMeeting() throws Exception {
        given(meetingService.checkIfMeetingIsPresent(any())).willReturn(true);
        doNothing().when(meetingRepository).updateMeeting(meeting);
        mvc.perform(put("/meetings").content(inputMeetingJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(meetingService.checkIfMeetingIsPresent(any())).willReturn(false);
        doNothing().when(meetingRepository).createNewMeeting(meeting);
        mvc.perform(put("/meetings").content(inputMeetingJSON).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(meetingService, times(2)).checkIfMeetingIsPresent(any());
        verify(meetingRepository, times(1)).updateMeeting(meeting);
        verify(meetingRepository, times(1)).createNewMeeting(meeting);
    }

    @Test
    public void testRemoveParticipantFromMeeting() throws Exception {
        given(participantService.findByLogin("user1")).willReturn(Optional.of(participant));
        given(meetingService.removeParticipantFromMeetingIfMeetingExists(1L, participant))
                .willReturn(new ResponseEntity<>(HttpStatus.OK));
        mvc.perform(put("/meetings/deleteParticipant/1?participantLogin=user1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        given(participantService.findByLogin("user1")).willReturn(Optional.empty());
        mvc.perform(put("/meetings/deleteParticipant/1?participantLogin=user1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(participantService, times(2)).findByLogin("user1");
        verify(meetingService, times(1)).removeParticipantFromMeetingIfMeetingExists(1L, participant);
    }
}
