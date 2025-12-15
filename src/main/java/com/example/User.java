package com.example;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "users")
public class User extends PanacheEntity {
    public String name;
    
    public Integer age;
    
    @Column(unique = true)
    public String email;
    
    public String password;
    
    public User() {}
    
    public User(String name, Integer age, String email, String password) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.password = password;
    }
    
    public static User findByEmail(String email) {
        return find("email", email).firstResult();
    }
}