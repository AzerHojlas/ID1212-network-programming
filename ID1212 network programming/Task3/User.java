package examples;

import java.io.Serializable;

public class User implements Serializable {
    
    private String username;
    private String password;
    private int userID;
    
    public User(){}
    
    public void setUsername(String username){
        this.username = username;
    }
    
    public void setPassword(String password){
        this.password = password;
    }
    
    public void setUserID(int id){
        this.userID = id;
    }
    
    public String getUsername(){
        return this.username;
    }
    
    public String getPassword(){
        return this.password;
    }
    
    public int getUserID(){
        return this.userID;
    }
    
}