package net.penguincoders.doit.Model;

import java.util.List;

public class ToDoModel {
    private int id;
    private int status;
    private List<ToDoModel> childList;
    private String task;

    @Override
    public String toString() {
        return "ToDoModel{" +
                "id=" + id +
                ", status=" + status +
                ", childList=" + childList +
                ", task='" + task + '\'' +
                '}';
    }

    public ToDoModel(int id,
                     String task,
                     int status,
                     List<ToDoModel> childList) {
        this.id = id;
        this.task = task;
        this.status = status;
        this.childList = childList;
    }

    public List<ToDoModel> getChildList() {
        return childList;
    }

    public void setChildList(List<ToDoModel> childList) {
        this.childList = childList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }
}
