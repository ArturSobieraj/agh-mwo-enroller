package com.company.enroller.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Entity
@Data
@Table(name = "meeting")
public class Meeting {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String title;

	@Column
	private String description;

	@Column
	private String date;

	@JsonIgnore
	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinTable(name = "meeting_participant", joinColumns = { @JoinColumn(name = "meeting_id") }, inverseJoinColumns = {
			@JoinColumn(name = "participant_login") })
	Set<Participant> participants = new HashSet<>();
}