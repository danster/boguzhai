package com.boguzhai.activity.me.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.boguzhai.R;
import com.boguzhai.activity.base.BaseActivity;
import com.boguzhai.activity.base.Constant;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.logic.dao.Advice;
import com.boguzhai.logic.thread.HttpJsonHandler;
import com.boguzhai.logic.thread.HttpPostRunnable;
import com.boguzhai.logic.utils.HttpClient;
import com.boguzhai.logic.utils.Utility;
import com.boguzhai.logic.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GetAdviceActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, XListView.IXListViewListener {

    public final String TAG = "AdviceActivity";
    public ArrayList<Advice> advices;
    public MyAdviceAdapter adapter;

    private HttpClient conn;
    private int number = 1;//分页序号
    private int totalCount = 0;//结果总数
    private int size = 0;//
    private int currentCount = 0;//当前总数
    private Button btn_advice_add;
    private XListView lv;
    private SwipeRefreshLayout swipe_layout_advices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLinearView(R.layout.settings_advice);
        title.setText("我的投诉建议");
        init();
    }

    private void init() {
        lv = (XListView) findViewById(R.id.lv_advices);
        swipe_layout_advices = (SwipeRefreshLayout) findViewById(R.id.swipe_layout_advices);
        btn_advice_add = (Button) findViewById(R.id.btn_advice_add);
        listen(btn_advice_add);

        swipe_layout_advices.setColorSchemeColors(R.color.gold);
        swipe_layout_advices.setOnRefreshListener(this);

        lv.setPullLoadEnable(true);
        lv.setXListViewListener(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到投诉/建议详情界面
                Intent intent = new Intent(GetAdviceActivity.this, CheckAdivceActivity.class);
                intent.putExtra("adviceId", advices.get(position - 1).id);
                startActivity(intent);
            }
        });
//        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                View dialog = View.inflate(context, R.layout.settings_advice_option_dialog, null);
//                TextView tv_delete = (TextView) dialog.findViewById(R.id.delete_advice);
//                final AlertDialog alertDialog = builder.setView(dialog).show();
//                tv_delete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //http请求
//                        Log.i(TAG, "删除投诉/建议http请求");
//                        deleteAdvice(position - 1, view);
//                        //关闭对话框
//                        alertDialog.dismiss();
//                    }
//                });
//                return true;
//            }
//        });

        advices = new ArrayList<>();
        requestData();
    }


//    private void deleteAdvice(int posistion, View view) {
//        conn = new HttpClient();
//        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
//        conn.setUrl(Constant.url + "pProposeAction!removeAdviceById.htm");
//        conn.setParam("id", advices.get(posistion).id);
//        Log.i(TAG, "id:" + advices.get(posistion).id);
//        new Thread(new HttpPostRunnable(conn, new DeleteAdvicesHandler(posistion, view))).start();
//    }


    /**
     * 请求网络数据
     */
    public void requestData() {
        conn = new HttpClient();
        conn.setHeader("cookie", "JSESSIONID=" + Variable.account.sessionid);
        conn.setUrl(Constant.url + "pProposeAction!getAdviceList.htm");
        conn.setParam("number", String.valueOf(number));
        new Thread(new HttpPostRunnable(conn, new AdvicesHandler())).start();
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_advice_add:
                startActivity(new Intent(GetAdviceActivity.this, AddAdviceActivity.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        Log.i(TAG, "刷新");
        number = 1;
        advices.clear();
        swipe_layout_advices.setRefreshing(true);
        requestData();
    }

    @Override
    public void onLoadMore() {
        Log.i(TAG, "加载更多");
        if(currentCount >= totalCount) {
            lv.stopLoadMore();
            Utility.toastMessage("没有更多数据了");
            return;
        }
        number++;
        requestData();
    }

    private class AdvicesHandler extends HttpJsonHandler {

        @Override
        public void handlerData(int code, JSONObject data) {
            switch (code) {
                case 1:
                    if (number > 1) {
                        number--;
                        lv.stopLoadMore();
                    }
                    swipe_layout_advices.setRefreshing(false);
                    Toast.makeText(GetAdviceActivity.this, "网络异常，获取信息失败", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    if (number > 1) {
                        number--;
                        lv.stopLoadMore();
                    }
                    swipe_layout_advices.setRefreshing(false);
                    Toast.makeText(GetAdviceActivity.this, "用户名密码失效，请重新登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(GetAdviceActivity.this, LoginActivity.class));
                    break;
                case 0:
                    try {
                        size = Integer.parseInt(data.getString("size"));//每页的数目
                        totalCount = Integer.parseInt(data.getString("count"));//总的数目
                        JSONArray jArray = data.getJSONArray("adviceList");
                        currentCount += jArray.length();
                        if (jArray.length() == 0) {
                            if (number != 1) {
                                number--;
                                lv.stopLoadMore();
                            }else {
                                Utility.toastMessage("暂无数据");
                                swipe_layout_advices.setRefreshing(false);
                            }
                            return;
                        }
                        Advice advice;
                        for (int i = 0; i < jArray.length(); i++) {
                            advice = new Advice();
                            advice.id = jArray.getJSONObject(i).getString("id");
                            advice.orderId = jArray.getJSONObject(i).getString("orderId");
                            advice.time = jArray.getJSONObject(i).getString("time");
                            advice.title = jArray.getJSONObject(i).getString("title");
                            advice.status = jArray.getJSONObject(i).getString("status");
                            advices.add(advice);
                        }

                        if (number == 1) {//刷新
                            Utility.toastMessage("刷新成功");
                            swipe_layout_advices.setRefreshing(false);
                            adapter = new MyAdviceAdapter();
                            lv.setAdapter(adapter);
                        } else if (number > 1) {//加载更多
                            adapter.notifyDataSetChanged();
                            lv.stopLoadMore();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    public final class ViewHolder {
        private TextView advice_orderId;
        private TextView advice_question;
        private TextView advice_status;
        private TextView advice_time;
    }


    public class MyAdviceAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return advices.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(GetAdviceActivity.this, R.layout.me_settings_advices_item, null);
                holder.advice_orderId = (TextView) convertView.findViewById(R.id.advice_orderId);
                holder.advice_question = (TextView) convertView.findViewById(R.id.advice_question);
                holder.advice_status = (TextView) convertView.findViewById(R.id.advice_status);
                holder.advice_time = (TextView) convertView.findViewById(R.id.advice_time);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.advice_orderId.setText(advices.get(position).orderId);
            holder.advice_question.setText(advices.get(position).title);
            holder.advice_status.setText(advices.get(position).status);
            holder.advice_time.setText(advices.get(position).time);

            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


    }




//    private class DeleteAdvicesHandler extends HttpJsonHandler {
//
//        private View view;
//        private int position;
//
//        DeleteAdvicesHandler(int position, View view) {
//            this.view = view;
//            this.position = position;
//        }
//
//        @Override
//        public void handlerData(int code, JSONObject data) {
//            super.handlerData(code, data);
//            switch (code) {
//                case 0:
//                    Utility.toastMessage("删除成功");
//                    //显示位移动画
//                    TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
//                            Animation.RELATIVE_TO_SELF, -1.0f,
//                            Animation.RELATIVE_TO_SELF, 0,
//                            Animation.RELATIVE_TO_SELF, 0);
//                    ta.setDuration(200);
//                    view.startAnimation(ta);
//                    ta.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            advices.remove(position);
//                            adapter.notifyDataSetChanged();
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//
//                    break;
//                case 1:
//                    Utility.toastMessage("服务器出错，删除失败");
//                    break;
//
//            }
//        }
//    }
}
