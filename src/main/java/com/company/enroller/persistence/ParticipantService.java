package com.company.enroller.persistence;

import java.util.Collection;
import java.util.Optional;

import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Participant;

@SuppressWarnings("unchecked")
@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		String hql = "FROM Participant";
		Query<Participant> query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Optional<Participant> findByLogin(String participantLogin) {
		String hql = "FROM Participant WHERE login is :login";
		Query<Participant> query = connector.getSession().createQuery(hql).setParameter("login", participantLogin);
		//connector.getSession().get(Participant.class, login);
		return query.uniqueResultOptional();
	}

	public void deleteParticipant(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(participant);
		transaction.commit();
	}

	public void addParticipant(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(participant);
		transaction.commit();
	}

	public void updateParticipant(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(participant);
		transaction.commit();
	}
}
