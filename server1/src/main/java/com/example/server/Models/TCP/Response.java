package com.example.server.Models.TCP;

import com.example.server.Enums.RequestType;
import com.example.server.Models.Entities.User;

public class Response {
    private String message;
    private int id;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Response{" +
                "message='" + message + '\'' +
                ", responseType=" + responseType +
                ", success=" + success +
                ", role='" + role + '\'' +
                '}';
    }

    private RequestType responseType;
    private Boolean success;
    private String role;

    public String getRole() {
        return role;
    }


    public Response(String message, RequestType responseType, Boolean success, String role) {
        this.message = message;
        this.responseType = responseType;
        this.success = success;
        this.role = role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Response() {}

    public Response(String response, RequestType responseType) {
        this.message = response;
        this.responseType = responseType;
        this.success = true;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public RequestType getResponseType() {
        return responseType;
    }

    public void setResponseType(RequestType responseType) {
        this.responseType = responseType;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
