package com.boguzhai.logic.dao;

/**
 * Created by danster on 5/8/15.
 */
public class AuthInfo {
    public String status = "";        //认证状态: 2"已提交未审核" -1"未认证" 1"审核未通过" 0"审核通过"
    public String property = "";      //用户性质: 1个人 2单位
    public String name = "";          //真实姓名 或 真实单位名称
    public String type = "";          //个人证件类型:"二代身份证" "三代身份证" "港澳台身份证" "护照" "其它"
    public String number = "";        //个人证件号码
    public String licenseNumber = "";      //营业执照号码
    public String taxNumber = "";          //税务登记证号码
    public String organizationNumber = ""; //组织机构代码
    public String legalPersonName = "";    //法人姓名
    public String legalPersonType = "";    //法人证件类型:"二代身份证" "三代身份证" "港澳台身份证" "护照" "其它"
    public String legalPersonNumber = "";  //法人证件号码

    public String getStatusStr(){
        String result = "";
        switch(status){
            case "-1": result = "未认证"; break;
            case "0": result = "审核通过"; break;
            case "1": result = "审核未通过"; break;
            case "2": result = "已提交未审核"; break;
            default: result = ""; break;
        }
        return result;
    }

    public String getPropertyStr(){
        String result = "个人";
        switch(property){
            case "1": result = "个人"; break;
            case "2": result = "单位"; break;
            default: result = "个人"; break;
        }
        return result;
    }
}
