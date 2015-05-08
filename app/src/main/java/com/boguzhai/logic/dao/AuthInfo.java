package com.boguzhai.logic.dao;

/**
 * Created by danster on 5/8/15.
 */
public class AuthInfo {
    public String status = "";        //认证状态: 未认证 审核中 审核未通过 审核通过
    public String property = "";      //用户性质: 个人 单位
    public String name = "";          //真实姓名 或 真实单位名称
    public String type = "";          //个人证件类型:"二代身份证" "三代身份证" "港澳台身份证" "护照" "其它"
    public String number = "";        //个人证件号码
    public String licenseNumber = "";      //营业执照号码
    public String taxNumber = "";          //税务登记证号码
    public String organizationNumber = ""; //组织机构代码
    public String legalPersonName = "";    //法人姓名
    public String legalPersonType = "";    //法人证件类型:"二代身份证" "三代身份证" "港澳台身份证" "护照" "其它"
    public String legalPersonNumber = "";  //法人证件号码
}
