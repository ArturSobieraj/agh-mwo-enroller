package com.company.enroller.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "participant")
public class Participant {

	@Id
	private String login;

	@Column
	private String password;
}
