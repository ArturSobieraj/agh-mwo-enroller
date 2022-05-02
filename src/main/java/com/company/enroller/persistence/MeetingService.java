package com.company.enroller.persistence;

import java.util.Collection;
import java.util.Optional;

import com.company.enroller.model.Participant;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.company.enroller.model.Meeting;

@SuppressWarnings("unchecked")
@Component("meetingService")
public class MeetingService {
	DatabaseConnector connector;

	public MeetingService() {
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

	public Optional<Meeting> findMeetingByTitleAndDate(String title, String date) {
		String hgl = "FROM Meeting WHERE title is :title and date is :date";
		Query<Meeting> query = connector.getSession().createQuery(hgl);
		query.setParameter("title", title);
		query.setParameter("date", date);
		return query.uniqueResultOptional();
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

	public ResponseEntity<?> addParticipantToMeeting(Optional<Participant> participantToAdd, Meeting meeting) {
		if (participantToAdd.isPresent()) {
			meeting.getParticipants().add(participantToAdd.get());
			updateMeetingWithNewParticipant(meeting);
			return new ResponseEntity<>("Participant added", HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Participant to add not found", HttpStatus.NOT_FOUND);
		}
	}

	public void deleteMeeting(Meeting meeting) {
		Transaction deleteMeetingTransaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		deleteMeetingTransaction.commit();
	}
}
