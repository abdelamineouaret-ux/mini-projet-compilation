// MiniCompilateurGUI.java
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;

public class MiniCompilateur extends JFrame {
    
    // Composants de l'interface
    private JTextArea zoneCode;
    private JTextArea zoneResultat;
    private JButton btnCompiler;
    private JButton btnOuvrirFichier;
    private JButton btnNouveauFichier;
    private JButton btnSauvegarder;
    private JButton btnEffacer;
    private JLabel lblStatut;
    private JLabel lblFichier;
    private File fichierCourant;
    
    public MiniCompilateur() {
        initialiserInterface();
    }
    
    private void initialiserInterface() {
        // Configuration de la fenêtre principale
        setTitle("Mini-Compilateur C - OUARET Abdelamine");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Couleurs du thème
        Color couleurFond = new Color(40, 44, 52);
        Color couleurTexte = new Color(171, 178, 191);
        Color couleurAccent = new Color(97, 175, 239);
        Color couleurSucces = new Color(152, 195, 121);
        Color couleurErreur = new Color(224, 108, 117);
        
        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(couleurFond);
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // === EN-TÊTE ===
        JPanel panelEntete = creerPanelEntete();
        panelPrincipal.add(panelEntete, BorderLayout.NORTH);
        
        // === ZONE CENTRALE (Code + Résultat) ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setBackground(couleurFond);
        
        // Panel gauche - Code source
        JPanel panelGauche = new JPanel(new BorderLayout(5, 5));
        panelGauche.setBackground(couleurFond);
        
        JLabel lblCode = new JLabel("📝 Code Source C");
        lblCode.setForeground(couleurTexte);
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelGauche.add(lblCode, BorderLayout.NORTH);
        
        zoneCode = new JTextArea();
        zoneCode.setFont(new Font("Consolas", Font.PLAIN, 14));
        zoneCode.setBackground(new Color(30, 34, 42));
        zoneCode.setForeground(couleurTexte);
        zoneCode.setCaretColor(couleurAccent);
        zoneCode.setTabSize(4);
        zoneCode.setText("/* Programme de test */\n\nint main() {\n    int x = 10;\n    \n    switch (x) {\n        case 10:\n            x = x + 5;\n            break;\n        \n        default:\n            x = 0;\n            break;\n    }\n    \n    return 0;\n}");
        
        JScrollPane scrollCode = new JScrollPane(zoneCode);
        scrollCode.setBorder(BorderFactory.createLineBorder(couleurAccent, 1));
        panelGauche.add(scrollCode, BorderLayout.CENTER);
        
        // Panel droit - Résultat de compilation
        JPanel panelDroit = new JPanel(new BorderLayout(5, 5));
        panelDroit.setBackground(couleurFond);
        
        JLabel lblResultat = new JLabel("📊 Résultat de la Compilation");
        lblResultat.setForeground(couleurTexte);
        lblResultat.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelDroit.add(lblResultat, BorderLayout.NORTH);
        
        zoneResultat = new JTextArea();
        zoneResultat.setFont(new Font("Consolas", Font.PLAIN, 12));
        zoneResultat.setBackground(new Color(30, 34, 42));
        zoneResultat.setForeground(couleurTexte);
        zoneResultat.setEditable(false);
        zoneResultat.setText("Cliquez sur 'Compiler' pour analyser votre code...");
        
        JScrollPane scrollResultat = new JScrollPane(zoneResultat);
        scrollResultat.setBorder(BorderFactory.createLineBorder(couleurAccent, 1));
        panelDroit.add(scrollResultat, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(panelGauche);
        splitPane.setRightComponent(panelDroit);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);
        
        // === BARRE D'OUTILS ===
        JPanel panelOutils = creerPanelOutils();
        panelPrincipal.add(panelOutils, BorderLayout.SOUTH);
        
        // Ajouter le panel principal à la fenêtre
        add(panelPrincipal);
        
        // Raccourcis clavier
        ajouterRaccourcisClavier();
    }
    
    private JPanel creerPanelEntete() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 44, 52));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Titre
        JLabel lblTitre = new JLabel("🖥️ Mini-Compilateur Langage C");
        lblTitre.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitre.setForeground(new Color(97, 175, 239));
        
        JLabel lblSousTitre = new JLabel("Auteur: OUARET Abdelamine | Université de Béjaia - 2025");
        lblSousTitre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSousTitre.setForeground(new Color(171, 178, 191));
        
        JPanel panelTitres = new JPanel(new GridLayout(2, 1));
        panelTitres.setBackground(new Color(40, 44, 52));
        panelTitres.add(lblTitre);
        panelTitres.add(lblSousTitre);
        
        panel.add(panelTitres, BorderLayout.WEST);
        
        // Info fichier
        lblFichier = new JLabel("📄 Nouveau fichier");
        lblFichier.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblFichier.setForeground(new Color(171, 178, 191));
        panel.add(lblFichier, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel creerPanelOutils() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(40, 44, 52));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Panel boutons
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBoutons.setBackground(new Color(40, 44, 52));
        
        Color couleurBouton = new Color(97, 175, 239);
        Color couleurTexte = Color.WHITE;
        
        // Bouton Compiler (principal)
        btnCompiler = creerBouton("▶️ Compiler", couleurBouton, couleurTexte);
        btnCompiler.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCompiler.addActionListener(e -> compiler());
        
        // Bouton Ouvrir Fichier
        btnOuvrirFichier = creerBouton("📁 Ouvrir", new Color(152, 195, 121), couleurTexte);
        btnOuvrirFichier.addActionListener(e -> ouvrirFichier());
        
        // Bouton Nouveau Fichier
        btnNouveauFichier = creerBouton("📄 Nouveau", new Color(209, 154, 102), couleurTexte);
        btnNouveauFichier.addActionListener(e -> nouveauFichier());
        
        // Bouton Sauvegarder
        btnSauvegarder = creerBouton("💾 Sauvegarder", new Color(198, 120, 221), couleurTexte);
        btnSauvegarder.addActionListener(e -> sauvegarder());
        
        // Bouton Effacer
        btnEffacer = creerBouton("🗑️ Effacer", new Color(224, 108, 117), couleurTexte);
        btnEffacer.addActionListener(e -> effacer());
        
        panelBoutons.add(btnCompiler);
        panelBoutons.add(btnOuvrirFichier);
        panelBoutons.add(btnNouveauFichier);
        panelBoutons.add(btnSauvegarder);
        panelBoutons.add(btnEffacer);
        
        // Label statut
        lblStatut = new JLabel("Prêt à compiler");
        lblStatut.setForeground(new Color(171, 178, 191));
        lblStatut.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(panelBoutons, BorderLayout.WEST);
        panel.add(lblStatut, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton creerBouton(String texte, Color fond, Color texteCouleur) {
        JButton btn = new JButton(texte);
        btn.setBackground(fond);
        btn.setForeground(texteCouleur);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 35));
        
        // Effet hover
        btn.addMouseListener(new MouseAdapter() {
            Color couleurOriginale = btn.getBackground();
            
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(couleurOriginale.brighter());
            }
            
            public void mouseExited(MouseEvent e) {
                btn.setBackground(couleurOriginale);
            }
        });
        
        return btn;
    }
    
    private void ajouterRaccourcisClavier() {
        // F5 pour compiler
        getRootPane().registerKeyboardAction(
            e -> compiler(),
            KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Ctrl+O pour ouvrir
        getRootPane().registerKeyboardAction(
            e -> ouvrirFichier(),
            KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Ctrl+S pour sauvegarder
        getRootPane().registerKeyboardAction(
            e -> sauvegarder(),
            KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        // Ctrl+N pour nouveau
        getRootPane().registerKeyboardAction(
            e -> nouveauFichier(),
            KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }
    
    private void compiler() {
        lblStatut.setText("⏳ Compilation en cours...");
        lblStatut.setForeground(new Color(209, 154, 102));
        
        String codeSource = zoneCode.getText();
        
        if (codeSource.trim().isEmpty()) {
            zoneResultat.setText("❌ ERREUR: Aucun code à compiler!");
            lblStatut.setText("Erreur: code vide");
            lblStatut.setForeground(new Color(224, 108, 117));
            return;
        }
        
        StringBuilder resultat = new StringBuilder();
        resultat.append("╔════════════════════════════════════════════════════════════╗\n");
        resultat.append("║         COMPILATION DU CODE SOURCE                         ║\n");
        resultat.append("╚════════════════════════════════════════════════════════════╝\n\n");
        
        // Étape 1: Analyse lexicale
        resultat.append(">>> ÉTAPE 1: ANALYSE LEXICALE\n");
        resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        AnalyseurLexical analyseurLexical = new AnalyseurLexical(codeSource);
        List<Token> tokens = analyseurLexical.analyser();
        
        resultat.append("Nombre de tokens reconnus: ").append(tokens.size() - 1).append("\n\n");
        
        // Afficher les tokens
        resultat.append("Tokens identifiés:\n");
        int count = 0;
        for (Token token : tokens) {
            if (token.getType() != TypeToken.FIN_FICHIER && count < 30) {
                resultat.append(String.format("  %-20s %-15s [L%d:C%d]\n",
                    token.getType(),
                    "'" + token.getValeur() + "'",
                    token.getLigne(),
                    token.getColonne()
                ));
                count++;
            }
        }
        
        if (tokens.size() > 31) {
            resultat.append("  ... et ").append(tokens.size() - 31).append(" autres tokens\n");
        }
        
        resultat.append("\n");
        
        // Erreurs lexicales
        List<String> erreursLex = analyseurLexical.getErreurs();
        if (erreursLex.size() > 0) {
            resultat.append("⚠️  ERREURS LEXICALES DÉTECTÉES:\n");
            for (String erreur : erreursLex) {
                resultat.append("  ✗ ").append(erreur).append("\n");
            }
        } else {
            resultat.append("✅ Aucune erreur lexicale détectée\n");
        }
        
        resultat.append("\n");
        
        // Étape 2: Analyse syntaxique
        resultat.append(">>> ÉTAPE 2: ANALYSE SYNTAXIQUE\n");
        resultat.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        
        AnalyseurSyntaxique analyseurSyntaxique = new AnalyseurSyntaxique(tokens);
        analyseurSyntaxique.analyser();
        
        List<String> erreursSyn = analyseurSyntaxique.getErreurs();
        if (erreursSyn.size() > 0) {
            resultat.append("⚠️  ERREURS SYNTAXIQUES DÉTECTÉES:\n");
            for (String erreur : erreursSyn) {
                resultat.append("  ✗ ").append(erreur).append("\n");
            }
        } else {
            resultat.append("✅ Aucune erreur syntaxique détectée\n");
        }
        
        resultat.append("\n");
        
        // Résultat final
        resultat.append("╔════════════════════════════════════════════════════════════╗\n");
        resultat.append("║              RÉSULTAT DE LA COMPILATION                    ║\n");
        resultat.append("╚════════════════════════════════════════════════════════════╝\n");
        
        int totalErreurs = erreursLex.size() + erreursSyn.size();
        
        if (totalErreurs == 0) {
            resultat.append("\n✅ SUCCÈS: Le programme est correct!\n");
            resultat.append("  • Analyse lexicale: OK\n");
            resultat.append("  • Analyse syntaxique: OK\n");
            lblStatut.setText("✅ Compilation réussie - 0 erreur");
            lblStatut.setForeground(new Color(152, 195, 121));
        } else {
            resultat.append("\n❌ ÉCHEC: ").append(totalErreurs).append(" erreur(s) détectée(s)\n");
            resultat.append("  • Erreurs lexicales: ").append(erreursLex.size()).append("\n");
            resultat.append("  • Erreurs syntaxiques: ").append(erreursSyn.size()).append("\n");
            lblStatut.setText("❌ Compilation échouée - " + totalErreurs + " erreur(s)");
            lblStatut.setForeground(new Color(224, 108, 117));
        }
        
        resultat.append("\n" + "═".repeat(60) + "\n");
        
        zoneResultat.setText(resultat.toString());
        zoneResultat.setCaretPosition(0);
    }
    
    private void ouvrirFichier() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".c");
            }
            public String getDescription() {
                return "Fichiers C (*.c)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            fichierCourant = fileChooser.getSelectedFile();
            
            try {
                StringBuilder contenu = new StringBuilder();
                BufferedReader reader = new BufferedReader(new FileReader(fichierCourant));
                String ligne;
                
                while ((ligne = reader.readLine()) != null) {
                    contenu.append(ligne).append("\n");
                }
                
                reader.close();
                
                zoneCode.setText(contenu.toString());
                lblFichier.setText("📄 " + fichierCourant.getName());
                lblStatut.setText("Fichier ouvert: " + fichierCourant.getName());
                lblStatut.setForeground(new Color(152, 195, 121));
                
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'ouverture du fichier:\n" + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void nouveauFichier() {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous créer un nouveau fichier?\nLes modifications non sauvegardées seront perdues.",
            "Nouveau fichier",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            zoneCode.setText("/* Nouveau programme */\n\nint main() {\n    \n    return 0;\n}");
            zoneResultat.setText("Cliquez sur 'Compiler' pour analyser votre code...");
            fichierCourant = null;
            lblFichier.setText("📄 Nouveau fichier");
            lblStatut.setText("Nouveau fichier créé");
            lblStatut.setForeground(new Color(171, 178, 191));
        }
    }
    
    private void sauvegarder() {
        if (fichierCourant == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".c");
                }
                public String getDescription() {
                    return "Fichiers C (*.c)";
                }
            });
            
            int result = fileChooser.showSaveDialog(this);
            
            if (result == JFileChooser.APPROVE_OPTION) {
                fichierCourant = fileChooser.getSelectedFile();
                
                // Ajouter l'extension .c si nécessaire
                if (!fichierCourant.getName().toLowerCase().endsWith(".c")) {
                    fichierCourant = new File(fichierCourant.getAbsolutePath() + ".c");
                }
            } else {
                return;
            }
        }
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fichierCourant));
            writer.write(zoneCode.getText());
            writer.close();
            
            lblFichier.setText("📄 " + fichierCourant.getName());
            lblStatut.setText("Fichier sauvegardé: " + fichierCourant.getName());
            lblStatut.setForeground(new Color(152, 195, 121));
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la sauvegarde du fichier:\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void effacer() {
        int response = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment effacer tout le code?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (response == JOptionPane.YES_OPTION) {
            zoneCode.setText("");
            zoneResultat.setText("Zone de résultat effacée.");
            lblStatut.setText("Code effacé");
            lblStatut.setForeground(new Color(171, 178, 191));
        }
    }
    
    public static void main(String[] args) {
        // Utiliser le Look and Feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Créer et afficher l'interface
        SwingUtilities.invokeLater(() -> {
            MiniCompilateur gui = new MiniCompilateur();
            gui.setVisible(true);
        });
    }
}