package model;


import annotation.postgresql.GeneratedValue;
import annotation.postgresql.Id;
import annotation.postgresql.Model;
import enm.GenerationType;

@Model(table = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String username;
    public String password;
    public String fullName;
    public String email;

    public User(int id, String username, String password, String fullName, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
    }
}

