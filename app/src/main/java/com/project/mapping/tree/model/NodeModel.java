package com.project.mapping.tree.model;

import android.widget.EditText;

import java.io.Serializable;
import java.util.LinkedList;

public class NodeModel<T> implements Serializable, Cloneable {


    /**
     * the parent node,if root node parent node=null;
     */
    public NodeModel<T> parentNode;

    /**
     * the data value
     */
    public T value;

    /**
     * have the child nodes
     */
    public LinkedList<NodeModel<T>> childNodes;

    /**
     * focus tag for the tree add nodes
     */
    public transient boolean focus = false;

    /**
     * index of the tree floor
     */
    public int floor;

    public boolean hidden = false;

    public int lineColor;
    public int leftLineColor;

    public String strContent;
    //层级
    public int level;

    public float nodeW = 0, nodeH = 0, boxW = 0, boxH = 0;

	public NodeModel(T value) {
        this.value = value;
        strContent = (String) value;
        this.childNodes = new LinkedList<>();

        this.focus = false;
        this.parentNode = null;
    }

    public NodeModel<T> getParentNode() {
        return parentNode;
    }

    public void setParentNode(NodeModel<T> parentNode) {
        this.parentNode = parentNode;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public LinkedList<NodeModel<T>> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(LinkedList<NodeModel<T>> childNodes) {
        this.childNodes = childNodes;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

}
