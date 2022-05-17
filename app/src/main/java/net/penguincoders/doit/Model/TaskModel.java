package net.penguincoders.doit.Model;

import java.io.Serializable;
import java.util.List;

public class TaskModel implements Serializable {
    private static final long serialVersionUID = 0;
    private int id;
    private boolean isProject;
    private boolean isPostIt;
    private boolean status;
    private String task;
    private int backgroundColor;
    private List<TaskModel> childList;
    private List<TaskModel> parentList;
    private Integer hierarchicalRoot;
    private int hierarchicalRank;
    private int hierarchicalLevel;
    private int hierarchicalRootNbSubtask;
    private boolean checkable;
    public TaskModel(int id,
                     String task,
                     boolean isProject,
                     boolean isPostIt,
                     boolean status,
                     int backgroundColor,
                     List<TaskModel> childList,
                     List<TaskModel> parentList) {
        this.setPostIt(isPostIt);
        this.setId(id);
        this.setTask(task);
        this.setProject(isProject);
        this.setStatus(status);
        this.setBackgroundColor(backgroundColor);
        this.childList = childList;
        this.parentList = parentList;
        this.hierarchicalRoot = id;
    }

    public TaskModel(ToDoModel todoIn) {
        this.setId(todoIn.getId());
        this.setTask(todoIn.getTask());
        this.setProject(todoIn.isProject());
        this.setPostIt(todoIn.isPostIt());
        this.setStatus(todoIn.isStatus());
        this.setBackgroundColor(todoIn.getBackgroundColor());
    }

    public static String childListToString(List<TaskModel> childList) {
        StringBuilder str = new StringBuilder();
        str.append("Child(s):");
        for (TaskModel element : childList) {
            str.append("\n");
            str.append(" ");
            str.append(element.isStatus() ? "☒" : "☐");
            str.append("-");
            str.append(stringMax(element.getTask()));
//            str.append("\n");
        }
        return str.toString();
    }

    public static String parentListToString(List<TaskModel> parentList) {
        StringBuilder str = new StringBuilder();
        str.append("Parent(s):");
        for (TaskModel element : parentList) {
            str.append("\n");
            str.append(" --> ");
            str.append(element.isStatus() ? "☒" : "☐");
            str.append("-");
            str.append(stringMax(element.getTask()));
//            str.append("\n");
        }
        return str.toString();
    }

    public static String stringMax(String stringExtra) {
        int imax = 60;
        return stringExtra.length() > imax ? stringExtra.substring(0, imax) : stringExtra;
    }

    public boolean isPostIt() {
        return isPostIt;
    }

    public void setPostIt(boolean postIt) {
        isPostIt = postIt;
    }

    public Integer getHierarchicalRoot() {
        return hierarchicalRoot;
    }

    public void setHierarchicalRoot(int hierarchicalRoot) {
        this.hierarchicalRoot = hierarchicalRoot;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<TaskModel> getParentList() {
        return parentList;
    }

    public void setParentList(List<TaskModel> parentList) {
        this.parentList = parentList;
    }

    private void setCheckable() {
        checkable = true;
        for (TaskModel element : childList) {
            checkable = checkable && element.isStatus();
        }
    }

    @Override
    public String toString() {
        return "ToDoModel{" +
                "id=" + getId() +
                ", isProject=" + isProject() +
                ", status=" + isStatus() +
                ", task='" + getTask() + '\'' +
                ", backgroundColor=" + getBackgroundColor() +
                ", childList=" + childList +
                ", parentList=" + parentList +
                ", hierarchicalRoot=" + hierarchicalRoot +
                ", hierarchicalRank=" + hierarchicalRank +
                ", hierarchicalLevel=" + hierarchicalLevel +
                ", hierarchicalRootNbSubtask=" + hierarchicalRootNbSubtask +
                '}';
    }

    public List<TaskModel> getChildList() {
        return childList;
    }

    public void setChildList(List<TaskModel> childList) {
        this.childList = childList;
        setCheckable();
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
        for (TaskModel element : childList) {
            if (element.getId() == intExtra) {
                return true;
            }
        }
        return false;
    }

    public boolean isProject() {
        return isProject;
    }

    public void setProject(boolean project) {
        isProject = project;
    }

    public boolean isRoot() {
        return hierarchicalRoot == getId();
    }

    public int getHierarchicalRank() {
        return hierarchicalRank;
    }

    public void setHierarchicalRank(int hierarchicalRank) {
        this.hierarchicalRank = hierarchicalRank;
    }

    public int getHierarchicalLevel() {
        return hierarchicalLevel;
    }

    public void setHierarchicalLevel(int hierarchicalLevel) {
        this.hierarchicalLevel = hierarchicalLevel;
    }

    public int getHierarchicalRootNbSubtask() {
        return hierarchicalRootNbSubtask;
    }

    public void setHierarchicalRootNbSubtask(int hierarchicalRootNbSubtask) {
        this.hierarchicalRootNbSubtask = hierarchicalRootNbSubtask;
    }

    public boolean isCheckable() {
        return checkable;
    }

    public boolean isHierarchicalRoot(int expandInOnlyRootView) {
        return hierarchicalRoot == expandInOnlyRootView;
    }
}
