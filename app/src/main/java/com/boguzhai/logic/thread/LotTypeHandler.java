package com.boguzhai.logic.thread;

import android.util.Pair;

import com.boguzhai.activity.base.Variable;
import com.boguzhai.logic.dao.Lottype_1;
import com.boguzhai.logic.dao.Lottype_2;
import com.boguzhai.logic.dao.Lottype_3;
import com.boguzhai.logic.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by danster on 5/18/15.
 */
public class LotTypeHandler  extends HttpJsonHandler{
    @Override
    public void handlerData(int code, JSONObject data){
        try {
            switch (code){
                case 0:
                    if(data==null) break;
                    JSONObject zoneMap = data.getJSONObject("auctionTypeMap");
                    Variable.mapLottype = new ArrayList<Lottype_1>();
                    Variable.mapLottype1 = new ArrayList<Pair<String,String>>();

                    Lottype_1 lottype_1 = new Lottype_1(); lottype_1.id=""; lottype_1.name="不限";
                    Lottype_2 lottype_2 = new Lottype_2(); lottype_2.id=""; lottype_2.name="不限";
                    Lottype_3 lottype_3 = new Lottype_3(); lottype_3.id=""; lottype_3.name="不限";

                    lottype_2.child.add(lottype_3);
                    lottype_1.child.add(lottype_2);
                    Variable.mapLottype.add(lottype_1);

                    Iterator<?> keys_1 = zoneMap.keys();
                    while( keys_1.hasNext() ) {
                        Lottype_1 object1 = new Lottype_1();
                        String key_1 = (String)keys_1.next();
                        object1.id = key_1;

                        Lottype_2 lottype2 = new Lottype_2(); lottype2.id=""; lottype2.name="不限";
                        object1.child.add(lottype2);

                        if (zoneMap.get(key_1) instanceof JSONObject){
                            JSONObject jsonObject1 = (JSONObject)zoneMap.get(key_1);
                            object1.name = jsonObject1.getString("value");

                            Iterator<?> keys_2 = jsonObject1.keys();
                            while( keys_2.hasNext() ) {
                                String key_2 = (String)keys_2.next();
                                if(!key_2.equals("value") && jsonObject1.get(key_2) instanceof JSONObject ){
                                    JSONObject jsonObject2 = (JSONObject) jsonObject1.get(key_2);
                                    Lottype_2 object2 = new Lottype_2();
                                    object2.id = key_2;
                                    object2.name = jsonObject2.getString("value");

                                    Lottype_3 lottype3 = new Lottype_3(); lottype3.id=""; lottype3.name="不限";
                                    object2.child.add(lottype3);

                                    Iterator<?> keys_3 = jsonObject2.keys();
                                    while( keys_3.hasNext() ) {
                                        String key_3 = (String) keys_3.next();
                                        if(!key_3.equals("value")) {
                                            Lottype_3 object3 = new Lottype_3();

                                            object3.id = key_3;
                                            object3.name = jsonObject2.getString(key_3);
                                            object2.child.add(object3);
                                        }
                                    }
                                    object1.child.add(object2);
                                }
                            }
                        }
                        Variable.mapLottype.add(object1);
                    }
                    for(Lottype_1 type: Variable.mapLottype){
                        Variable.mapLottype1.add(new Pair<String, String>(type.id, type.name));
                    }
                    break;
                case 1:
                    Utility.toastMessage("无法获取拍品类型映射表");
                    break;
            }
        }catch(JSONException ex) {
            Utility.toastMessage("网络数据出现错误");
        }
    }
}
