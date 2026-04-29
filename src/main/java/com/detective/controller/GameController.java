package com.detective.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.detective.dao.GameDao;
import com.detective.model.CrimeScene;
import com.detective.model.Player;
import com.detective.model.Suspect;

@Controller
public class GameController {
    
    @Autowired
    private GameDao gameDao;
    
    @GetMapping("/")
    public String accueil() {
        return "index";
    }
    
    @PostMapping("/creer-joueur")
    public String creerJoueur(@RequestParam String username, HttpSession session, Model model) {
        if (gameDao.usernameExists(username)) {
            model.addAttribute("erreur", "Ce nom existe déjà !");
            return "index";
        }
        
        Player player = new Player(username);
        gameDao.savePlayer(player);
        session.setAttribute("joueur", player);
        return "redirect:/niveau/1";
    }
    
    @GetMapping("/charger-joueur")
    public String chargerJoueur(@RequestParam String username, HttpSession session, Model model) {
        Player player = gameDao.getPlayerByUsername(username);
        if (player == null) {
            model.addAttribute("erreur", "Joueur non trouvé !");
            return "index";
        }
        session.setAttribute("joueur", player);
        return "redirect:/niveau/" + player.getCurrentLevel();
    }
    
    @GetMapping("/niveau/{level}")
    public String niveau(@PathVariable int level, HttpSession session, Model model) {
        Player player = (Player) session.getAttribute("joueur");
        if (player == null) {
            return "redirect:/";
        }
        
        CrimeScene scene = gameDao.getCrimeSceneByLevel(level);
        if (scene == null) {
            return "redirect:/fin";
        }
        
        model.addAttribute("player", player);
        model.addAttribute("scene", scene);
        model.addAttribute("level", level);
        model.addAttribute("suspects", scene.getSuspects());
        
        return "game";
    }
    
    @PostMapping("/repondre")
    public String repondre(@RequestParam int suspectId, 
                           @RequestParam int crimeSceneId,
                           @RequestParam int level,
                           HttpSession session,
                           Model model) {
        Player player = (Player) session.getAttribute("joueur");
        if (player == null) {
            return "redirect:/";
        }
        
        // Compter les erreurs pour ce niveau
        Integer erreursCount = (Integer) session.getAttribute("erreurs_niveau_" + level);
        if (erreursCount == null) {
            erreursCount = 0;
        }
        
        boolean isGuilty = gameDao.checkGuilty(suspectId, crimeSceneId);
        
        // Récupérer les suspects pour les feedbacks
        Suspect suspectChoisi = gameDao.getSuspectById(suspectId);
        Suspect coupable = gameDao.getGuiltySuspectByCrimeSceneId(crimeSceneId);
        CrimeScene scene = gameDao.getCrimeSceneByLevel(level);
        
        if (isGuilty) {
            // Calcul des points selon le nombre d'erreurs
            int pointsMax = 100 * level;
            int pointsGagnes = pointsMax;
            
            if (erreursCount == 1) {
                pointsGagnes = pointsMax / 2;
            } else if (erreursCount >= 2) {
                pointsGagnes = pointsMax / 4;
            }
            
            int newScore = player.getTotalScore() + pointsGagnes;
            player.setTotalScore(newScore);
            gameDao.updateScore(player.getId(), newScore);
            
            // Reset des erreurs pour ce niveau
            session.removeAttribute("erreurs_niveau_" + level);
            
            // Feedback succès
            String feedback = "✅ BRAVO ! " + suspectChoisi.getName() + " est le coupable !\n" +
                              "🏆 Points gagnés : " + pointsGagnes + "/" + pointsMax + "\n" +
                              "🔍 " + getExplicationCoupable(level);
            
            model.addAttribute("message", feedback);
            
            if (level < 3) {
                player.setCurrentLevel(level + 1);
                gameDao.updateLevel(player.getId(), level + 1);
                model.addAttribute("nextLevel", level + 1);
                
                // Recharger le niveau suivant
                CrimeScene nextScene = gameDao.getCrimeSceneByLevel(level + 1);
                model.addAttribute("scene", nextScene);
                model.addAttribute("level", level + 1);
                model.addAttribute("suspects", nextScene.getSuspects());
                model.addAttribute("player", player);
                return "game";
            } else {
                model.addAttribute("player", player);
                return "fin";
            }
        } else {
            // Incrémenter les erreurs
            erreursCount++;
            session.setAttribute("erreurs_niveau_" + level, erreursCount);
            
            int pointsMax = 100 * level;
            int pointsRestants = pointsMax;
            if (erreursCount == 1) {
                pointsRestants = pointsMax / 2;
            } else if (erreursCount >= 2) {
                pointsRestants = pointsMax / 4;
            }
            
            // Feedback erreur
            String feedback = "❌ MAUVAIS SUSPECT ! " + suspectChoisi.getName() + " est innocent.\n" +
                              "💡 " + getIndiceCoupable(level) + "\n" +
                              "📉 Points max restants pour ce niveau : " + pointsRestants;
            
            model.addAttribute("erreur", feedback);
            model.addAttribute("player", player);
            model.addAttribute("level", level);
            model.addAttribute("scene", scene);
            model.addAttribute("suspects", scene.getSuspects());
            return "game";
        }
    }
    
    @GetMapping("/fin")
    public String fin(HttpSession session, Model model) {
        Player player = (Player) session.getAttribute("joueur");
        if (player == null) {
            return "redirect:/";
        }
        model.addAttribute("player", player);
        return "fin";
    }
    
    // Méthodes pour les feedbacks
    private String getExplicationCoupable(int level) {
        switch(level) {
            case 1:
                return "Younes tenait une bouteille avec un symbole de poison. Ses gants n'avaient aucune raison d'être dans la pâtisserie ce soir-là !";
            case 2:
                return "Nafaa avait les chaussures sèches alors qu'il aurait dû traverser le jardin humide. Il n'est jamais sorti de la maison !";
            case 3:
                return "Nafaa portait des gants anti-radiation AVANT de savoir que la vitrine était brisée. Il avait la clé dorée et le conteneur de transport !";
            default:
                return "Félicitations, vous avez résolu toutes les enquêtes !";
        }
    }
    
    private String getIndiceCoupable(int level) {
        switch(level) {
            case 1:
                return "Regardez attentivement ce que tient chaque suspect dans ses mains...";
            case 2:
                return "Observez l'état des chaussures de Nafaa par rapport au jardin humide...";
            case 3:
                return "Pourquoi Nafaa a-t-il des gants anti-radiation avant même d'entrer dans la salle ?";
            default:
                return "Analysez bien tous les indices pour trouver le coupable !";
        }
    }
    // 1 - Quitter la partie
@GetMapping("/quitter")
public String quitter(HttpSession session) {
    Player player = (Player) session.getAttribute("joueur");
    if (player != null) {
        player.setCurrentLevel(1);
        player.setTotalScore(0);
        gameDao.updateLevel(player.getId(), 1);
        gameDao.updateScore(player.getId(), 0);
    }
    session.invalidate();
    return "redirect:/";
}

// 2 - Rejouer
@GetMapping("/rejouer")
public String rejouer(HttpSession session) {
    Player player = (Player) session.getAttribute("joueur");
    if (player != null) {
        player.setCurrentLevel(1);
        player.setTotalScore(0);
        gameDao.updateLevel(player.getId(), 1);
        gameDao.updateScore(player.getId(), 0);
        session.setAttribute("joueur", player);
    }
    return "redirect:/niveau/1";
}

// 3 - Voir une scène précédente (lecture seule)
@GetMapping("/voir-scene/{level}")
public String voirScene(@PathVariable int level, HttpSession session, Model model) {
    Player player = (Player) session.getAttribute("joueur");
    if (player == null) {
        return "redirect:/";
    }
    // On ne peut voir que les scènes déjà jouées
    if (level >= player.getCurrentLevel()) {
        return "redirect:/niveau/" + player.getCurrentLevel();
    }
    CrimeScene scene = gameDao.getCrimeSceneByLevel(level);
    model.addAttribute("player", player);
    model.addAttribute("scene", scene);
    model.addAttribute("level", level);
    model.addAttribute("suspects", scene.getSuspects());
    model.addAttribute("readonly", true);
    return "scene-readonly";
}
}