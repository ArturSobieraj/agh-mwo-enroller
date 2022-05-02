package com.company.enroller.persistence;

import java.util.Collection;
import java.util.Optional;

import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.company.enroller.model.Meeting;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unchecked")
@Repository("meetingRepository")
public class MeetingRepository {
	DatabaseConnector connector;

	public MeetingRepository() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Meeting> getAll() {
		String hql = "FROM Meeting";
		Query<Meeting> query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Optional<Meeting> findMeetingById(Long id) {
		String hql = "FROM Meeting WHERE id is :id";
		Query<Meeting> meetingQuery = connector.getSession().createQuery(hql).setParameter("id", id);
		return meetingQuery.uniqueResultOptional();
	}

	public void createNewMeeting(Meeting newMeeting) {
		Transaction addMeetingTransaction = connector.getSession().beginTransaction();
		connector.getSession().save(newMeeting);
		addMeetingTransaction.commit();
	}

	public void updateMeetingWithNewParticipant(Meeting meetingWithNewParticipant) {
		Transaction updateMeetingWithNewParticipantTransaction = connector.getSession().beginTransaction();
		connector.getSession().update(meetingWithNewParticipant);
		updateMeetingWithNewParticipantTransaction.commit();
	}

	public void deleteMeeting(Meeting meeting) {
		Transaction deleteMeetingTransaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		deleteMeetingTransaction.commit();
	}

	public void updateMeeting(Meeting meeting) {
		Transaction updateMeetingTransaction = connector.getSession().beginTransaction();
		connector.getSession().update(meeting);
		updateMeetingTransaction.commit();
	}
}
