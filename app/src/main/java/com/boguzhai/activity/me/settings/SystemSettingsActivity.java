package com.boguzhai.activity.me.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.ResetPwdActivity;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class SystemSettingsActivity extends BaseActivity {

    public static String TAG = "SystemSettingsActivity";

    private PackageManager packageManager;
    private int currentVersionCode;//当前app版本
    private String downloadUrl;//新版本的下载地址
    private String updateDescription;//更新描述
    private HttpClient conn;
    private HttpHandler downLoadHandler;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.setLinearView(R.layout.me_settings);
        title.setText("系统设置");
        init();
	}

	protected void init(){



        int ids[]={R.id.ll_app_update, R.id.ll_pwd, R.id.ll_about, R.id.ll_guide, R.id.ll_advice};
        listen(ids);

        /**
         * 获得当前app的版本号
         */
        packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            currentVersionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        builder = new AlertDialog.Builder(this);
        dialog = builder.create();
    }

	@Override
	public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.ll_app_update:
                //检查更新
                checkVersion();
                break;
            case R.id.ll_pwd:
                Utility.gotoActivity(ResetPwdActivity.class);
                break;
            case R.id.ll_about:
                Utility.gotoActivity(AboutBoGuZhaiActivity.class);
                break;
            case R.id.ll_guide:
                Utility.gotoActivity(AuctionGuideActivity.class);
                break;
            case R.id.ll_advice:
                startActivity(new Intent(SystemSettingsActivity.this, GetAdviceActivity.class));
                break;
        }
    }

    private void downloadApp() {
        HttpUtils http = new HttpUtils();
        builder = new AlertDialog.Builder(this);
        downLoadHandler = http.download(downloadUrl, Environment.getExternalStorageDirectory() + "boguzhai.apk",
                new RequestCallBack<File>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        showBeforeAppDownDialog();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<File> arg0) {
                        showAppDownLoadSucessDialog();
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        showAppDownLoadFailedDialog();
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {
                        showAppDownLodingDialog(total, current);
                    }
                });
    }


    /**
     * 连接服务器，检查更新
     */
    private void checkVersion() {

        //显示正在检查更新对话框
//        builder.setTitle("正在检查更新...");
//        builder.setMessage(updateDescription);
//        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.i(TAG, "下载地址" + downloadUrl);
//                downloadApp();
//            }
//        });
//        dialog = builder.show();

        conn = new HttpClient();
        conn.setUrl(Constant.url.replace("/phones/","/") + "uploadDownAction!checkVersion.htm");
        new Thread(new HttpPostRunnable(conn, new MyAppUpateHandler())).start();
    }


    private class MyAppUpateHandler extends HttpJsonHandler {
        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    Toast.makeText(Variable.app_context, "网络异常，获取数据失败", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Log.i(TAG, "获取信息成功");
                    try {
                        downloadUrl = data.getString("url");
                        updateDescription = data.getString("description");
                        int versionCode = data.getInt("versionCode");
                        if(versionCode == currentVersionCode) {
                            Toast.makeText(Variable.app_context, "当前已经是最新版本", Toast.LENGTH_SHORT).show();
                        }else {
                            showAppUpdateDialog();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(Variable.app_context, "数据解析异常", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }







    private void showAppUpdateDialog() {
        dialog.dismiss();
        builder.setTitle("检测到新版本");
        builder.setMessage(updateDescription);
        builder.setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i(TAG, "下载地址" + downloadUrl);
                downloadApp();
            }
        });
        dialog = builder.show();
    }

    private void showAppDownLodingDialog(long total, long current) {
        builder.setTitle("版本更新");
        builder.setMessage("正在下载:" + current / 1024+ "kb" + "/" + total / 1024 + "kb");
        builder.setPositiveButton("取消下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadHandler.cancel();
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }

    private void showBeforeAppDownDialog() {
        dialog.dismiss();
        builder.setTitle("版本更新");
        builder.setMessage("正在连接服务器...");
        builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downLoadHandler.cancel();
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }

    private void showAppDownLoadSucessDialog() {
        dialog.dismiss();
        builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage("下载完成");
        builder.setPositiveButton("安装", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setDataAndType(Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(),
                                "boguzhai.apk")),
                        "application/vnd.android.package-archive");
                startActivityForResult(intent, 0);
            }
        });
        builder.show();
    }

    private void showAppDownLoadFailedDialog() {
        dialog.dismiss();
        builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage("网络异常，下载失败");
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.show();
    }

}
