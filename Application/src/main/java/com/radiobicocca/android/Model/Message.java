package com.radiobicocca.android.Model;

import java.util.Date;

/**
 * Created by lucap on 2/24/2018.
 */

public class Message {
    private String id;
    private String username;
    private String message;
    private Date time;


    public Message(String id, String username, String message, Date time) {
        this.id = id;
        this.username = username;
        this.message = message;
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
