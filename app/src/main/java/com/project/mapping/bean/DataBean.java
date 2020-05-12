package com.project.mapping.bean;

import androidx.annotation.NonNull;

public class DataBean extends BaseBean {

    /**
     * bizError : {"bizErrNum":"BIZ_0000","bizErrContent":"服务器内部错误，未指定原因","bizErrType":"OTHER_ERROR"}
     * message : 发送短信失败，请重试!
     */

    private BizErrorBean bizError;
    private String message;

    public BizErrorBean getBizError() {
        return bizError;
    }

    public void setBizError(BizErrorBean bizError) {
        this.bizError = bizError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class BizErrorBean {
        /**
         * bizErrNum : BIZ_0000
         * bizErrContent : 服务器内部错误，未指定原因
         * bizErrType : OTHER_ERROR
         */

        private String bizErrNum;
        private String bizErrContent;
        private String bizErrType;

        public String getBizErrNum() {
            return bizErrNum;
        }

        public void setBizErrNum(String bizErrNum) {
            this.bizErrNum = bizErrNum;
        }

        public String getBizErrContent() {
            return bizErrContent;
        }

        public void setBizErrContent(String bizErrContent) {
            this.bizErrContent = bizErrContent;
        }

        public String getBizErrType() {
            return bizErrType;
        }

        public void setBizErrType(String bizErrType) {
            this.bizErrType = bizErrType;
        }

        @NonNull
        @Override
        public String toString() {
            return "BizErrorBean{" +
                    "bizErrNum=" + bizErrNum +
                    ", bizErrContent='" + bizErrContent +
                    ", bizErrType='" + bizErrType +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        if (bizError != null) {
            return "DataBean{" +
                    "message=" + message +
                    ", " + super.toString() +
                    ", bizError=" + bizError.toString() +
                    '}';
        } else {
            return super.toString();
        }
    }
}
