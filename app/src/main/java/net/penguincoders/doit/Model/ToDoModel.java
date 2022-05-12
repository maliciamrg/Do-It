package net.penguincoders.doit.Model;

import java.io.Serializable;
import java.util.List;

public class ToDoModel implements Serializable {

    private int id;
    private boolean isProject;
    private boolean status;
    private String task;
    private int backgroundColor;

    public ToDoModel() {
    }

    public ToDoModel(int id,
                     String task,
                     boolean isProject,
                     boolean status,
                     int backgroundColor) {
        this.id = id;
        this.isProject = isProject;
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

    public void setProject(boolean project) {
        isProject = project;
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
