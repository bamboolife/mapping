package com.project.mapping.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mapping.MainActivity;
import com.project.mapping.R;
import com.project.mapping.bean.FileTypeBean;
import com.project.mapping.constant.Constant;
import com.project.mapping.util.DensityUtils;
import com.project.mapping.util.FileBlock;
import com.project.mapping.util.FileUtils;
import com.project.mapping.util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileListFragment extends BaseFragment implements View.OnClickListener {

    private RecyclerView mRvList;
    private ImageView mBtnAdd;
    private PopupWindow mDirPopup, mFilePopup;


    private static final String SD_PATH = "/sdcard/mapping/";
    private FileAdapter fileAdapter;
    private EditText etCreate;
    private int mPosition;
    private InputMethodManager inputMethodManager;
    private int onCopy = -1;
    private String onFolder = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("全部文档");
        readLocFile(null);
        initView(view);
    }

    private List<FileTypeBean> allFiles = new ArrayList<>();

    @SuppressLint("NewApi")
    private void readLocFile(String path) {
        List<FileTypeBean> data = FileBlock.getAllFiles(path);
        if (data == null) {
            return;
        }
        allFiles.clear();
        List<FileTypeBean> folderList = new ArrayList<>();
        for (FileTypeBean datum : data) {
            if (datum.isFolder) {
                folderList.add(datum);
            } else {
                allFiles.add(datum);
            }
        }
        allFiles.addAll(0, folderList);
    }

    private void initView(View view) {
        inputMethodManager = (InputMethodManager) getActivity().getApplicationContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        mRvList = view.findViewById(R.id.rv_list);
        mBtnAdd = view.findViewById(R.id.tb_right_btn);
        Toolbar mTitle = view.findViewById(R.id.tb_head);

        mBtnAdd.setVisibility(View.VISIBLE);
        mBtnAdd.setOnClickListener(this);
        fileAdapter = new FileAdapter();
        mRvList.setAdapter(fileAdapter);
        mRvList.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        fileAdapter.setFileClickListener(new FileAdapterClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void setDetailedListener(int position) {
                View view = View.inflate(getActivity(), R.layout.pop_file_details, null);
                initBottomPop(view);
                initBottomPopView(view, position);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void setItemListener(int position) {
                if (allFiles.get(position).isFolder) {
                    onFolder = allFiles.get(position).name;
                    setTitle(onFolder);
                    readLocFile(onFolder);
                    fileAdapter.notifyDataSetChanged();
                } else {

                    ((MainActivity) getActivity()).read(allFiles.get(position).fullPath());
                    back();


                }
            }
        });

        ((Toolbar) view.findViewById(R.id.tb_head)).setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onFolder != null) {
                    onFolder = null;
                    readLocFile(null);
                    fileAdapter.notifyDataSetChanged();
                    setTitle("全部文档");

                } else {
                    back();
                }
            }
        });
    }

    private void initBottomPop(View view) {
        mFilePopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFilePopup.setBackgroundDrawable(new BitmapDrawable());
        mFilePopup.setFocusable(true);
        mFilePopup.setOutsideTouchable(true);
        mFilePopup.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        popOutShadow(0.7f);
        mFilePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popOutShadow(1.0f);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initBottomPopView(View view, int position) {
        mPosition = position;
        boolean isFolder = allFiles.get(position).isFolder;
        TextView copy = view.findViewById(R.id.tv_copy);
        TextView delete = view.findViewById(R.id.tv_delete);
        copy.setVisibility(isFolder || allFiles.stream().
                filter((FileTypeBean fi) -> fi.isFolder).
                collect(Collectors.toList()).size() == 0 ? View.GONE : View.VISIBLE);

        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileUtils.deleteFiles(allFiles.get(mPosition).fullPath());
                readLocFile(null);
                fileAdapter.notifyDataSetChanged();
                popDismiss();
            }
        });
        view.findViewById(R.id.tv_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
                showDirPop(1);
            }
        });
        view.findViewById(R.id.tv_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCopy = mPosition;
                fileAdapter.notifyDataSetChanged();
                Log.e("onCopy", "" + onCopy);
                ToastUtil.showToast("选择文件夹", getActivity());
                popDismiss();
            }
        });
    }

    private void showDirPop(final int titleType) {
        View view = View.inflate(getActivity(), R.layout.pop_create_dir, null);
        TextView tvCreate = view.findViewById(R.id.tv_create);
        etCreate = view.findViewById(R.id.et_create);
        if (titleType == 0) {
            tvCreate.setText("新建文件夹");
            etCreate.setHint("未命名");
        } else {
            etCreate.setHint("输入文件名");
            tvCreate.setText("文件重命名");
        }
        mDirPopup = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mDirPopup.setBackgroundDrawable(new BitmapDrawable());
        mDirPopup.setFocusable(true);
        mDirPopup.setOutsideTouchable(true);
        mDirPopup.showAtLocation(view, Gravity.CENTER, 0, 0);
        popOutShadow(0.7f);
        mDirPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                popOutShadow(1.0f);
            }
        });
        view.findViewById(R.id.btn_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etCreate.getText().toString().trim();
                if (titleType == 0) {
                    if (!TextUtils.isEmpty(content)) {
                        String createPath = Constant.FILEPATH + File.separator + content;
                        File file = new File(createPath);
                        if (!file.exists()) {
                            file.mkdirs();
                            ToastUtil.showToast("文件夹创建成功", getActivity());
                        } else {
                            ToastUtil.showToast("请不要重复创建文件夹", getActivity());
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(content)) {
                        if (allFiles.get(mPosition).isFolder) {
                            rename(content, File.separator, content);
                        } else {
                            rename(content, content, ".mapping");
                        }
                    }
                }
                readLocFile(null);
                fileAdapter.notifyDataSetChanged();
                popDismiss();
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
            }
        });
    }

    private void rename(String content, String separator, String content2) {
        for (int i = 0; i < allFiles.size(); i++) {
            if (allFiles.get(i).name.equals(content)) {
                ToastUtil.showToast("有相同名字存在，无法重命名", getActivity());
                return;
            } else if (i == mPosition) {
                Log.d("==file1==", allFiles.get(mPosition).path);
                Log.d("==file2==", Constant.FILEPATH + separator + content2);
                FileUtils.renameFile(allFiles.get(mPosition).fullPath(),
                        Constant.FILEPATH + separator + content2);
            }
        }
    }

    private void popDismiss() {
        if (mDirPopup != null) {
            inputMethodManager.hideSoftInputFromWindow(etCreate.getWindowToken(), 0);
            mDirPopup.dismiss();
        }
        if (mFilePopup != null) {
            mFilePopup.dismiss();
        }
    }

    /**
     * 让popupwindow以外区域阴影显示
     *
     * @param alpha
     */
    private void popOutShadow(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0。
        getActivity().getWindow().setAttributes(lp);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tb_right_btn:
                showDirPop(0);
                break;
            default:

                break;
        }
    }

    private class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
        private FileAdapterClickListener mFileClickListener;
        private List<FileTypeBean> files;

        public void setFileClickListener(FileAdapterClickListener myClickListener) {
            this.mFileClickListener = myClickListener;
        }

        @NonNull
        @Override
        public FileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_file, viewGroup, false);
            FileViewHolder fileViewHolder = new FileViewHolder(view);
            return fileViewHolder;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void refreshData() {
            files = onCopy == -1
                    ? allFiles
                    : allFiles.stream().filter((FileTypeBean fi) -> fi.isFolder).collect(Collectors.toList());
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHolder holder, final int position) {
            TextView tvFileName = holder.itemView.findViewById(R.id.tv_file_name);
            TextView tvFileTime = holder.itemView.findViewById(R.id.tv_file_time);
            ImageView ivFileType = holder.itemView.findViewById(R.id.iv_file_type);
            ImageView btnDetailed = holder.itemView.findViewById(R.id.btn_detailed);
            RelativeLayout rlItem = holder.itemView.findViewById(R.id.rl_item);

            if (files.get(position).isFolder) {
                ivFileType.setImageResource(R.mipmap.folder);
            } else {
                ivFileType.setImageResource(R.mipmap.file);
            }
            tvFileName.setText(files.get(position).name);
            tvFileTime.setText(files.get(position).data);
            btnDetailed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mFileClickListener != null) {
                        mFileClickListener.setDetailedListener(position);
                    }
                }
            });
            rlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onCopy != -1) {
                        String src = allFiles.get(onCopy).fullPath();
                        String dest = allFiles.get(position).path + File.separator + allFiles.get(onCopy).fileName();
                        new File(src).renameTo(new File(dest));

                        ToastUtil.showToast("导图已转移", getActivity());
                        readLocFile(null);
                        onCopy = -1;
                        fileAdapter.notifyDataSetChanged();

                    } else if (mFileClickListener != null) {
                        mFileClickListener.setItemListener(position);
                    }
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public int getItemCount() {
            refreshData();
            return files.size();
        }

        class FileViewHolder extends RecyclerView.ViewHolder {
            public FileViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }

    }

    private interface FileAdapterClickListener {

        void setDetailedListener(int position);

        void setItemListener(int position);
    }

}
