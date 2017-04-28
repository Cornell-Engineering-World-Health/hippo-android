package edu.cornell.engineering.ewh.hippoandroid;

/**
 * Created by Albert on 4/27/2017.
 */


public class CallSession {
    private String endTime, startTime, dateTime, sessionId, name;
    private boolean active;
    private User[] participants;

    public CallSession(String endTime, String startTime, String dateTime, String sessionId, String name,
                   boolean active, User[] participants) {
        this.endTime = endTime;
        this.startTime = startTime;
        this.dateTime = dateTime;
        this.sessionId = sessionId;
        this.name = name;
        this.active = active;

        //TODO: Filter out yourself from participants.
        this.participants = participants;
    }

    public String getEndTime(){
        return endTime;
    }
    public String getStartTime(){
        return startTime;
    }
    public String getDateTime(){
        return dateTime;
    }
    public String getSessionId(){
        return sessionId;
    }
    public String getName(){
        return name;
    }
    public boolean getActive(){
        return active;
    }
    public User[] getParticipants(){
        return participants;
    }

    //TODO: Pretty print the times
    public String toString(){
        String desc =  name+"\nStart Time: "+startTime+"\nEnd Time: "+endTime+"\nParticipants: ";
        for(User u: participants){
            desc += u.toString()+", ";
        }
        return desc;
    }
}