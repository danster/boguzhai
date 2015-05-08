package com.boguzhai.activity.mainpage;

import android.app.Fragment;
import android.content.Intent;
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
import com.boguzhai.activity.me.order.PayOrderActivity;
import com.boguzhai.activity.me.proxy.ProxyPricingActivity;
import com.boguzhai.activity.me.settings.SystemSettingsActivity;
import com.boguzhai.activity.me.upload.UploadLotActivity;

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

        ((TextView)view.findViewById(R.id.title_center)).setText("我");
        view.findViewById(R.id.title_left).setVisibility(View.INVISIBLE);

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
    }

    /*************************** View Listener ****************************/
    public void listen( View v){ if(v!=null){ v.setOnClickListener(listener);}} // 监听一个 VieW
    public void listen( int id){ this.listen(view.findViewById(id));}           // 监听一个 View Id
    public void listen( int[] ids){ for(int id: ids){ this.listen(id);}}        // 监听一组 View Ids

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.title_right:
                    startActivity(new Intent(context, RegisterActivity.class));
                    break;
                case R.id.me_login:
                    startActivity(new Intent(context, LoginActivity.class));
                    break;
                case R.id.me_myinfo:
                    startActivity(new Intent(context, AccountInfoActivity.class));
                    break;
                case R.id.me_my_auctions:

                    startActivityByLogin(MyAuctionActivity.class);
                    break;
                case R.id.me_biding:
                    startActivityByLogin(BiddingActivity.class);
                    break;
                case R.id.me_billing:
                    startActivityByLogin(PayOrderActivity.class);
                    break;
                case R.id.me_my_proxy:
                    startActivityByLogin(ProxyPricingActivity.class);
                    break;
                case R.id.me_my_favorites:
                    startActivityByLogin(MyCollectionActivity.class);
                    break;
                case R.id.me_upload:
                    startActivityByLogin(UploadLotActivity.class);
                    break;
                case R.id.me_system:
                    startActivityByLogin(SystemSettingsActivity.class);
                    break;
                default:
                    break;
            }
        }
    }

    public void startActivityByLogin(Class<?> cls){
        context.startActivity(new Intent(context, cls));
//        if(Variable.isLogin == true)
//            mContext.startActivity(new Intent(mContext, cls));
//        else {
//            Intent intent = new Intent(mContext, LoginActivity.class);
//            mContext.startActivity(intent);
//        }
    }

}