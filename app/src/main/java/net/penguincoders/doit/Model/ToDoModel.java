package net.penguincoders.doit.Model;

import java.io.Serializable;

public class ToDoModel implements Serializable {

    private int id;
    private boolean isProject;
    private boolean isPostIt;
    private boolean status;
    private String task;
    private Integer backgroundColor;

    public ToDoModel() {
    }

    public ToDoModel(int id,
                     String task,
                     boolean isProject,
                     boolean isPostIt,
                     boolean status,
                     int backgroundColor) {
        this.id = id;
        this.isProject = isProject;
        this.isPostIt = isPostIt;
        this.status = status;
        this.task = task;
        this.backgroundColor = backgroundColor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isProject() {
        return isProject;
    }

    public boolean isPostIt() {
        return isPostIt;
    }
    public void setProject(boolean project) {
        isProject = project;
    }
    public void setPostIt(boolean postit) {
        isPostIt = postit;
    }
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


}
