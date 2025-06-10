package model;

import annotation.postgresql.GeneratedValue;
import annotation.postgresql.Id;
import annotation.postgresql.Model;
import enm.GenerationType;

@Model(table = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String text;
    public Long userId;
}
