package com.project.mapping.tree;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.nineoldandroids.animation.ObjectAnimator;
import com.project.mapping.R;
import com.project.mapping.tree.line.EaseCubicInterpolator;
import com.project.mapping.tree.model.NodeModel;
import com.project.mapping.tree.model.TreeModel;
import com.project.mapping.util.ColorHelper;
import com.project.mapping.util.LooperFlag;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class TreeView extends ViewGroup {

    private static final String TAG = "TreeView";
    public float scale = 1;
    private Context ctx;

    //树形结构
    public TreeModel<String> mTreeModel;
    public TreeLayoutManager mTreeLayoutManager;

    //点击事件
    private TreeViewItemClick mTreeViewItemClick;
    //长按
    private TreeViewItemLongClick mTreeViewItemLongClick;

    //最近点击的item
    private NodeModel<String> mCurrentFocus;
    //连接线上下偏移

    //触摸循环事件，放大，等同，缩小
    private Integer[] looperBody = new Integer[]{0, 1, 0, -1};
    private LooperFlag<Integer> mLooperFlag;

//    private GestureDetector mGestureDetector;

    //竖线加粗
    private Paint sPaint;
    private Path sPath;

    private Paint mPaint;
    private Path mPath;

    public TreeView(Context context) {
        this(context, null, 0);
    }

    public TreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setClipChildren(false);
        setClipToPadding(false);

        sPaint = new Paint();
        sPaint.setAntiAlias(true);
        sPath = new Path();
        sPath.reset();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mPath = new Path();
        mPath.reset();

        setLayoutParams(new LayoutParams(-1, -1));
        ctx = context;

        mLooperFlag = new LooperFlag<>(looperBody, new LooperFlag.LooperListener<Integer>() {
            @Override
            public void onLooper(Integer item) {
                looperBusiness(item);
            }
        });
        setTreeLayoutManager(new TreeLayoutManager(this));
	}

    public void initMapping(TreeModel<String> data) {
        if (null == data) {
            NodeModel<String> nm	= new NodeModel<>("");
            nm.level				= TreeConstant.LEVEL_0;
            nm.lineColor			= Color.rgb(117, 148, 230);
            data	= new TreeModel<>(nm);
        }
        this.setTreeModel(data);
//        KeyboardUtils.showInput(this, nv.et);
    }

    public void setScale(float newScale) {
        if      (newScale < .2) scale = .2f;
        else if (newScale > 1000) scale = 1000;
        else                    scale = newScale;

        if (mCurrentFocus != null)
            findNodeViewFromNodeModel(mCurrentFocus).clearFocus();
            mCurrentFocus   = null;
        mTreeLayoutManager.onTreeLayout();
    }

    int movingleft, movingtop;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int size = getChildCount();
        for (int i = 0; i < size; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }
        movingtop     = this.getTop();
        movingleft    = this.getLeft();
        Log.e(TAG + " onMeasure", this.getLeft() + "  ");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG + " onLayout", this.getLeft() + "  " + changed + "  " + (mTreeLayoutManager != null) + "  " + (mTreeModel != null));
        if (mTreeLayoutManager != null && mTreeModel != null) {
            setLeft(movingleft);
            setTop(movingtop);
            mTreeLayoutManager.onTreeLayout();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        if (mTreeModel != null) {
            Log.e(TAG, "dispatchDraw");
            drawTreeLine(canvas, mTreeModel.getRootNode());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void looperBusiness(Integer item) {
        EaseCubicInterpolator easeCubicInterpolator = new EaseCubicInterpolator(0.39f, 0.13f, 0.33f, 1f);
        ObjectAnimator animator1;
        ObjectAnimator animator2;

        if (item == -1) {
            animator1 = ObjectAnimator.ofFloat(TreeView.this, "scaleX", getScaleX(), 0.3f)
                    .setDuration(500);
            animator2 = ObjectAnimator.ofFloat(TreeView.this, "scaleY", getScaleX(), 0.3f)
                    .setDuration(500);

        } else if (item == 0) {
            animator1 = ObjectAnimator.ofFloat(TreeView.this, "scaleX", getScaleX(), 1.0f)
                    .setDuration(500);
            animator2 = ObjectAnimator.ofFloat(TreeView.this, "scaleY", getScaleX(), 1.0f)
                    .setDuration(500);
        } else {
            animator1 = ObjectAnimator.ofFloat(TreeView.this, "scaleX", getScaleX(), 1.6f)
                    .setDuration(500);
            animator2 = ObjectAnimator.ofFloat(TreeView.this, "scaleY", getScaleX(), 1.6f)
                    .setDuration(500);
        }

        animator1.setInterpolator(easeCubicInterpolator);
        animator2.setInterpolator(easeCubicInterpolator);
        animator1.start();
        animator2.start();
    }

    /**
     * 绘制树形的连线
     *
     * @param canvas
     * @param root
     */
    private void drawTreeLine(Canvas canvas, NodeModel<String> root) {
        NodeView fatherView = (NodeView) findNodeViewFromNodeModel(root);
        if (fatherView != null) {
            LinkedList<NodeModel<String>> childNodes = root.getChildNodes();
            for (NodeModel<String> node : childNodes) {
                //连线
                drawLineToView(canvas, fatherView, findNodeViewFromNodeModel(node), node);
                //递归
                drawTreeLine(canvas, node);
            }
        }
    }

    public float scaleDp2px(float dp) {
        return scale * TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, ctx.getResources().getDisplayMetrics());
    }

    /**
     * 绘制两个View直接的连线
     *
     * @param canvas
     * @param from
     * @param to
     */
    private void drawLineToView(Canvas canvas, View from, View to, NodeModel<String> node) {
        if (to.getVisibility() == GONE) {
            return;
        }

        mPaint.setStyle(Paint.Style.STROKE);
        sPaint.setStyle(Paint.Style.STROKE);
        sPaint.setStrokeCap(Paint.Cap.ROUND);

        float width = 0.75f;

        mPaint.setStrokeWidth(scaleDp2px(width));
        sPaint.setStrokeWidth(scaleDp2px(5));
        //TODO
        if (node.leftLineColor == 0) {
            mPaint.setColor(ctx.getResources().getColor(R.color.chelsea_cucumber));
        } else {
            mPaint.setColor(node.leftLineColor);
            sPaint.setColor(node.leftLineColor);
        }

//        int top = from.getTop();
//        int formY = top + from.getMeasuredHeight() / 2;
//        int formX = from.getRight();
//
//        int top1 = to.getTop();
//        int toY = top1 + to.getMeasuredHeight() / 2;
//        int toX = to.getLeft();
//
//        mPath.reset();
//        mPath.moveTo(formX, formY);
//        mPath.quadTo(toX - scaleDp2px(5), toY, toX, toY);
        setLine(from, to, canvas);

        canvas.drawPath(mPath, mPaint);
    }

    //TODO
    private void setLine(View from, View to, Canvas c) {
        float padB  = scaleDp2px(8);
        float formX = from.getRight();
        float toX   = to.getLeft();
        float formY = (from.getBottom() - padB);
        float toY   = (to  .getBottom() - padB);
        float dLine = scaleDp2px(4);

        mPath.reset();
        mPath.moveTo(formX, formY);
        mPath.lineTo(formX + dLine, formY);

        if (formY == toY) {
            mPath.lineTo(toX, toY);
//            mPath.quadTo(toX, toY, toX, toY);
        } else if (formY < toY) {
            mPath.lineTo(formX + dLine, toY - scaleDp2px(15));
//            RectF mRectF = new RectF(formX, toY- scaleDp2px(15), formX+scaleDp2px(15), toY);
            RectF mRectF = new RectF(formX + dLine, toY - scaleDp2px(15), formX + scaleDp2px(15), toY);
            mPath.arcTo(mRectF, 180, -90);
            mPath.lineTo(toX, toY);
//            mPath.quadTo(toX - scaleDp2px(5), toY, toX, toY);

            if (((NodeView) from).treeNode.level == 0) {
                sPath.reset();
                sPath.moveTo(formX + dLine, formY);
                sPath.lineTo(formX + dLine, toY - scaleDp2px(5));
                c.drawPath(sPath, sPaint);
            }
            System.out.println(String.format("formY====%s  toY====%s", formY, toY));
        } else {
            mPath.lineTo(formX + dLine, toY + scaleDp2px(15));
//            RectF mRectF = new RectF(formX, toY, formX+scaleDp2px(15), toY+ scaleDp2px(15));
            RectF mRectF = new RectF(formX + dLine, toY, formX + scaleDp2px(15), toY + scaleDp2px(15));
            mPath.arcTo(mRectF, 180, 90);
//            mPath.quadTo(toX - scaleDp2px(5), toY, toX, toY);
            mPath.lineTo(toX, toY);

            if (((NodeView) from).treeNode.level == 0) {
                sPath.reset();
                sPath.moveTo(formX + dLine, formY);
                sPath.lineTo(formX + dLine, toY + scaleDp2px(5));
                c.drawPath(sPath, sPaint);
            }
        }
    }

	public TreeModel<String> getTreeModel() {
        return mTreeModel;
    }

    public void setTreeModel(TreeModel<String> treeModel) {
        Log.e(TAG, "setTreeModel "+treeModel.getRootNode().strContent);
        mTreeModel = treeModel;

        clearAllNoteViews();

        addNoteViews();

        setCurrentSelectedNode(mTreeModel.getRootNode());
    }

    /**
     * 清除所有的NoteView
     */
    private void clearAllNoteViews() {
        int count = getChildCount();
        if (count > 0) {
            for (int i = count; i >= 0; i--) {
                View childView = getChildAt(i);
                if (childView instanceof NodeView) {
                    removeView(childView);
                }
            }
        }
    }

    /**
     * 添加所有的NoteView
     */
    public void addNoteViews() {
        if (mTreeModel != null) {
            NodeModel<String> rootNode = mTreeModel.getRootNode();
            Deque<NodeModel<String>> deque = new ArrayDeque<>();
            deque.add(rootNode);
            while (!deque.isEmpty()) {
                NodeModel<String> poll = deque.poll();

                addNodeViewToGroup(poll);

                LinkedList<NodeModel<String>> childNodes = poll.getChildNodes();
                for (NodeModel<String> ch : childNodes) {
                    deque.push(ch);
                }
            }
        }
    }

    private void addNodeViewToGroup(NodeModel<String> poll) {
        Log.w("addNodeViewToGroup","..." + poll.level);
        final NodeView nodeView = new NodeView(ctx);
        nodeView.setFocusable(true);
        nodeView.setClickable(true);
        nodeView.setSelected(false);
        nodeView.setPivotX(0);
        nodeView.setPivotY(0);
        nodeView.setTreeNode(poll);

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        nodeView.setLayoutParams(lp);
        //set the nodeclick
        nodeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                performTreeItemClick(view);
            }
        });
        nodeView.setEtFocusChangeListener(new NodeView.IFocusListener() {
            @Override
            public void focusChange(boolean b, NodeModel<String> nm) {
                mCurrentFocus = nm;
                Log.e(TAG, "focusChange: "+ b + "  " + nm.strContent);
            }
        });
        nodeView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                preformTreeItemLongClick(v);
                return true;
            }
        });
        this.addView(nodeView);
        mCurrentFocus = poll;
    }

    public void setNodeFocus(final NodeView node) {
        if (node == null) return;

        this.post(() -> {
            int w = TreeView.this.getMeasuredWidth();
            int h = TreeView.this.getMeasuredHeight();
            Log.w(TAG, "setNodeFocus" + w + "  " + h );

            if (node.getLeft() < - TreeView.this.getLeft() || node.getRight() > w - TreeView.this.getLeft()) {
                TreeView.this.setLeft((w - node.getMeasuredWidth()) / 2 - node.getLeft());
            }
            if (node.getTop()  < - TreeView.this.getTop() || node.getBottom() > h - TreeView.this.getTop()) {
                TreeView.this.setTop((h - node.getMeasuredHeight()) / 2 - node.getTop());
            }
        });
    }

    public void setTreeViewItemClick(TreeViewItemClick treeViewItemClick) {
        mTreeViewItemClick = treeViewItemClick;
    }

    public void setTreeViewItemLongClick(TreeViewItemLongClick treeViewItemLongClick) {
        mTreeViewItemLongClick = treeViewItemLongClick;
    }

    private void preformTreeItemLongClick(View v) {
        setCurrentSelectedNode(((NodeView) v).getTreeNode());

        if (mTreeViewItemLongClick != null) {
            mTreeViewItemLongClick.onLongClick(v);
        }
    }

    private void performTreeItemClick(View view) {
        setCurrentSelectedNode(((NodeView) view).getTreeNode());
        if (mTreeViewItemClick != null) {
            mTreeViewItemClick.onItemClick(view);
        }
    }

    public void setCurrentSelectedNode(final NodeModel<String> nodeModel) {
//        if (mCurrentFocus != null) {
//            mCurrentFocus.setFocus(false);
//            NodeView treeNodeView = (NodeView) findNodeViewFromNodeModel(mCurrentFocus);
//            if (treeNodeView != null) {
//                treeNodeView.setSelected(false);
//            }
//        }
//TODO
        nodeModel.setFocus(true);
        findNodeViewFromNodeModel(nodeModel).setSelected(true);
        mCurrentFocus = nodeModel;
        System.out.println("hasFocus----  11111111111");
    }

    /**
     * 设置树形结构分布管理器
     *
     * @param treeLayoutManager
     */
    public void setTreeLayoutManager(TreeLayoutManager treeLayoutManager) {
        mTreeLayoutManager = treeLayoutManager;
        Log.e(TAG, "mTreeLayoutManager is null : "+ (mTreeLayoutManager == null));
    }

    /**
     * 模型查找NodeView
     *
     * @param nodeModel
     * @return
     */
    public NodeView findNodeViewFromNodeModel(NodeModel<String> nodeModel) {
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView instanceof NodeView) {
                NodeModel<String> treeNode = ((NodeView) childView).getTreeNode();
                if (treeNode == nodeModel) {
                    return (NodeView) childView;
                }
            }
        }
        return null;
    }

    public void changeNodeValue(NodeModel<String> model, String value) {
        NodeView treeNodeView = (NodeView) findNodeViewFromNodeModel(model);
        NodeModel<String> treeNode = treeNodeView.getTreeNode();
        treeNode.setValue(value);
        treeNodeView.setTreeNode(treeNode);
    }

    public NodeModel<String> getCurrentFocusNode() {
        return mCurrentFocus;
    }

    /**
     * 添加同层节点
     *
     * @param nodeValue
     */
    public void addNode(String nodeValue) {
        addNode(nodeValue, -1);
    }

    public void addNode(String nodeValue, int index) {
        if (getCurrentFocusNode() == null || index==0) return;

        NodeModel<String> addNode = new NodeModel<>(nodeValue);
        NodeModel<String> parentNode = getCurrentFocusNode().getParentNode();
        Log.e("addNode", nodeValue + "..." + index + "  " + (parentNode != null));
        if (parentNode != null) {

            if (mTreeModel.getRootNode() != parentNode) {
                if (parentNode.level == TreeConstant.LEVEL_1) {
                    addNode.level = TreeConstant.LEVEL_2;
                }
                if (parentNode.level == TreeConstant.LEVEL_2 || parentNode.level == TreeConstant.LEVEL_3) {
                    addNode.level = TreeConstant.LEVEL_3;
                }
                addNode.lineColor = getCurrentFocusNode().lineColor;
            } else {
                addNode.level = TreeConstant.LEVEL_1;
                addNode.lineColor = ColorHelper.getInstance().getRandomColor(getCurrentFocusNode(), false);
            }
            addNode.leftLineColor = parentNode.lineColor;

            if (index < 0) {
                mTreeModel.addNode(parentNode, addNode);
            } else {
                mTreeModel.addNode(parentNode, addNode, index);
            }
            Log.i(TAG, "addNode: true");
            addNodeViewToGroup(addNode);
        }
    }

    /**
     * 添加子节点
     *
     * @param nodeValue
     */
    public void addSubNode(String nodeValue) {
        if (getCurrentFocusNode() == null) return;
        NodeModel<String> addNode = new NodeModel<>(nodeValue);

        addNode.leftLineColor = getCurrentFocusNode().lineColor;
        if (mTreeModel != null) {
            if (mTreeModel.getRootNode() != getCurrentFocusNode()) {
                if (getCurrentFocusNode().level == TreeConstant.LEVEL_1) {
                    addNode.level = TreeConstant.LEVEL_2;
                }
                if (getCurrentFocusNode().level == TreeConstant.LEVEL_2 || getCurrentFocusNode().level == TreeConstant.LEVEL_3) {
                    addNode.level = TreeConstant.LEVEL_3;
                }
                addNode.lineColor = getCurrentFocusNode().lineColor;
            } else {
                addNode.level = TreeConstant.LEVEL_1;
                addNode.lineColor = ColorHelper.getInstance().getRandomColor(getCurrentFocusNode(), true);
            }
        }

        mTreeModel.addNode(getCurrentFocusNode(), addNode);
        addNodeViewToGroup(addNode);
    }

    public void addSubNode(NodeModel<String> parent,String nodeValue) {
        NodeModel<String> addNode = new NodeModel<>(nodeValue);

        addNode.leftLineColor = parent.lineColor;
        if (mTreeModel != null) {
            if (mTreeModel.getRootNode() != parent) {
                if (parent.level == TreeConstant.LEVEL_1) {
                    addNode.level = TreeConstant.LEVEL_2;
                }
                if (parent.level == TreeConstant.LEVEL_2 || parent.level == TreeConstant.LEVEL_3) {
                    addNode.level = TreeConstant.LEVEL_3;
                }
                addNode.lineColor = parent.lineColor;
            } else {
                addNode.level = TreeConstant.LEVEL_1;
                addNode.lineColor = ColorHelper.getInstance().getRandomColor(parent, true);
            }
        }

        mTreeModel.addNode(parent, addNode);
        addNodeViewToGroup(addNode);
    }
    public void addSubNode(NodeModel<String> parent,NodeModel<String> addNode) {
        addNode.leftLineColor = parent.lineColor;
        if (mTreeModel != null) {
            if (mTreeModel.getRootNode() != parent) {
                if (parent.level == TreeConstant.LEVEL_1) {
                    addNode.level = TreeConstant.LEVEL_2;
                }
                if (parent.level == TreeConstant.LEVEL_2 || parent.level == TreeConstant.LEVEL_3) {
                    addNode.level = TreeConstant.LEVEL_3;
                }
                addNode.lineColor = parent.lineColor;
            } else {
                addNode.level = TreeConstant.LEVEL_1;
                addNode.lineColor = ColorHelper.getInstance().getRandomColor(parent, true);
            }
        }

        mTreeModel.addNode(parent, addNode);
        addNodeViewToGroup(addNode);
    }

    public void deleteNode(NodeModel<String> node) {
        if (node == null) return;

        //设置current的选择

        NodeModel<String> parentNode = node.getParentNode();
        if (parentNode != null) {
            //切断
            mTreeModel.removeNode(parentNode, node);
            setCurrentSelectedNode(parentNode);
        }

        //清理碎片
        Queue<NodeModel<String>> queue = new ArrayDeque<>();
        queue.add(node);

        while (!queue.isEmpty()) {
            NodeModel<String> poll = queue.poll();
            NodeView treeNodeView = (NodeView) findNodeViewFromNodeModel(poll);
            removeView(treeNodeView);
            for (NodeModel<String> nm : poll.getChildNodes()) {
                queue.add(nm);
            }
        }
    }

    public int getTreeNodeIndex(NodeModel<String> node) {
        return TreeUtils.getInstance().focusIndex(node);
    }
}