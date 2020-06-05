package com.ensias.iwimapp;

public class RowItem {
    private String course;
    private String prof;
    private String subject;
    private int    imageID;

    public RowItem(String course, String prof, String subject, int imageID) {
        this.course = course;
        this.prof = prof;
        this.subject = subject;
        this.imageID=imageID;
    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getCourse() {
        return course;
    }

    public String getProf() {
        return prof;
    }

    public String getSubject() {
        return subject;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setProf(String prof) {
        this.prof = prof;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
