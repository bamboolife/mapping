package com.project.mapping.tree;

import com.project.mapping.tree.model.NodeModel;

import java.util.LinkedList;

public class TreeUtils {
    private static TreeUtils treeUtils;

    private TreeUtils() {
    }

    public static TreeUtils getInstance() {
        if (treeUtils == null) {
            treeUtils = new TreeUtils();
        }
        return treeUtils;
    }

    /**
     * 获取当前级别的位置
     *
     * @param nodeM
     * @return
     */
    public int focusIndex(NodeModel<String> nodeM) {
        if (nodeM == null || nodeM.getParentNode() == null) {
            return -1;
        }
        LinkedList<NodeModel<String>> linked = nodeM.getParentNode().getChildNodes();
        for (NodeModel<String> node : linked) {
            if (node == nodeM) {
                return linked.indexOf(node);
            }
        }
        return 0;
    }

    public NodeModel<String> cloneData(NodeModel<String> nodeM){
        System.out.println("value---"+nodeM.strContent);
        NodeModel<String> temp = new NodeModel<>(nodeM.strContent);
        return getInfo(nodeM,temp);
    }

    private NodeModel<String> getInfo(NodeModel<String> nodeM,NodeModel<String> temp){
        System.out.println("value---"+nodeM.strContent);
        for(int i = 0;i<nodeM.getChildNodes().size();i++){
            NodeModel<String> c = createNode(temp,nodeM.getChildNodes().get(i).strContent);
            getInfo(nodeM.getChildNodes().get(i),c);
        }
        return temp;
    }

    private NodeModel<String> createNode(NodeModel<String> nP,String value){
        NodeModel<String> createN = new NodeModel<>(value);
        nP.getChildNodes().add(createN);
        return createN;
    }

    //粘贴复制的节点
    public void addNode(TreeView tv,NodeModel<String> parent,NodeModel<String> child){
        tv.addSubNode(parent,child);
        int size = child.getChildNodes().size();
        for(int i = 0;i<size;i++){
            if(size>0){
                addNode(tv,child,child.getChildNodes().get(i));
            }
        }
    }
}
