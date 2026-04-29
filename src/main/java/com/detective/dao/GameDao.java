package com.detective.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.detective.model.CrimeScene;
import com.detective.model.Player;
import com.detective.model.Suspect;
@Repository
public class GameDao {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // Créer un nouveau joueur
    public void savePlayer(Player player) {
        String sql = "INSERT INTO players (username, total_score, current_level) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, player.getUsername(), player.getTotalScore(), player.getCurrentLevel());
    }
    
    // Vérifier si le nom d'utilisateur existe
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM players WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
    
    // Récupérer un joueur par son nom
    public Player getPlayerByUsername(String username) {
        String sql = "SELECT * FROM players WHERE username = ?";
        List<Player> players = jdbcTemplate.query(sql, new PlayerRowMapper(), username);
        return players.isEmpty() ? null : players.get(0);
    }
    
    // Mettre à jour le score et le niveau
    public void updatePlayerScore(int playerId, int newScore, int newLevel) {
        String sql = "UPDATE players SET total_score = ?, current_level = ? WHERE id = ?";
        jdbcTemplate.update(sql, newScore, newLevel, playerId);
    }
    
    // RowMapper pour convertir les résultats SQL en objet Player
    private static class PlayerRowMapper implements RowMapper<Player> {
        @Override
        public Player mapRow(ResultSet rs, int rowNum) throws SQLException {
            Player player = new Player();
            player.setId(rs.getInt("id"));
            player.setUsername(rs.getString("username"));
            player.setTotalScore(rs.getInt("total_score"));
            player.setCurrentLevel(rs.getInt("current_level"));
            player.setCreatedAt(rs.getString("created_at"));
            return player;
        }
    }
        // Récupérer la scène de crime par niveau
    public CrimeScene getCrimeSceneByLevel(int levelNumber) {
        String sql = "SELECT * FROM crime_scenes WHERE level_id = ?";
        List<CrimeScene> scenes = jdbcTemplate.query(sql, new CrimeSceneRowMapper(), levelNumber);
        if (scenes.isEmpty()) return null;
        
        CrimeScene scene = scenes.get(0);
        scene.setSuspects(getSuspectsByCrimeSceneId(scene.getId()));
        return scene;
    }
    
    // Récupérer les suspects par scène de crime
    public List<Suspect> getSuspectsByCrimeSceneId(int crimeSceneId) {
        String sql = "SELECT * FROM suspects WHERE crime_scene_id = ?";
        return jdbcTemplate.query(sql, new SuspectRowMapper(), crimeSceneId);
    }
    
    // Vérifier si le suspect est le coupable
    public boolean checkGuilty(int suspectId, int crimeSceneId) {
        String sql = "SELECT is_guilty FROM suspects WHERE id = ? AND crime_scene_id = ?";
        Boolean isGuilty = jdbcTemplate.queryForObject(sql, Boolean.class, suspectId, crimeSceneId);
        return isGuilty != null && isGuilty;
    }
    
    // Mettre à jour le score du joueur
    public void updateScore(int playerId, int newScore) {
        String sql = "UPDATE players SET total_score = ? WHERE id = ?";
        jdbcTemplate.update(sql, newScore, playerId);
    }
    
    // Passer au niveau suivant
    public void updateLevel(int playerId, int newLevel) {
        String sql = "UPDATE players SET current_level = ? WHERE id = ?";
        jdbcTemplate.update(sql, newLevel, playerId);
    }
    
    // RowMapper pour CrimeScene
    private static class CrimeSceneRowMapper implements RowMapper<CrimeScene> {
        @Override
        public CrimeScene mapRow(ResultSet rs, int rowNum) throws SQLException {
            CrimeScene scene = new CrimeScene();
            scene.setId(rs.getInt("id"));
            scene.setLevelId(rs.getInt("level_id"));
            scene.setDescription(rs.getString("description"));
            scene.setImagePath(rs.getString("image_path"));
            scene.setClues(rs.getString("clues"));
            return scene;
        }
    }

    
    // RowMapper pour Suspect
    private static class SuspectRowMapper implements RowMapper<Suspect> {
        @Override
        public Suspect mapRow(ResultSet rs, int rowNum) throws SQLException {
            Suspect suspect = new Suspect();
            suspect.setId(rs.getInt("id"));
            suspect.setCrimeSceneId(rs.getInt("crime_scene_id"));
            suspect.setName(rs.getString("name"));
            suspect.setImagePath(rs.getString("image_path"));
            suspect.setDescription(rs.getString("description"));
            suspect.setGuilty(rs.getBoolean("is_guilty"));
            return suspect;
        }
    }
    public Suspect getSuspectById(int suspectId) {
    String sql = "SELECT * FROM suspects WHERE id = ?";
    return jdbcTemplate.queryForObject(sql, new SuspectRowMapper(), suspectId);
}

public Suspect getGuiltySuspectByCrimeSceneId(int crimeSceneId) {
    String sql = "SELECT * FROM suspects WHERE crime_scene_id = ? AND is_guilty = TRUE";
    return jdbcTemplate.queryForObject(sql, new SuspectRowMapper(), crimeSceneId);
}
}
