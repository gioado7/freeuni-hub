package Manage;

public class User {
    String userName;
    String userLastName;
    String sex;
    String course;
    public User(String userName,String userLastName, String sqesi, String course){
        this.userName = userName;
        this.userLastName = userLastName;
        this.sex = sex;
        this.course = course;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserLastName(){
        return userLastName;
    }
    public String getSex(){
        return sex;
    }
    public String getCourse(){
        return course;
    }
}