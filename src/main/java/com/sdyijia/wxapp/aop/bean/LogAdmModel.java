package com.sdyijia.wxapp.aop.bean;

import com.sdyijia.wxapp.modules.sys.bean.Base;

import javax.persistence.Entity;
import java.util.Date;

/**
 * 定义保存日志信息的类
 */
@Entity
public class LogAdmModel extends Base {
    private String userId; // 操作用户
    private String userName;
    private String admModel; // 模块
    private String admEvent; // 操作
    private Date createDate; // 创建时间
    private String admOptContent; // 操作内容
    private String note; // 备注

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAdmModel() {
        return admModel;
    }

    public void setAdmModel(String admModel) {
        this.admModel = admModel;
    }

    public String getAdmEvent() {
        return admEvent;
    }

    public void setAdmEvent(String admEvent) {
        this.admEvent = admEvent;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getAdmOptContent() {
        return admOptContent;
    }

    public void setAdmOptContent(String admOptContent) {
        this.admOptContent = admOptContent;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}