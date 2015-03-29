package com.my.dhreelife.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Obana on 7/21/13.
 */
public class Event {
    private String eventTitle;
    private String eventId;
	private String eventCreatorName;
    private int eventCreatorId;
    private Date eventCreatedDate;
    private String eventDescription;
    private List<User> participatedUser;
    private int eventStatus;
    private int numOfParticipant;
    private int alarmId = -1;
    
    

    public int getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(int alarmId) {
		this.alarmId = alarmId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}


    public int getNumOfParticipant() {
        return numOfParticipant;
    }

    public void setNumOfParticipant(int numOfParticipant) {
        this.numOfParticipant = numOfParticipant;
    }

    public void setEventStatus (int eventStatus)
    {
        this.eventStatus = eventStatus;
    }

    public int getEventStatus ()
    {
        return this.eventStatus;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventCreatorName() {
        return eventCreatorName;
    }

    public void setEventCreatorName(String eventCreatorName) {
        this.eventCreatorName = eventCreatorName;
    }

    public int getEventCreatorId() {
        return eventCreatorId;
    }

    public void setEventCreatorId(int eventCreatorId) {
        this.eventCreatorId = eventCreatorId;
    }

    public Date getEventCreatedDate() {
        return eventCreatedDate;
    }

    public void setEventCreatedDate(Date eventCreatedDate) {
        this.eventCreatedDate = eventCreatedDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Event(String eventTitle, String eventCreatorName,int eventCreatorId,Date eventCreatedDate,String eventDescription,int numOfParticipant)
    {
        this.eventTitle = eventTitle;
        this.eventCreatorName = eventCreatorName;
        this.eventCreatorId = eventCreatorId;
        this.eventCreatedDate = eventCreatedDate;
        this.eventDescription = eventDescription;
        this.numOfParticipant = numOfParticipant;
        participatedUser = new ArrayList<User>();
    }
    
    public Event()
    {
    	
    	
    }

    public void addUserToEvent(User user)
    {
        participatedUser.add(user);
    }

}
