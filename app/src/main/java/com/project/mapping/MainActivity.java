package com.project.mapping;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.project.mapping.bean.DataBean;
import com.project.mapping.bean.FileTypeBean;
import com.project.mapping.constant.Constant;
import com.project.mapping.tree.TreeUtils;
import com.project.mapping.tree.TreeView;
import com.project.mapping.tree.model.NodeModel;
import com.project.mapping.tree.model.TreeModel;
import com.project.mapping.ui.fragment.BaseFragment;
import com.project.mapping.ui.fragment.FileListFragment;
import com.project.mapping.ui.fragment.LoginFragment;
import com.project.mapping.ui.fragment.PayFragment;
import com.project.mapping.ui.fragment.SnapPreviewFragment;
import com.project.mapping.ui.fragment.SuggestionFragment;
import com.project.mapping.util.DensityUtils;
import com.project.mapping.util.DeviceUtil;
import com.project.mapping.util.FileBlock;
import com.project.mapping.util.FileUtils;
import com.project.mapping.util.ImageUtils;
import com.project.mapping.util.RetrofitManager;
import com.project.mapping.util.SPUtil;
import com.project.mapping.util.ScreenUtils;
import com.project.mapping.util.SoftKeyBoardListener;
import com.project.mapping.util.ToastUtil;
import com.project.mapping.util.rx.Transformers;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends RxAppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener,
        EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    private final String TAG = "MainActivity";
    ImageView mIvDrawerOpen;
    NavigationView mNvMenuRight;
    DrawerLayout mIdDrawerLayout;
//    private PopupWindow mPopupWindow;
    private BottomSheetDialog mBottomDialog;
    private boolean mIsLogin;
    /**
     * 用户账号为手机号码后四位
     */
    private String mNumberLast4;
    /**
     * 付费类型  0免费版 1(9.9/年) 2.(25/永久)
     */
    private String mPayType;
    private TextView mTvUserAccount;
    private TextView mTvPayType;
    private TreeView treeV;
    private Button /*btAddSub,*/ btAddNode, btFocus, /*btnTop, btnBottom, btnDel,*/
            btnCopy, btnPaste;
    private ImageView imgTop, imgBottom, imgDel, imgAddSub;
    private LinearLayout llBottomBtn;
    private NodeModel<String> clone;
    private IWXAPI wxapi;
    boolean isBuild = false;
    private boolean isJumpFileList = false;
    private boolean isSave = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        treeV = (TreeView) ((ViewGroup) findViewById(R.id.msv)).getChildAt(0);
        treeV.initMapping(null);
        applyPermission();

//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        if (savedInstanceState != null) {
            String mapPath = savedInstanceState.getString("EditorMapPath");
            if (mapPath != null) read(mapPath);
        } else if (SPUtil.getString("EditorMapPath", null) != null) {
            read(SPUtil.getString("EditorMapPath", null));
        }
    }

    private void applyPermission() {
        String[] perms = {Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= 23) {
            if (EasyPermissions.hasPermissions(this, perms)) {
                init();
            } else {
                EasyPermissions.requestPermissions(this, "您需要打开存储空间和电话权限才可以使用app", 0, perms);
            }
        } else {
            init();
        }
    }

    private void init() {
        initData();
        initListener();
        initWeChat();
    }

    private void initWeChat() {
        wxapi = WXAPIFactory.createWXAPI(this, null);
        wxapi.registerApp(Constant.WEHCHAT_APPID);
    }

    private void initData() {
        mIsLogin = SPUtil.getBoolean(Constant.LOGIN, false);
        mNumberLast4 = SPUtil.getString(Constant.NUMBER_LAST_4, "1234");
        Map<String, String> map = new HashMap<>();
        map.put(Constant.CHANNEL_SOURCES, Constant.CHANNEL_ID);
        map.put(Constant.EQUIPMENT_ID, DeviceUtil.getDeviceId(this));
        map.put(Constant.EQUIPMENT_MODEL, DeviceUtil.getDeviceModel());
        RetrofitManager.getInstance().getService().putReportDevices(map).
                compose(Transformers.<DataBean>applySchedulers(this, ActivityEvent.DESTROY)).
                subscribe(dataBean -> Log.d("===putReportDevices===", dataBean.toString()));
    }

    private void initView() {
        btAddNode = findViewById(R.id.btn_add_node);
        btFocus = findViewById(R.id.btn_focus_mid);
        btnCopy = findViewById(R.id.btn_copy);
        btnPaste = findViewById(R.id.btn_paste);
        imgTop = findViewById(R.id.img_opt1);
        imgBottom = findViewById(R.id.img_opt2);
        imgDel = findViewById(R.id.img_opt3);
        imgAddSub = findViewById(R.id.img_opt4);

        mIvDrawerOpen = (ImageView) findViewById(R.id.iv_drawer_open);
        mIdDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        mNvMenuRight = (NavigationView) findViewById(R.id.nv_menu_right);
        View headerView = mNvMenuRight.getHeaderView(0);
        mTvUserAccount = headerView.findViewById(R.id.tv_user);
        mTvPayType = headerView.findViewById(R.id.tv_pay_type);
        headerView.findViewById(R.id.iv_user_pic).setOnClickListener(this);
        if (mIsLogin) {
            mNvMenuRight.getMenu().getItem(7).setVisible(true);
            mTvUserAccount.setVisibility(View.VISIBLE);
            mTvUserAccount.setText(mNumberLast4);
        } else {
            mNvMenuRight.getMenu().getItem(7).setVisible(false);
            mTvUserAccount.setVisibility(View.GONE);
        }

        llBottomBtn = findViewById(R.id.ll_bottom_btn);
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                llBottomBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void keyBoardHide(int height) {
                llBottomBtn.setVisibility(View.GONE);
            }
        });
        initPopView();
    }


    private void initPopView(){
        view = View.inflate(this, R.layout.pop_share, null);

        mBottomDialog=new BottomSheetDialog(this);
        mBottomDialog.setContentView(view);
        mBottomDialog.setCanceledOnTouchOutside(true);
        mBottomDialog.getBehavior().setPeekHeight((int) (ScreenUtils.getScreenHeight(MappingApplication.mContext)/2.5));

    }
    /**
     * initListener
     */
    private void initListener() {
        mIvDrawerOpen.setOnClickListener(this);
        mNvMenuRight.setNavigationItemSelectedListener(this);
        imgTop.setOnClickListener(this);
        imgBottom.setOnClickListener(this);
        imgDel.setOnClickListener(this);
        imgAddSub.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_drawer_open:
                mIdDrawerLayout.openDrawer(Gravity.RIGHT);
                mIsLogin = SPUtil.getBoolean(Constant.LOGIN, false);
                if (mIsLogin) {
                    mNumberLast4 = SPUtil.getString(Constant.NUMBER_LAST_4, "1234");
                    mNvMenuRight.getMenu().getItem(7).setVisible(true);
                    mTvUserAccount.setVisibility(View.VISIBLE);
                    mTvUserAccount.setText(mNumberLast4);

                } else {
                    mNvMenuRight.getMenu().getItem(7).setVisible(false);
                    mTvUserAccount.setVisibility(View.GONE);
                }
                break;
            case R.id.iv_user_pic:
                if (mIsLogin) {
                    ToastUtil.showToast("您已登录", this);
                } else {
                    replaceFragment(new LoginFragment());
                }
                break;
            case R.id.btn_add_sub:
                treeV.addNode("");
                break;
            case R.id.btn_add_node:
            case R.id.img_opt4:
                treeV.addSubNode("");
                break;
            case R.id.btn_top:
            case R.id.img_opt1:
                treeV.addNode("", treeV.getTreeNodeIndex(treeV.getCurrentFocusNode()));
                break;
            case R.id.btn_bottom:
            case R.id.img_opt2:
                treeV.addNode("", treeV.getTreeNodeIndex(treeV.getCurrentFocusNode()) + 1);
                break;
            case R.id.btn_del:
            case R.id.img_opt3:
                if (treeV != null && treeV.getCurrentFocusNode() != treeV.getTreeModel().getRootNode()) {
                    treeV.deleteNode(treeV.getCurrentFocusNode());
                }
                break;
            case R.id.btn_copy:
                clone = TreeUtils.getInstance().cloneData(treeV.getCurrentFocusNode());
                break;
            case R.id.btn_paste:
                if (null != clone) {
                    TreeUtils.getInstance().addNode(treeV, treeV.getCurrentFocusNode(), clone);
//                    treeV.addSubNode(clone);
                    clone = null;
                }
                break;
            default:
                break;
        }
    }

    interface Callback {
        void onSuccesed(String msg);

        void onFail(String msg);
    }

    private void showDirPop(Context ctx, String title, Callback cb) {
        Log.e(TAG, "showDirPop: " + title);
        View view = View.inflate(ctx, R.layout.pop_create_dir, null);
        TextView tvCreate = view.findViewById(R.id.tv_create);
        EditText etCreate = view.findViewById(R.id.et_create);
        if (title != null) etCreate.setText(title);
        else etCreate.setHint("请输入文件名，默认为当前时间戳");
        tvCreate.setText("保存导图文件");

        PopupWindow mDirPopup = new PopupWindow(view, -1, -2);
        mDirPopup.setBackgroundDrawable(new BitmapDrawable());
        mDirPopup.setFocusable(true);
        mDirPopup.setOutsideTouchable(true);
        mDirPopup.showAtLocation(view, Gravity.CENTER, 0, 0);

        view.findViewById(R.id.btn_confirm).setOnClickListener(view1 -> {
            if (TextUtils.isEmpty(etCreate.getText().toString().trim())) {
                ToastUtil.showToast("请输入文件名", this);
                return;
            }
            mDirPopup.dismiss();
            path = null;
            saveMap("" + etCreate.getText(), true);
            if (cb != null) {
                cb.onSuccesed(null);
            }
        });
        view.findViewById(R.id.btn_cancel).setOnClickListener(view12 -> {
            mDirPopup.dismiss();
            if (cb != null) {
                cb.onFail(null);
            }
            if (isBuild) {
                isBuild = false;
            } else {
//                ToastUtil.showToast("已取消保存", this);
            }
        });
    }

    private void saveTempMap(Callback cb) {
        Log.e(TAG, "saveTempMap: " + path);
        if (null == path || path.endsWith(".temp.mapping")) {
            NodeModel root = treeV.getTreeModel().getRootNode();
            String fileName = root.strContent;

            if (fileName.length() == 0 && root.childNodes.size() == 0) {
                if (isJumpFileList) {
                    cb.onSuccesed(null);
                } else {
                    if (cb != null) {
                        cb.onFail("不需要存储");
                        MappingApplication.isRandom = false;
                    }
                }
                if (isSave) {
                    ToastUtil.showToast("当前无内容无法保存", this);
                }
            } else {
                showDirPop(this, fileName.replaceFirst("[.]+", ""), cb);
            }
            isSave = false;
            isJumpFileList = false;
        } else {
            // File could be already deleted in FileListFragment!
            if (new File(path).exists())
                saveMap(null, true);
            if (cb != null) cb.onSuccesed("");
        }
    }

    private void saveMap(String filename, boolean isShowToast) {
        mPayType = SPUtil.getString(Constant.PAY_TYPE, "免费版");
        Log.e(TAG, "saveMap: " + filename);
        final int freeSize = filename == null ? 6 : 5;
        List<FileTypeBean> allFiles = FileBlock.getAllFiles(null);
        if (null == path) {
            path = Constant.FILEPATH + filename + ".mapping";
        }
        if (!mPayType.equals("免费版") || allFiles == null || allFiles.size() < freeSize) {
            FileUtils.saveMap(treeV.getTreeModel(), path, new FileUtils.SaveFile() {
                @Override
                public void saveSuccess(String path) {
                    Log.e("saveSuccess", path);
                    if (isShowToast) {
                        ToastUtil.showToast("保存成功", MainActivity.this);
                    }
                }
            });
        } else {
            ToastUtil.showToast("保存失败，免费版最多只能保存4个", MainActivity.this);
        }
    }

    /**
     * 获取treeView 的bitmap
     *
     * @return
     */
    public Bitmap getTreeViewBitmap() {
        return ImageUtils.generateBitmap(treeV);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mIdDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.pay:
                replaceFragment(new PayFragment());
                break;
            case R.id.share:
                ImageUtils.importImage(treeV, this, false, ImageUtils.SD_PATH);
                showSharePop();
                break;
            case R.id.save:
                isSave = true;
                saveTempMap(null);
                break;
            case R.id.file_list:
                isJumpFileList = true;
                saveTempMap(new Callback() {
                    @Override
                    public void onSuccesed(String msg) {
                        replaceFragment(new FileListFragment());
                    }

                    @Override
                    public void onFail(String msg) {
                        isJumpFileList = false;
                    }
                });
                break;
            case R.id.import_image:
//                ImageUtils.importImage(treeV, this, true, ImageUtils.SD_PATH);
                replaceFragment(new SnapPreviewFragment());

                break;
            case R.id.suggestion:
                replaceFragment(new SuggestionFragment());
                break;
            case R.id.newly_build:
                isBuild = true;
                saveTempMap(new Callback() {
                    @Override
                    public void onSuccesed(String msg) {
                        treeV.initMapping(null);
                        path = null;
                    }

                    @Override
                    public void onFail(String msg) {
//                        treeV.initMapping(null);
//                        path = null;
                    }
                });
//				treeV.initMapping(null);
//				path = null;
                break;
            case R.id.logout:
                SPUtil.put(Constant.LOGIN, false);
                SPUtil.put(Constant.PAY_TYPE, "免费版");
                ToastUtil.showToast("退出成功，请重新登录", MainActivity.this);
                replaceFragment(new LoginFragment());
                break;
            default:
                break;

        }
        return false;
    }

    /**
     * 分享图片
     *
     * @param shareType 区分分享到朋友圈还是好友 0分享到对话，1分享到朋友圈
     */
    public void imageShare(int shareType) {
        String imagePath = ImageUtils.getImagePath();
        if (imagePath == null) {
//            ToastUtil.showToast("图片不存在", this);
            ImageUtils.importImage(treeV, MainActivity.this, false, ImageUtils.SD_PATH);
            imagePath = ImageUtils.getImagePath();

        }
        Log.d("==imagePath==", imagePath);


        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        WXImageObject imgObj = new WXImageObject(bmp);
        imgObj.setImagePath(imagePath);
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 100, 100, true);
        msg.setThumbImage(thumbBmp);
        bmp.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = shareType == 0 ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxapi.sendReq(req);
    }

    private View view;

    public void showSharePop() {

        mBottomDialog.show();
        initPopListener(view);
    }

    public void setSaveToSD(int visible) {
        if (null == view) {
            return;
        }
        view.findViewById(R.id.ll_save).setVisibility(visible);
    }


    private void initPopListener(View view) {

        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        LinearLayout llFriends = view.findViewById(R.id.ll_friends);
        LinearLayout llMoments = view.findViewById(R.id.ll_moments);
        LinearLayout llSave = view.findViewById(R.id.ll_save);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
            }
        });
        llMoments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
                imageShare(1);
            }
        });
        llFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
                imageShare(0);
            }
        });
        llSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popDismiss();
                ImageUtils.importImage(treeV, MainActivity.this, true, ImageUtils.SD_PATH);
            }
        });
    }

    private void popDismiss() {
        if (mBottomDialog != null) {
            mBottomDialog.dismiss();
        }
    }

    public void replaceFragment(BaseFragment baseFragment) {
        if (mIdDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            mIdDrawerLayout.closeDrawers();
            mIdDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_content, baseFragment)
                .addToBackStack(null)
                .commit();
    }


    public void openD() {
        if (mIdDrawerLayout != null) {
            mIdDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    //粘贴复制的接点
    public void copyNodes(NodeModel<String> node) {
        clone = TreeUtils.getInstance().cloneData(node);
        ToastUtil.showToast("复制节点成功", this);
    }

    //粘贴复制的接点
    public void saveNodes(NodeModel<String> node) {
        if (null != clone) {
            TreeUtils.getInstance().addNode(treeV, node, clone);
            clone = null;
        } else {
            treeV.addSubNode(node, "");
//            ToastUtil.showToast("当前没有复制任何内容",this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        Log.e(TAG, "res " + Arrays.toString(permissions) + "  " + Arrays.toString(grantResults));
        for (int res : grantResults) {
            if (res != 0) return;
        }
        init();
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        ToastUtil.showToast("已拒绝获取电话或存储空间权限并不再询问", this);
//        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
        new AlertDialog
                .Builder(this).setMessage("此功能需要电话和存储空间权限，否则无法正常使用，是否打开设置").setCancelable(false)
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        gotoAppDetailIntent(MainActivity.this);
                        finish();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ToastUtil.showToast("权限不足，退出软件", MainActivity.this);
                        finish();
                    }
                })
                .show();

//        } else {
//            finish();
//        }
    }

    public static void gotoAppDetailIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + activity.getPackageName()));
        activity.startActivity(intent);
    }

    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {
        finish();
    }

    String path = null;

    public void read(final String mFilePath) {
        Log.e(TAG, "read: " + mFilePath);
        if (path != null) {
            readFile(mFilePath);
//            saveTempMap(new Callback() {
//                @Override
//                public void onSuccesed(String msg) {
//                    readFile(mFilePath);
//                }
//
//                @Override
//                public void onFail(String msg) {
//                    readFile(mFilePath);
//                }
//            });
        } else readFile(mFilePath);
    }

    void readFile(String mFilePath) {
        Log.e(TAG, "readFile: " + mFilePath);
        path = mFilePath;
        //读取owant文件
        if (!TextUtils.isEmpty(mFilePath)) {
            try {
                System.out.println("filePath=" + mFilePath);
                Object o = FileUtils.readZipFileObject(mFilePath, "");
                TreeModel<String> tree = (TreeModel<String>) o;
                treeV.initMapping(tree);
            } catch (Exception e) {
                Log.e(TAG, "read Err:" + e);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "onPause");
        saveMap(path == null ? ".temp" : null, false);
        SPUtil.put("EditorMapPath", path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (EasyPermissions.hasPermissions(this,
                    Manifest.permission.READ_PHONE_STATE)) {
                getOrderType();
            }
        } else {
            getOrderType();
        }
    }

    private void getOrderType() {
        RetrofitManager.getInstance().getService().
                getOrderType(DeviceUtil.getDeviceId(this)).
                compose(Transformers.applySchedulers(this, ActivityEvent.DESTROY)).
                subscribe(dataBean -> {
                    Log.d("==getOrderType==", dataBean.toString());
                    if (dataBean.getStatus().equals(Constant.BIZ_SUCCESS)) {
                        SPUtil.put(Constant.PAY_TYPE, dataBean.getData());
                        mTvPayType.setText(dataBean.getData());
                    }
                });
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("EditorMapPath", path);
    }
}