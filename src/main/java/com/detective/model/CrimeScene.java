package com.detective.model;

import java.util.List;

public class CrimeScene {
    private int id;
    private int levelId;
    private String description;
    private String imagePath;
    private String clues;
    private List<Suspect> suspects;
    
    // Constructeurs
    public CrimeScene() {}
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getClues() { return clues; }
    public void setClues(String clues) { this.clues = clues; }
    
    public List<Suspect> getSuspects() { return suspects; }
    public void setSuspects(List<Suspect> suspects) { this.suspects = suspects; }
}