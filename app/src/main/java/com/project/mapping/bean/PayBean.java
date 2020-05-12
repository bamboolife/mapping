package com.project.mapping.bean;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class PayBean extends PayErrorBean {
    /**
     * data : {"package":"Sign=WXPay","out_trade_no":"20200501120539385412659166707712","appid":"wx3922c854984be505","sign":"3F851383B7AB51FED8B7D6A22FA0E0D3","partnerid":"1588116381","prepayid":"wx011233402055064658af86381766243500","noncestr":"1233393659","timestamp":"1588307620"}
     * status : BIZ_SUCCESS
     * traceId : null
     */

    private DataBean data;
    private Object traceId;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }


    public Object getTraceId() {
        return traceId;
    }

    public void setTraceId(Object traceId) {
        this.traceId = traceId;
    }

    public static class DataBean {
        /**
         * package : Sign=WXPay
         * out_trade_no : 20200501120539385412659166707712
         * appid : wx3922c854984be505
         * sign : 3F851383B7AB51FED8B7D6A22FA0E0D3
         * partnerid : 1588116381
         * prepayid : wx011233402055064658af86381766243500
         * noncestr : 1233393659
         * timestamp : 1588307620
         */
        @SerializedName("package")
        private String packageX;
        private String out_trade_no;
        private String appid;
        private String sign;
        private String partnerid;
        private String prepayid;
        private String noncestr;
        private String timestamp;

        public String getPackageX() {
            return packageX;
        }

        public void setPackageX(String packageX) {
            this.packageX = packageX;
        }

        public String getOut_trade_no() {
            return out_trade_no;
        }

        public void setOut_trade_no(String out_trade_no) {
            this.out_trade_no = out_trade_no;
        }

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String toString() {
            return "DataBean{" +
                    "packageX=" + packageX +
                    ", out_trade_no='" + out_trade_no +
                    ", appid=" + appid +
                    ", sign=" + sign +
                    ", partnerid=" + partnerid +
                    ", prepayid=" + prepayid +
                    ", noncestr=" + noncestr +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    @NonNull
    @Override
    public String toString() {
        if (data == null) {
            return super.toString();
        } else {
            return "PayBean{" +
                    "status=" + getStatus() +
                    ",DateBean=" + data.toString() +
                    "}";
        }
    }
}
