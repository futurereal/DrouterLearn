package com.viomi.modulesetting.entity;

/**
 * Created by Ljh on 2021/3/24.
 * Description:
 */
public class VMAccountEntity {

    /**
     * userViomiInfoBean : {"id":-1,"userId":111246621,"creator":null,"status":1,"updateTime":null,"createTime":null,"pwd":null,
     * "nickName":"用户3117","mobile":"18588703117","account":"20203068","headImg":"https://cdn.cnbj2.fds.api.mi-img
     * .com/viomi-fileupload/images/user_head/ym_head_default@2x.png","gender":null,"email":null,"openId":null}
     * token : AvUVDut7RqutTzTt
     */

    private UserViomiInfoBeanBean userViomiInfoBean;
    private String token;

    public UserViomiInfoBeanBean getUserViomiInfoBean() {
        return userViomiInfoBean;
    }

    public void setUserViomiInfoBean(UserViomiInfoBeanBean userViomiInfoBean) {
        this.userViomiInfoBean = userViomiInfoBean;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static class UserViomiInfoBeanBean {
        /**
         * id : -1
         * userId : 111246621
         * creator : null
         * status : 1
         * updateTime : null
         * createTime : null
         * pwd : null
         * nickName : 用户3117
         * mobile : 18588703117
         * account : 20203068
         * headImg : https://cdn.cnbj2.fds.api.mi-img.com/viomi-fileupload/images/user_head/ym_head_default@2x.png
         * gender : null
         * email : null
         * openId : null
         */

        private int id;
        private String userId;
        private Object creator;
        private int status;
        private Object updateTime;
        private Object createTime;
        private Object pwd;
        private String nickName;
        private String mobile;
        private String account;
        private String headImg;
        private Object gender;
        private Object email;
        private Object openId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public Object getCreator() {
            return creator;
        }

        public void setCreator(Object creator) {
            this.creator = creator;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public Object getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(Object updateTime) {
            this.updateTime = updateTime;
        }

        public Object getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Object createTime) {
            this.createTime = createTime;
        }

        public Object getPwd() {
            return pwd;
        }

        public void setPwd(Object pwd) {
            this.pwd = pwd;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public Object getGender() {
            return gender;
        }

        public void setGender(Object gender) {
            this.gender = gender;
        }

        public Object getEmail() {
            return email;
        }

        public void setEmail(Object email) {
            this.email = email;
        }

        public Object getOpenId() {
            return openId;
        }

        public void setOpenId(Object openId) {
            this.openId = openId;
        }
    }
}

