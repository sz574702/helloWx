package com.sdyijia.wxapp.bean;

import javax.persistence.ManyToOne;

public class OAuth {
    private String openid;
    private String nickname;

    private String type;// 登录方式
    @ManyToOne
    private SysUser sysUser;

}
