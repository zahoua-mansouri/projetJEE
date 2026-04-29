package com.detective.model;

public class Player {
    private int id;
    private String username;
    private int totalScore;
    private int currentLevel;
    private String createdAt;
    
    // Constructeurs
    public Player() {}
    
    public Player(String username) {
        this.username = username;
        this.totalScore = 0;
        this.currentLevel = 1;
    }
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    
    public int getCurrentLevel() { return currentLevel; }
    public void setCurrentLevel(int currentLevel) { this.currentLevel = currentLevel; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}