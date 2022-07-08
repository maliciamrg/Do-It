package com.malicia.mrg.utils;

import android.util.Log;
import com.malicia.mrg.MainActivity;
import com.malicia.mrg.Model.ParentModel;

import java.util.*;

public class BuildHierarchyTree {

    private final Map<Integer, List<ParentModel>> todoMapTree = new LinkedHashMap<>(); //stores (id, employee) pair


    //Build tree, Recursion, Time O(n), Space O(h), n is number of employees, h is levels of hierarchy tree
    public BuildHierarchyTree(List<ParentModel> linkLists) {
        todoMapTree.clear();
        //Read data and build map, Iteration, Time O(n), Space O(n), n is number of employees
        for (ParentModel linkEle : linkLists) {
            List<ParentModel> linkEleList = new ArrayList<>();
            if (todoMapTree.containsKey(linkEle.getTaskId())){
                linkEleList = todoMapTree.get(linkEle.getTaskId());
            }
            linkEleList.add(linkEle);
            todoMapTree.put(linkEle.getTaskId(), linkEleList);
        }
    }

    public void buildHierarchyTree(int id) {
        buildHierarchyTree(todoMapTree.get(id).get(0), 0);
    }

    public void buildHierarchyTree(ParentModel root, int deep) {
        deep ++;
        ParentModel todoMapElement = root;
        List<ParentModel> subs = getSubsById(todoMapElement.getTaskId());
        todoMapElement.setChildList(subs);
        if (subs.size() == 0 || deep > 9)
            return;
        for (ParentModel em : subs)
            buildHierarchyTree(em, deep);
    }

    //Get subordinates list by given id, Time O(n), Space O(k) ~ O(n), k is number of subordinates
    private List<ParentModel> getSubsById(int parentId) {
        final List<ParentModel> subs = new ArrayList<ParentModel>();

        for (List<ParentModel> lstEle : todoMapTree.values()) {
            for (ParentModel todoMapEle : lstEle) {
                if (todoMapEle.getTaskParentId() == parentId)
                    subs.add(todoMapEle);
            }
        }
        return subs;
    }

    //Print tree, Recursion, Time O(n), Space O(h)
    public List<HierarchyData> printHierarchyTree(int id, int level) {
        List<HierarchyData> hashRetour = new ArrayList<>();
        StringBuilder str = new StringBuilder();
        str.append("<>\n" );
        printHierarchyTree(todoMapTree.get(id).get(0), level,hashRetour,str);
        Log.d(MainActivity.LOG_TAG,str.toString());
        return hashRetour;
    }

    public void printHierarchyTree(ParentModel root, int level, List<HierarchyData> hashRetour,StringBuilder str) {
        if (level>9) return;
        for (int i = 0; i < level; i++) {
            str.append("+--");
        }

        hashRetour.add(new HierarchyData(root.getTaskId(),level));

        str.append(root.getTaskId()+ " -- " + root.getTaskName());
        str.append("\n" );

        List<ParentModel> subs = root.getChildList();
        for (ParentModel em : subs)
            printHierarchyTree(em, level + 1, hashRetour,str);
    }

}