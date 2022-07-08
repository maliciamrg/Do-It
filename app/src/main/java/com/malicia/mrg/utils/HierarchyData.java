package com.malicia.mrg.utils;

public class HierarchyData {
    Integer taskId;
    Integer level;

    public HierarchyData(Integer taskId, Integer level) {
        this.taskId = taskId;
        this.level = level;
    }

    public HierarchyData() {
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
