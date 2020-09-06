package com.example.viewnews.usermodel;

import org.litepal.crud.LitePalSupport;

/**
 * Created by yyp on 2016/8/10.
 */
public class Comment extends LitePalSupport {

    String name; //评论者

    String content; //评论内容

    private String uniquekey;//评论新闻的标识
    public Comment(){

    }

    public Comment(String name, String content,String uniquekey ){
        this.name = name;
        this.content = content;
        this.uniquekey=uniquekey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
