package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.boguzhai.R;
import com.boguzhai.activity.base.Variable;
import com.boguzhai.activity.login.LoginActivity;
import com.boguzhai.activity.login.RegisterActivity;
import com.boguzhai.activity.me.bidding.BiddingActivity;
import com.boguzhai.activity.me.collect.MyCollectionActivity;
import com.boguzhai.activity.me.info.AccountInfoActivity;
import com.boguzhai.activity.me.myauction.MyAuctionActivity;
import com.boguzhai.activity.me.mylot.MyLotActivity;
import com.boguzhai.activity.me.order.MyPayOrderActivity;
import com.boguzhai.activity.me.settings.SystemSettingsActivity;
import com.boguzhai.activity.me.upload.UploadLotActivity;
import com.boguzhai.logic.utils.Utility;

public class MeFragment extends Fragment {
    private View view;
    private MyOnClickListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fg_me, null);
        listener = new MyOnClickListener();

        ((TextView)view.findViewById(R.id.title_center)).setText("我");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

        init();
        return view;
    }

    public void init(){
        if( Variable.isLogin == false ){
            ((TextView)view.findViewById(R.id.title_right)).setText("注册");
            view.findViewById(R.id.title_right).setVisibility(View.VISIBLE);
            view.findViewById(R.id.me_myinfo).setVisibility(View.GONE);
            view.findViewById(R.id.me_login_view).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.title_right).setVisibility(View.INVISIBLE);
            view.findViewById(R.id.me_myinfo).setVisibility(View.VISIBLE);
            view.findViewById(R.id.me_login_view).setVisibility(View.GONE);
            ((TextView)view.findViewById(R.id.name)).setText(Variable.account.nickname);

            String address = "";
            if(Variable.account.address_1.equals(Variable.account.address_2)){
                address = Variable.account.address_2+" "+Variable.account.address_3;
            } else {
                address = Variable.account.address_1+" "+Variable.account.address_2;
            }

            ((TextView)view.findViewById(R.id.address)).setText(address);
            ((TextView)view.findViewById(R.id.mobile)).setText(Variable.account.mobile);
        }

        this.listen(R.id.me_login);
        this.listen(R.id.title_right);
        this.listen(R.id.me_myinfo);
        this.listen(R.id.me_my_auctions);
        this.listen(R.id.me_biding);
        this.listen(R.id.me_mylot);
        this.listen(R.id.me_myorder);
        this.listen(R.id.me_my_favorites);
        this.listen(R.id.me_upload);
        this.listen(R.id.me_system);
    }

    /*************************** View Listener ****************************/
    public void listen( View v){ if(v!=null){ v.setOnClickListener(listener);}} // 监听一个 VieW
    public void listen( int id){ this.listen(view.findViewById(id));}           // 监听一个 View Id

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.title_right:
                    Utility.gotoActivity(RegisterActivity.class);
                    break;
                case R.id.me_login:
                    Utility.gotoActivity(LoginActivity.class);
                    break;
                case R.id.me_myinfo:
                    Utility.gotoActivity(AccountInfoActivity.class);
                    break;
                case R.id.me_my_auctions:
                    Utility.gotoActivity(MyAuctionActivity.class);
                    break;
                case R.id.me_biding:
                    Utility.gotoActivity(BiddingActivity.class);
                    break;
                case R.id.me_mylot:
                    if(Variable.isLogin==true) {
                        Utility.gotoActivity(MyLotActivity.class);
                    } else {
                        Utility.gotoLogin();
                    }
                    break;
                case R.id.me_myorder:
                    if(Variable.isLogin==true) {
                        Utility.gotoActivity(MyPayOrderActivity.class);
                    } else {
                        Utility.gotoLogin();
                    }
                    break;
                case R.id.me_my_favorites:
                    Utility.gotoActivity(MyCollectionActivity.class);
                    break;
                case R.id.me_upload:
                    Utility.gotoActivity(UploadLotActivity.class);
                    break;
                case R.id.me_system:
                    Utility.gotoActivity(SystemSettingsActivity.class);
                    break;
                default:
                    break;
            }
        }
    }


}