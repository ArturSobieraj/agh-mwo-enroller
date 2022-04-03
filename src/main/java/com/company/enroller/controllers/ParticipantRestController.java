package com.company.enroller.controllers;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.company.enroller.model.Participant;
import com.company.enroller.persistence.ParticipantService;

@RestController
@RequestMapping("/participants")
public class ParticipantRestController {

	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getParticipants() {
		Collection<Participant> participants = participantService.getAll();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getParticipantsById(@PathVariable String id) {
		Optional<Participant> participant = participantService.findByLogin(id);
		if (participant.isEmpty()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Participant>(participant.get(), HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public void deleteParticipantById(@PathVariable String id) {
		Optional<Participant> participant = participantService.findByLogin(id);
		if (participant.isPresent()) {
			participantService.deleteParticipant(participant.get());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@PostMapping
	public ResponseEntity<?> addParticipant(@RequestBody Participant participant) {
		Optional<Participant> existingParticipant = participantService.findByLogin(participant.getLogin());
		if (existingParticipant.isPresent()) {
			return new ResponseEntity<>("Unable to create. A participant with login " + participant.getLogin() + " already exist.", HttpStatus.CONFLICT);
		} else {
			participantService.addParticipant(participant);
			return new ResponseEntity<>("User " + participant.getLogin() + " added", HttpStatus.OK);
		}
	}

	@PutMapping
	public ResponseEntity<?> updateParticipant(@RequestBody Participant participant) {
		Optional<Participant> exisitingParticipant = participantService.findByLogin(participant.getLogin());
		if (exisitingParticipant.isPresent()) {
			participantService.updateParticipant(participant);
			return new ResponseEntity<>("Participant " + participant.getLogin() + " updated", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Participant " + participant.getLogin() + " not exists", HttpStatus.NOT_FOUND);
		}
	}
}
