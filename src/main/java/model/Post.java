package model;

import annotation.postgresql.Id;
import annotation.postgresql.Model;

@Model(table = "posts")
public class Post {
    @Id
    public int id;
    public String text;
    public Long userId;
}
