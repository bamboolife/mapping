package com.project.mapping.tree;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.project.mapping.tree.model.ForTreeItem;
import com.project.mapping.tree.model.NodeModel;
import com.project.mapping.tree.model.TreeModel;
import com.project.mapping.util.DensityUtils;

import java.util.LinkedList;

public class TreeLayoutManager {
	private TreeView tree;

	public TreeLayoutManager(TreeView tree) {
		this.tree   = tree;
	}

	public void onTreeLayout() {
		final TreeModel<String> mTreeModel = tree.getTreeModel();

		if (mTreeModel != null) {
			Log.e("onTreeLayout", "...");
			mTreeModel.addForTreeItem((ForTreeItem<NodeModel<String>>) (msg, next) -> refreshView());
			refreshView();
		}
	}

	private void refreshView() {
		final int vW = tree.getMeasuredWidth(),  vH = tree.getMeasuredHeight();
		final TreeModel<String> mTreeModel = tree.getTreeModel();

		parseLocation(tree, mTreeModel.getRootNode());
		printRect(mTreeModel.getRootNode(), 0, 0);

		final ViewGroup.LayoutParams layoutParams = tree.getLayoutParams();
		if (  (  layoutParams.width != mTreeModel.getRootNode().boxW
				|| layoutParams.height != mTreeModel.getRootNode().boxH)
				&& ( vW < mTreeModel.getRootNode().boxW || vH < mTreeModel.getRootNode().boxH)
		) {
			layoutParams.width  = -2;
			layoutParams.height = -2;
			tree.setLayoutParams(layoutParams);
		}
		tree.setNodeFocus(tree.findNodeViewFromNodeModel(tree.getCurrentFocusNode()));
		Log.e("refreshView", tree.getLeft() + "  " + tree.getMeasuredWidth() + "  " + tree.getMeasuredHeight());

			Rect rect		= new Rect();
			tree.getWindowVisibleDisplayFrame(rect);
			int hei			= rect.bottom - rect.top;
			int wid			= rect.right  - rect.left;

			if (mTreeModel.getRootNode().boxH < hei) {
				tree.setTop((int) ((hei - mTreeModel.getRootNode().boxH) / 2));
			}
			if (mTreeModel.getRootNode().boxW < wid) {
				tree.setLeft((int) ((wid - mTreeModel.getRootNode().boxW) / 2));
			}
	}

	private void parseLocation(TreeView tree, NodeModel<String> parent) {
		float dp24		= tree.scaleDp2px(24);
		View nodeView	= tree.findNodeViewFromNodeModel(parent);
		float	w		= parent.nodeW = (int) (tree.scale * nodeView.getMeasuredWidth()),
				h		= parent.nodeH = (int) (tree.scale * nodeView.getMeasuredHeight());
		float childsW = 0, childsH = 0;

		LinkedList<NodeModel<String>> childNodes = parent.childNodes;
		for (NodeModel<String> child : childNodes) {
			parseLocation(tree, child);
			childsW  =  Math.max(childsW, child.boxW);
			childsH  += child.boxH;
		}
		parent.boxW		= childsW + w + dp24;
		parent.boxH		= Math.max(childsH, h);
	}

	private void printRect(NodeModel<String> parent, int left, int top) {
		float dp24		= tree.scaleDp2px(24);
		float scale		= tree.scale;
		View nodeView	= tree.findNodeViewFromNodeModel(parent);
		float newX		= left;
		float newY		= top  + (parent.boxH - parent.nodeH) / 2;

		nodeView.layout((int)newX, (int)newY, (int)(newX + parent.nodeW), (int)(newY + parent.nodeH));
		nodeView.setScaleX(scale);
		nodeView.setScaleY(scale);

		LinkedList<NodeModel<String>> childNodes = parent.childNodes;
		for (int i = 0, lastY = top; i < childNodes.size(); i++) {
			NodeModel<String> child = childNodes.get(i);
			printRect(child, (int)(left + parent.nodeW + dp24), lastY);
			lastY += child.boxH;
		}
	}
}