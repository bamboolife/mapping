package com.project.mapping.util;

import android.graphics.Color;

import com.project.mapping.tree.TreeUtils;
import com.project.mapping.tree.model.NodeModel;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by lin.woo on 2020/3/15.
 */
public class ColorHelper {
    public static Integer[] iColor;
    private static ColorHelper colorHelper;

    private ColorHelper() {
    }

    public static ColorHelper getInstance() {
        if (colorHelper == null || iColor == null) {
            colorHelper = new ColorHelper();
            initColor();
        }
        return colorHelper;
    }

    private static void initColor() {
        iColor = new Integer[7];
        iColor[0] = Color.rgb(226, 37, 41);
        iColor[1] = Color.rgb(244, 116, 21);
        iColor[2] = Color.rgb(227, 166, 2);
        iColor[3] = Color.rgb(35, 186, 149);
        iColor[4] = Color.rgb(23, 157, 168);
        iColor[5] = Color.rgb(0, 128, 255);
        iColor[6] = Color.rgb(225, 0, 225);
    }

    public int getRandomColor() {
        int r = new Random().nextInt(7);
        return iColor[r];
    }

    /**
     * @param node
     * @return
     */
    public int getRandomColor(NodeModel<String> node, boolean sub) {
        LinkedList<NodeModel<String>> linked;
        if (sub) {
            linked = node.getChildNodes();
            if(linked.isEmpty()){
                return getRandomColor();
            }
            NodeModel<String> temp = linked.getLast();
            while (true) {
                int r = new Random().nextInt(7);
                if (temp.lineColor != iColor[r]) {
                    return iColor[r];
                }
                System.out.println("ColorHelper getRandomColor sub : " + r);
            }
        }
        linked = node.getParentNode().getChildNodes();
        int index = TreeUtils.getInstance().focusIndex(node);
        int color1 = -1;
        int color2 = -1;
        int color = node.lineColor;
        if (index != 0) {
            color1 = linked.get(index).lineColor;
        }

        if (index != linked.size() - 1) {
            color2 = linked.get(index + 1).lineColor;
        }

        while (true) {
            int r = new Random().nextInt(7);
            if (color != iColor[r] && color1 != iColor[r] && color2 != iColor[r]) {
                return iColor[r];
            }
            System.out.println("ColorHelper getRandomColor : " + r);
        }
    }
}
