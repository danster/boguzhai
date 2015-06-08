package com.boguzhai.logic.thread;

import android.util.Pair;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Address_1;
import com.boguzhai.logic.dao.Address_2;
import com.boguzhai.logic.dao.Address_3;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by danster on 5/18/15.
 */
public class AddressHandler extends HttpJsonHandler{
    @Override
    public void handlerData(int code, JSONObject data){
        try {
            switch (code){
                case 0:
                    if(data==null) break;

                    JSONObject zoneMap = data.getJSONObject("addressZoneMap");

                    if(Variable.mapZone == null)
                        Variable.mapZone = new ArrayList<Address_1>();
                    else
                        Variable.mapZone.clear();

                    if(Variable.mapProvince == null)
                        Variable.mapProvince = new ArrayList<Pair<String,String>>();
                    else
                        Variable.mapProvince.clear();

                    Address_1 address_1 = new Address_1(); address_1.id=""; address_1.name="不限";
                    Address_2 address_2 = new Address_2(); address_2.id=""; address_2.name="不限";
                    Address_3 address_3 = new Address_3(); address_3.id=""; address_3.name="不限";

                    address_2.child.add(address_3);
                    address_1.child.add(address_2);
                    Variable.mapZone.add(address_1);

                    Iterator<?> keys_1 = zoneMap.keys();
                    while( keys_1.hasNext() ) {
                        Address_1 object1 = new Address_1();
                        String key_1 = (String)keys_1.next();
                        object1.id = key_1;

                        Address_2 address2 = new Address_2(); address2.id=""; address2.name="不限";
                        object1.child.add(address2);

                        if (zoneMap.get(key_1) instanceof JSONObject){
                            JSONObject jsonObject1 = (JSONObject)zoneMap.get(key_1);
                            object1.name = jsonObject1.getString("value");

                            Iterator<?> keys_2 = jsonObject1.keys();
                            while( keys_2.hasNext() ) {
                                String key_2 = (String)keys_2.next();
                                if(!key_2.equals("value") && jsonObject1.get(key_2) instanceof JSONObject ){
                                    JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key_2);
                                    Address_2 object2 = new Address_2();
                                    object2.id = key_2;
                                    object2.name = jsonObject2.getString("value");

                                    Address_3 address3 = new Address_3(); address3.id=""; address3.name="不限";
                                    object2.child.add(address3);

                                    Iterator<?> keys_3 = jsonObject2.keys();
                                    while( keys_3.hasNext() ) {
                                        String key_3 = (String)keys_3.next();
                                        if(!key_3.equals("value")) {
                                            Address_3 object3 = new Address_3();

                                            object3.id = key_3;
                                            object3.name = jsonObject2.getString(key_3);
                                            object2.child.add(object3);
                                        }
                                    }
                                    object1.child.add(object2);
                                }
                            }
                        }
                        Variable.mapZone.add(object1);
                    }

                    for(Address_1 addr: Variable.mapZone){
                        Variable.mapProvince.add(new Pair<String, String>(addr.id, addr.name));
                    }
                    break;
                case 1:
                    Utility.toastMessage("无法获取行政区域映射表");
                    break;
            }
        }catch(JSONException ex) {
            Utility.toastMessage("网络数据出现错误");
        }
    }
}
