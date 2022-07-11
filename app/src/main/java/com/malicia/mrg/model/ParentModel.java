package com.malicia.mrg.model;


import java.util.List;

public class ParentModel {
    public int linkId;
    public int taskId;
    public String taskName;
    public int taskParentId;
    private List<ParentModel> childList;

    public ParentModel(int linkId, int taskId, String taskName, int taskParentId) {
        this.linkId = linkId;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskParentId = taskParentId;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskParentId() {
        return taskParentId;
    }

    public void setTaskParentId(int taskParentId) {
        this.taskParentId = taskParentId;
    }

    public List<ParentModel> getChildList() {
        return childList;
    }

    public void setChildList(List<ParentModel> childList) {
        this.childList = childList;
    }
}
