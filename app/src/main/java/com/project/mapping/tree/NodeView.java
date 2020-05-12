package com.project.mapping.tree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.mapping.MainActivity;
import com.project.mapping.R;
import com.project.mapping.tree.model.NodeModel;
import com.project.mapping.util.DensityUtils;

import androidx.annotation.Nullable;


@SuppressLint("AppCompatCustomView")
//public class NodeView extends EditText {
//
//    public NodeModel<String> treeNode = null;
//
//    public NodeView(Context context) {
//        this(context, null, 0);
//    }
//
//    public NodeView(Context context, AttributeSet attrs) {
//        this(context, attrs, 0);
//    }
//
//    public NodeView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//
//        setTextColor(Color.WHITE);
//        setPadding(12, 10, 12, 10);
//
//        Drawable drawable = context.getResources().getDrawable(R.drawable.node_view_bg);
//        setBackgroundDrawable(drawable);
//    }
//
//    public NodeModel<String> getTreeNode() {
//        return treeNode;
//    }
//
//    public void setTreeNode(NodeModel<String> treeNode) {
//        this.treeNode = treeNode;
//        setSelected(treeNode.isFocus());
//        setText(treeNode.getValue());
//        setHint("              ");
//    }
//
//}
public class NodeView extends FrameLayout {
    private View v;
    public NodeModel<String> treeNode = null;
    public EditText et;
    private View line,line_2;
    private ImageView tvAdd;
    private final String TAG = "NodeView";

    public NodeView(Context context) {
        this(context, null, 0);
    }

    public NodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        v = LayoutInflater.from(getContext()).inflate(R.layout.item_node, this, true);
        et = v.findViewById(R.id.et_title);
        line = v.findViewById(R.id.line);
        line_2 = v.findViewById(R.id.line_2);
        tvAdd = v.findViewById(R.id.tv_add);
        setLayoutParams(new ViewGroup.LayoutParams(-2, -2));

        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (treeNode != null) {
                    treeNode.strContent = s.toString();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((MainActivity)getContext()).copyNodes(treeNode);
                return true;
            }
        });
        tvAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getContext()).saveNodes(treeNode);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public NodeModel<String> getTreeNode() {
        return treeNode;
    }

    public void setTreeNode(NodeModel<String> treeNode) {
        this.treeNode = treeNode;
        setSelected(treeNode.isFocus());
//        et.setFocusableInTouchMode(true);
        et.requestFocus();
        et.setText(treeNode.strContent);
        et.setHint("              ");
        if (treeNode.lineColor != 0) {
            line.setBackgroundColor(treeNode.lineColor);
            line_2.setBackgroundColor(treeNode.lineColor);
        }
        setShowLevel(treeNode.level);
    }

    private void setShowLevel(int level) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) line.getLayoutParams();
        switch (level) {
            case TreeConstant.LEVEL_0:
                et.setTextSize(22);
                lp.height = DensityUtils.dp2px(getContext(), 6);
                break;
            case TreeConstant.LEVEL_1:
                et.setTextSize(15);
                lp.height = DensityUtils.dp2px(getContext(), 2.5f);
                break;
            case TreeConstant.LEVEL_2:
                et.setTextSize(12);
                lp.height = DensityUtils.dp2px(getContext(), 1.33f);
                break;
            case TreeConstant.LEVEL_3:
                et.setTextSize(12);
                lp.height = DensityUtils.dp2px(getContext(), 0.75f);
                break;
            default:
                et.setTextSize(12);
                lp.height = DensityUtils.dp2px(getContext(), 0.75f);
        }
        line.setLayoutParams(lp);
    }


    public void setEtFocusChangeListener(final IFocusListener iFocusListener) {
        et.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    iFocusListener.focusChange(hasFocus, treeNode);
                }
            }
        });
    }

    public interface IFocusListener {
        void focusChange(boolean b, NodeModel<String> nm);
    }
}