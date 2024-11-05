package com.example.client.Models.TCP;

import com.example.client.Enums.RequestType;

public class Request {
    private RequestType requestType;
    private String message;
    public Request(){}
    public Request(RequestType requestType, String message) {
        this.requestType = requestType;
        this.message = message;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public String getMessage() {
        return message;
    }
}
