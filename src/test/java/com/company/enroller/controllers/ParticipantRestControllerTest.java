package com.company.enroller.controllers;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collection;
import java.util.Optional;

import com.company.enroller.exception.BadParticipantIdException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RunWith(SpringRunner.class)
@WebMvcTest(ParticipantRestController.class)
public class ParticipantRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ParticipantService participantService;

	public Participant participant;
	public String inputJSON;

	@Before
	public void setTestData() {
		participant = new Participant();
		participant.setLogin("test");
		participant.setPassword("test");
		inputJSON = "{\"login\":\"test\", \"password\":\"test\"}";
	}

	@Test
	public void getParticipants() throws Exception {
		Collection<Participant> allParticipants = singletonList(participant);
		given(participantService.getAll()).willReturn(allParticipants);
		mvc.perform(get("/participants").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1))).andExpect(jsonPath("$[0].login", is(participant.getLogin())));
	}

	@Test
	public void addParticipant() throws Exception {
		given(participantService.findByLogin("test")).willReturn(Optional.empty());
		doNothing().when(participantService).addParticipant(participant);
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());

		given(participantService.findByLogin("test")).willReturn(Optional.of(participant));
		mvc.perform(post("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isConflict());

		verify(participantService, times(2)).findByLogin("test");
	}

	@Test
	public void testUpdateParticipants() throws Exception {
		given(participantService.findByLogin("test")).willReturn(Optional.of(participant));
		doNothing().when(participantService).updateParticipant(participant);
		mvc.perform(put("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		given(participantService.findByLogin("test")).willReturn(Optional.empty());
		mvc.perform(put("/participants").content(inputJSON).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

		verify(participantService, times(2)).findByLogin("test");
		verify(participantService, times(1)).updateParticipant(participant);
	}

	@Test
	public void testDeleteParticipant() throws Exception {
		given(participantService.findByLogin("test")).willReturn(Optional.of(participant));
		doNothing().when(participantService).deleteParticipant(participant);
		mvc.perform(delete("/participants/test").content(inputJSON).contentType(MediaType.APPLICATION_JSON));

		given(participantService.findByLogin("test")).willReturn(Optional.empty());
		mvc.perform(delete("/participants/test").content(inputJSON).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof BadParticipantIdException));

		verify(participantService, times(2)).findByLogin("test");
		verify(participantService, times(1)).deleteParticipant(participant);
	}
}
