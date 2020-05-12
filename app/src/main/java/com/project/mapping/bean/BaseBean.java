package com.project.mapping.bean;

import androidx.annotation.NonNull;

public class BaseBean {


    /**
     * data : Successfully!
     * status : BIZ_SUCCESS
     * traceId : null
     */

    private String data;
    private String status;
    private Object traceId;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getTraceId() {
        return traceId;
    }

    public void setTraceId(Object traceId) {
        this.traceId = traceId;
    }

    @NonNull
    @Override
    public String toString() {
        return "BaseBean{" +
                "data=" + data +
                ", status=" + status +
                '}';
    }
}
