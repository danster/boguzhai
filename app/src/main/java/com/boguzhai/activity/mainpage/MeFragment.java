package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.App;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.activity.login.RegisterActivity;
import com.boguzhai.activity.me.info.AccountInfoActivity;

public class MeFragment extends Fragment {
    private static String TAG = "MeFragment";
    private View view;
    private MainActivity context;
    private MyOnClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_me, null);
        context = (MainActivity)getActivity();
        listener = new MyOnClickListener();

        TextView title = (TextView)view.findViewById(R.id.title_center);
        title.setText("我");
        TextView title_right = (TextView)view.findViewById(R.id.title_right);
        title_right.setText("注册");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);
        view.findViewById(R.id.title_right).setVisibility(View.VISIBLE);

        this.showMyInfo();
        this.listen(R.id.me_login);
        this.listen(R.id.title_right);
        this.listen(R.id.me_myinfo);
        this.listen(R.id.me_my_auctions);
        this.listen(R.id.me_biding);
        this.listen(R.id.me_billing);
        this.listen(R.id.me_my_proxy);
        this.listen(R.id.me_my_favorites);
        this.listen(R.id.me_upload);
        this.listen(R.id.me_system);

        return view;
    }

    public void showMyInfo(){
        if( App.isLogin == false ){
            view.findViewById(R.id.me_myinfo).setVisibility(View.GONE);
            view.findViewById(R.id.me_login_view).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.me_myinfo).setVisibility(View.VISIBLE);
            view.findViewById(R.id.me_login_view).setVisibility(View.GONE);
        }
    }

    // 监听一个 ViewId
    public void listen( int id){
        View v = view.findViewById(id);
        if ( v != null ){
            v.setOnClickListener(listener);
        }
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.me_login:
                    Log.i("MeFragment","to login");
                    startActivity(new Intent(context, LoginActivity.class));
                    break;
                case R.id.title_right:
                    Log.i("MeFragment","to register");
                    startActivity(new Intent(context, RegisterActivity.class));
                    break;
                case R.id.me_myinfo:
                    Log.i("MeFragment","to show myinfo");
                    startActivity(new Intent(context, AccountInfoActivity.class));
                    break;
                default:
                    break;
            }

        }
    }

}