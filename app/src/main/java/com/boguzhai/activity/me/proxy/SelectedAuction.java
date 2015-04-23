package com.boguzhai.activity.me.proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobo on 15/4/21.
 */
public class SelectedAuction {


    public String auctionName = "";//拍卖会名称
    public List<String> sessionNames = new ArrayList<>();//该拍卖会下的所有拍卖专场的名称集合

    /**
     * 判断某个专场是否存在
     * @param sessionName
     * @return
     */
    public boolean isSessionExist(String sessionName) {
        boolean result = false;
        if(sessionNames.size() <= 0) {
            return result;
        }
        for(String name : sessionNames) {
            if(sessionName.equals(name)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 添加一个专场
     */
    public void addSession(String sessionName) {
        if(!isSessionExist(sessionName)) {
            sessionNames.add(sessionName);
        }
    }

}
