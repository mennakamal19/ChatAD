package com.example.chatad.models;

public class Messagemodel
{
    private String name,msg,image,id;

    public Messagemodel() {
    }

    public Messagemodel(String name, String msg, String image, String id) {
        this.name = name;
        this.msg = msg;
        this.image = image;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
