package model;


import annotation.postgresql.Id;
import annotation.postgresql.Model;

@Model(table = "users")
public class User {
    @Id
    public int id;
    public String username;
    public String password;
    public String fullName;

    public User(int id, String username, String password, String fullName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
    }
}

