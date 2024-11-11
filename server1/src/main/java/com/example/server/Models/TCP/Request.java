package com.example.server.Models.TCP;

import com.example.server.Enums.RequestType;

public class Request {
    private RequestType requestType;
    private String message;
    private Boolean success;

    public Request(RequestType requestType, String message) {
        this.requestType = requestType;
        this.message = message;
        this.success = true;
    }

    public Request(){}

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}

