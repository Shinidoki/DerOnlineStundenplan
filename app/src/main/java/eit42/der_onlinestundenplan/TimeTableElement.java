package eit42.der_onlinestundenplan;

/**
 * Created by L.Schnitzmeier on 13.05.2016.
 */
public class TimeTableElement {

    public String hour;
    public String subject;
    public String teacher;
    public String room;


    public TimeTableElement(String pHour, String pSubject, String pTeacher, String pRoom)
    {
        hour = pHour;
        subject = pSubject;
        teacher = pTeacher;
        room = pRoom;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }


}
