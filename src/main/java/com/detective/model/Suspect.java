package com.detective.model;

public class Suspect {
    private int id;
    private int crimeSceneId;
    private String name;
    private String imagePath;
    private String description;
    private boolean isGuilty;
    
    // Constructeurs
    public Suspect() {}
    
    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCrimeSceneId() { return crimeSceneId; }
    public void setCrimeSceneId(int crimeSceneId) { this.crimeSceneId = crimeSceneId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isGuilty() { return isGuilty; }
    public void setGuilty(boolean guilty) { isGuilty = guilty; }
}