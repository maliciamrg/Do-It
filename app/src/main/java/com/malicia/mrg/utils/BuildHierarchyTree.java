package com.malicia.mrg.utils;

import net.penguincoders.doit.Model.ToDoLinkModel;

import java.util.*;

public class BuildHierarchyTree {

    private final Map<Integer, ToDoLinkModel> todoMapTree = new LinkedHashMap<>(); //stores (id, employee) pair


    //Build tree, Recursion, Time O(n), Space O(h), n is number of employees, h is levels of hierarchy tree
    public BuildHierarchyTree(List<ToDoLinkModel> linkLists) {
        todoMapTree.clear();
        //Read data and build map, Iteration, Time O(n), Space O(n), n is number of employees
        for (ToDoLinkModel linkEle : linkLists) {
            todoMapTree.put(linkEle.getTaskId(), linkEle);
        }
    }

    public void buildHierarchyTree(int id) {
        buildHierarchyTree(todoMapTree.get(id));
    }

    public void buildHierarchyTree(ToDoLinkModel root) {
        ToDoLinkModel todoMapElement = root;
        List<ToDoLinkModel> subs = getSubsById(todoMapElement.getTaskId());
        todoMapElement.setChildList(subs);
        if (subs.size() == 0)
            return;
        for (ToDoLinkModel em : subs)
            buildHierarchyTree(em);
    }

    //Get subordinates list by given id, Time O(n), Space O(k) ~ O(n), k is number of subordinates
    private List<ToDoLinkModel> getSubsById(int parentId) {
        List<ToDoLinkModel> subs = new ArrayList<ToDoLinkModel>();
        for (ToDoLinkModel todoMapEle : todoMapTree.values()) {
            if (todoMapEle.getTaskParentId() == parentId)
                subs.add(todoMapEle);
        }
        return subs;
    }

    //Print tree, Recursion, Time O(n), Space O(h)
    public Map<Integer, Integer> printHierarchyTree(int id, int level) {
        Map<Integer, Integer> hashRetour = new LinkedHashMap<>();
        printHierarchyTree(todoMapTree.get(id), level,hashRetour);
        return hashRetour;
    }

    public void printHierarchyTree(ToDoLinkModel root, int level, Map<Integer,Integer> hashRetour) {
//        for (int i = 0; i < level; i++) {
//            str.append("+--");
//        }

        hashRetour.put(root.getTaskId(),level);

//        str.append(root.getTaskName());
//        str.append("\n" );

        List<ToDoLinkModel> subs = root.getChildList();
        for (ToDoLinkModel em : subs)
            printHierarchyTree(em, level + 1, hashRetour);
    }

}