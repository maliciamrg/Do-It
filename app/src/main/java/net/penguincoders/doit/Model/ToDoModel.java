package net.penguincoders.doit.Model;

import java.util.List;

public class ToDoModel {
    private int id;
    private boolean status;
    private List<ToDoModel> childList;
    private String task;
    private boolean isParent;

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
                     boolean status,
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

    public boolean getStatus() {
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

    public void setParent(boolean isParent) {
        this.isParent = isParent;
    }

    public boolean isParent() {
        return isParent;
    }
}
