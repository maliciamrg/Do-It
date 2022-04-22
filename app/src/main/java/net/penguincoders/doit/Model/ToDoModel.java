package net.penguincoders.doit.Model;

import java.io.Serializable;
import java.util.List;

public class ToDoModel implements Serializable {
    private static final long serialVersionUID = 0;
    private int id;
    private boolean isProject;
    private boolean status;
    private String task;
    private List<ToDoModel> childList;
    private List<ToDoModel> parentList;
    private int hierarchicalRoot;
    private int hierarchicalRank;
    private int hierarchicalLevel;
    private int hierarchicalRootNbSubtask;
    private int backgroundColor;

    public ToDoModel(int id,
                     String task,
                     boolean isProject,
                     boolean status,
                     List<ToDoModel> childList,
                     List<ToDoModel> parentList) {
        this.id = id;
        this.task = task;
        this.isProject = isProject;
        this.status = status;
        this.childList = childList;
        this.parentList = parentList;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<ToDoModel> getParentList() {
        return parentList;
    }

    public void setParentList(List<ToDoModel> parentList) {
        this.parentList = parentList;
    }

    @Override
    public String toString() {
        return "ToDoModel{" +
                "id=" + id +
                ", isProject=" + isProject +
                ", status=" + status +
                ", task='" + task + '\'' +
                ", childList=" + childList +
                ", parentList=" + parentList +
                '}';
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

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isParent(int intExtra) {
        for (ToDoModel element : childList) {
            if(element.getId()==intExtra){return true;}
        }
        return false;
    }


    public static String childListToString(List<ToDoModel> childList) {
        StringBuilder str = new StringBuilder();
        str.append("Child(s):");
        for (ToDoModel element : childList) {
            str.append("\n");
            str.append(" ");
            str.append(element.isStatus() ? "☒" : "☐");
            str.append("-");
            str.append(stringMax(element.getTask()));
//            str.append("\n");
        }
        return str.toString();
    }

    public static  String parentListToString(List<ToDoModel> parentList) {
        StringBuilder str = new StringBuilder();
        str.append("Parent(s):");
        for (ToDoModel element : parentList) {
            str.append("\n");
            str.append(" --> ");
            str.append(element.isStatus() ? "☒" : "☐");
            str.append("-");
            str.append(stringMax(element.getTask()));
//            str.append("\n");
        }
        return str.toString();
    }
    public static  String stringMax(String stringExtra) {
        int imax = 60;
        return stringExtra.length() > imax ? stringExtra.substring(0, imax) : stringExtra;
    }

    public boolean isProject() {
        return isProject;
    }

    public void setProject(boolean project) {
        isProject = project;
    }

    public void setHierarchicalRoot(int hierarchicalRoot) {
        this.hierarchicalRoot = hierarchicalRoot;
    }

    public int getHierarchicalRoot() {
        return hierarchicalRoot;
    }

    public void setHierarchicalRank(int hierarchicalRank) {
        this.hierarchicalRank = hierarchicalRank;
    }

    public int getHierarchicalRank() {
        return hierarchicalRank;
    }

    public void setHierarchicalLevel(int hierarchicalLevel) {
        this.hierarchicalLevel = hierarchicalLevel;
    }

    public int getHierarchicalLevel() {
        return hierarchicalLevel;
    }

    public void setHierarchicalRootNbSubtask(int hierarchicalRootNbSubtask) {
        this.hierarchicalRootNbSubtask = hierarchicalRootNbSubtask;
    }

    public int getHierarchicalRootNbSubtask() {
        return hierarchicalRootNbSubtask;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }
}
