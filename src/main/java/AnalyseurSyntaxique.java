// AnalyseurSyntaxique.java
import java.util.ArrayList;
import java.util.List;

public class AnalyseurSyntaxique {
    private List<Token> tokens;
    private int position;
    private Token tokenCourant;
    private List<String> erreurs;
    
    public AnalyseurSyntaxique(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.erreurs = new ArrayList<>();
        if (tokens.size() > 0) {
            this.tokenCourant = tokens.get(0);
        }
    }
    
    public boolean analyser() {
        try {
            programme();
            
            if (tokenCourant.getType() != TypeToken.FIN_FICHIER) {
                ajouterErreur("Tokens non attendus après la fin du programme");
            }
            
            return erreurs.size() == 0;
        } catch (Exception e) {
            ajouterErreur("Erreur fatale: " + e.getMessage());
            return false;
        }
    }
    
    // Programme ::= {Declaration | Fonction}
    private void programme() {
        while (tokenCourant.getType() != TypeToken.FIN_FICHIER) {
            if (estTypeDeclaration()) {
                // Regarder si c'est une fonction ou une déclaration
                int savePosition = position;
                Token saveToken = tokenCourant;
                
                avancer(); // Passer le type
                
                if (tokenCourant.getType() == TypeToken.IDENTIFICATEUR) {
                    avancer();
                    
                    // Si '(' alors c'est une fonction
                    if (tokenCourant.getType() == TypeToken.PARENTHESE_OUVRANTE) {
                        // Revenir en arrière et analyser comme fonction
                        position = savePosition;
                        tokenCourant = saveToken;
                        fonction();
                    } else {
                        // Revenir en arrière et analyser comme déclaration
                        position = savePosition;
                        tokenCourant = saveToken;
                        declarationVariable();
                    }
                } else {
                    position = savePosition;
                    tokenCourant = saveToken;
                    declarationVariable();
                }
            } else {
                instruction();
            }
        }
    }
    
    // Fonction ::= Type Identificateur ( [Parametres] ) Bloc
    private void fonction() {
        type();
        
        if (!accepter(TypeToken.IDENTIFICATEUR)) {
            ajouterErreur("Identificateur attendu pour le nom de la fonction");
            recuperer();
            return;
        }
        
        if (!accepter(TypeToken.PARENTHESE_OUVRANTE)) {
            ajouterErreur("'(' attendu après le nom de la fonction");
            recuperer();
            return;
        }
        
        // Paramètres optionnels
        if (tokenCourant.getType() != TypeToken.PARENTHESE_FERMANTE) {
            parametres();
        }
        
        if (!accepter(TypeToken.PARENTHESE_FERMANTE)) {
            ajouterErreur("')' attendu après les paramètres");
            recuperer();
        }
        
        // Corps de la fonction (bloc)
        if (tokenCourant.getType() == TypeToken.ACCOLADE_OUVRANTE) {
            bloc();
        } else {
            ajouterErreur("Bloc attendu pour le corps de la fonction");
            recuperer();
        }
    }
    
    // Parametres ::= Type Identificateur { , Type Identificateur }
    private void parametres() {
        type();
        
        if (!accepter(TypeToken.IDENTIFICATEUR)) {
            ajouterErreur("Identificateur attendu pour le paramètre");
            return;
        }
        
        while (tokenCourant.getType() == TypeToken.VIRGULE) {
            avancer();
            type();
            
            if (!accepter(TypeToken.IDENTIFICATEUR)) {
                ajouterErreur("Identificateur attendu après la virgule");
                return;
            }
        }
    }
    
    // DeclarationVariable ::= Type Identificateur [Affectation] { , Identificateur [Affectation] } ;
    private void declarationVariable() {
        type();
        
        if (!accepter(TypeToken.IDENTIFICATEUR)) {
            ajouterErreur("Identificateur attendu après le type");
            recuperer();
            return;
        }
        
        // Affectation optionnelle
        if (tokenCourant.getType() == TypeToken.AFFECTATION) {
            avancer();
            expression();
        }
        
        // Déclarations multiples (ex: int x, y, z;)
        while (tokenCourant.getType() == TypeToken.VIRGULE) {
            avancer();
            
            if (!accepter(TypeToken.IDENTIFICATEUR)) {
                ajouterErreur("Identificateur attendu après la virgule");
                recuperer();
                return;
            }
            
            // Affectation optionnelle
            if (tokenCourant.getType() == TypeToken.AFFECTATION) {
                avancer();
                expression();
            }
        }
        
        if (!accepter(TypeToken.POINT_VIRGULE)) {
            ajouterErreur("Point-virgule attendu après la déclaration");
            recuperer();
        }
    }
    
    // Type ::= int | float | char | double | void
    private void type() {
        if (tokenCourant.getType() == TypeToken.INT ||
            tokenCourant.getType() == TypeToken.FLOAT ||
            tokenCourant.getType() == TypeToken.CHAR ||
            tokenCourant.getType() == TypeToken.DOUBLE ||
            tokenCourant.getType() == TypeToken.VOID) {
            avancer();
        } else {
            ajouterErreur("Type attendu (int, float, char, double, void)");
            recuperer();
        }
    }
    
    // Instruction ::= InstructionSimple | InstructionSwitch | Bloc
    private void instruction() {
        if (tokenCourant.getType() == TypeToken.SWITCH) {
            instructionSwitch();
        } else if (tokenCourant.getType() == TypeToken.ACCOLADE_OUVRANTE) {
            bloc();
        } else {
            instructionSimple();
        }
    }
    
    // InstructionSimple ::= Affectation | Incrementation | Decrementation | Break | Continue | Return
    private void instructionSimple() {
        if (tokenCourant.getType() == TypeToken.IDENTIFICATEUR) {
            avancer();
            
            // Affectation ou affectation composée
            if (tokenCourant.getType() == TypeToken.AFFECTATION ||
                tokenCourant.getType() == TypeToken.PLUS_EGAL ||
                tokenCourant.getType() == TypeToken.MOINS_EGAL ||
                tokenCourant.getType() == TypeToken.MULT_EGAL ||
                tokenCourant.getType() == TypeToken.DIV_EGAL) {
                avancer();
                expression();
            }
            // Incrémentation ou décrémentation
            else if (tokenCourant.getType() == TypeToken.INCREMENT ||
                     tokenCourant.getType() == TypeToken.DECREMENT) {
                avancer();
            }
            
            if (!accepter(TypeToken.POINT_VIRGULE)) {
                ajouterErreur("Point-virgule attendu après l'instruction");
                recuperer();
            }
        } else if (tokenCourant.getType() == TypeToken.INCREMENT ||
                   tokenCourant.getType() == TypeToken.DECREMENT) {
            avancer();
            if (!accepter(TypeToken.IDENTIFICATEUR)) {
                ajouterErreur("Identificateur attendu après " + tokens.get(position - 1).getValeur());
            }
            if (!accepter(TypeToken.POINT_VIRGULE)) {
                ajouterErreur("Point-virgule attendu");
                recuperer();
            }
        } else if (tokenCourant.getType() == TypeToken.BREAK ||
                   tokenCourant.getType() == TypeToken.CONTINUE) {
            avancer();
            if (!accepter(TypeToken.POINT_VIRGULE)) {
                ajouterErreur("Point-virgule attendu après " + tokens.get(position - 1).getValeur());
                recuperer();
            }
        } else if (tokenCourant.getType() == TypeToken.RETURN) {
            avancer();
            if (tokenCourant.getType() != TypeToken.POINT_VIRGULE) {
                expression();
            }
            if (!accepter(TypeToken.POINT_VIRGULE)) {
                ajouterErreur("Point-virgule attendu après return");
                recuperer();
            }
        } else {
            ajouterErreur("Instruction non reconnue");
            recuperer();
        }
    }
    
    // InstructionSwitch ::= switch ( Expression ) { {Case} [Default] }
    private void instructionSwitch() {
        if (!accepter(TypeToken.SWITCH)) {
            ajouterErreur("'switch' attendu");
            return;
        }
        
        if (!accepter(TypeToken.PARENTHESE_OUVRANTE)) {
            ajouterErreur("'(' attendu après switch");
            recuperer();
            return;
        }
        
        expression();
        
        if (!accepter(TypeToken.PARENTHESE_FERMANTE)) {
            ajouterErreur("')' attendu après l'expression du switch");
            recuperer();
        }
        
        if (!accepter(TypeToken.ACCOLADE_OUVRANTE)) {
            ajouterErreur("'{' attendu pour le corps du switch");
            recuperer();
            return;
        }
        
        // Cases
        while (tokenCourant.getType() == TypeToken.CASE) {
            instructionCase();
        }
        
        // Default optionnel
        if (tokenCourant.getType() == TypeToken.DEFAULT) {
            instructionDefault();
        }
        
        if (!accepter(TypeToken.ACCOLADE_FERMANTE)) {
            ajouterErreur("'}' attendu pour fermer le switch");
            recuperer();
        }
    }
    
    // Case ::= case Constante : {Instruction}
    private void instructionCase() {
        if (!accepter(TypeToken.CASE)) {
            ajouterErreur("'case' attendu");
            return;
        }
        
        // Constante (nombre ou caractère)
        if (tokenCourant.getType() == TypeToken.NOMBRE_ENTIER ||
            tokenCourant.getType() == TypeToken.CARACTERE ||
            tokenCourant.getType() == TypeToken.IDENTIFICATEUR) {
            avancer();
        } else {
            ajouterErreur("Constante attendue après case");
            recuperer();
            return;
        }
        
        if (!accepter(TypeToken.DEUX_POINTS)) {
            ajouterErreur("':' attendu après la valeur du case");
            recuperer();
        }
        
        // Instructions du case
        while (tokenCourant.getType() != TypeToken.CASE &&
               tokenCourant.getType() != TypeToken.DEFAULT &&
               tokenCourant.getType() != TypeToken.ACCOLADE_FERMANTE &&
               tokenCourant.getType() != TypeToken.FIN_FICHIER) {
            instruction();
        }
    }
    
    // Default ::= default : {Instruction}
    private void instructionDefault() {
        if (!accepter(TypeToken.DEFAULT)) {
            ajouterErreur("'default' attendu");
            return;
        }
        
        if (!accepter(TypeToken.DEUX_POINTS)) {
            ajouterErreur("':' attendu après default");
            recuperer();
        }
        
        // Instructions du default
        while (tokenCourant.getType() != TypeToken.ACCOLADE_FERMANTE &&
               tokenCourant.getType() != TypeToken.FIN_FICHIER) {
            instruction();
        }
    }
    
    // Bloc ::= { {Declaration | Instruction} }
    private void bloc() {
        if (!accepter(TypeToken.ACCOLADE_OUVRANTE)) {
            ajouterErreur("'{' attendu");
            return;
        }
        
        while (tokenCourant.getType() != TypeToken.ACCOLADE_FERMANTE &&
               tokenCourant.getType() != TypeToken.FIN_FICHIER) {
            if (estTypeDeclaration()) {
                declarationVariable();
            } else {
                instruction();
            }
        }
        
        if (!accepter(TypeToken.ACCOLADE_FERMANTE)) {
            ajouterErreur("'}' attendu");
            recuperer();
        }
    }
    
    // Expression ::= ExpressionLogique
    private void expression() {
        expressionLogique();
    }
    
    // ExpressionLogique ::= ExpressionComparaison { (&&|||) ExpressionComparaison }
    private void expressionLogique() {
        expressionComparaison();
        
        while (tokenCourant.getType() == TypeToken.ET_LOGIQUE ||
               tokenCourant.getType() == TypeToken.OU_LOGIQUE) {
            avancer();
            expressionComparaison();
        }
    }
    
    // ExpressionComparaison ::= ExpressionArithmetique { (==|!=|<|>|<=|>=) ExpressionArithmetique }
    private void expressionComparaison() {
        expressionArithmetique();
        
        while (tokenCourant.getType() == TypeToken.EGAL ||
               tokenCourant.getType() == TypeToken.DIFFERENT ||
               tokenCourant.getType() == TypeToken.INFERIEUR ||
               tokenCourant.getType() == TypeToken.SUPERIEUR ||
               tokenCourant.getType() == TypeToken.INFERIEUR_EGAL ||
               tokenCourant.getType() == TypeToken.SUPERIEUR_EGAL) {
            avancer();
            expressionArithmetique();
        }
    }
    
    // ExpressionArithmetique ::= Terme { (+|-) Terme }
    private void expressionArithmetique() {
        terme();
        
        while (tokenCourant.getType() == TypeToken.PLUS ||
               tokenCourant.getType() == TypeToken.MOINS) {
            avancer();
            terme();
        }
    }
    
    // Terme ::= Facteur { (*|/|%) Facteur }
    private void terme() {
        facteur();
        
        while (tokenCourant.getType() == TypeToken.MULTIPLICATION ||
               tokenCourant.getType() == TypeToken.DIVISION ||
               tokenCourant.getType() == TypeToken.MODULO) {
            avancer();
            facteur();
        }
    }
    
    // Facteur ::= Nombre | Identificateur | Caractere | ( Expression ) | !Facteur | -Facteur
    private void facteur() {
        if (tokenCourant.getType() == TypeToken.NOMBRE_ENTIER ||
            tokenCourant.getType() == TypeToken.NOMBRE_REEL ||
            tokenCourant.getType() == TypeToken.CARACTERE ||
            tokenCourant.getType() == TypeToken.IDENTIFICATEUR) {
            avancer();
        } else if (tokenCourant.getType() == TypeToken.PARENTHESE_OUVRANTE) {
            avancer();
            expression();
            if (!accepter(TypeToken.PARENTHESE_FERMANTE)) {
                ajouterErreur("')' attendu");
                recuperer();
            }
        } else if (tokenCourant.getType() == TypeToken.NON_LOGIQUE ||
                   tokenCourant.getType() == TypeToken.MOINS) {
            avancer();
            facteur();
        } else {
            ajouterErreur("Facteur invalide");
            recuperer();
        }
    }
    
    // Méthodes utilitaires
    private boolean accepter(TypeToken type) {
        if (tokenCourant.getType() == type) {
            avancer();
            return true;
        }
        return false;
    }
    
    private void avancer() {
        position++;
        if (position < tokens.size()) {
            tokenCourant = tokens.get(position);
        }
    }
    
    private boolean estTypeDeclaration() {
        return tokenCourant.getType() == TypeToken.INT ||
               tokenCourant.getType() == TypeToken.FLOAT ||
               tokenCourant.getType() == TypeToken.CHAR ||
               tokenCourant.getType() == TypeToken.DOUBLE ||
               tokenCourant.getType() == TypeToken.VOID;
    }
    
    private void ajouterErreur(String message) {
        String erreur = "Erreur syntaxique ligne " + tokenCourant.getLigne() + 
                       ", col " + tokenCourant.getColonne() + ": " + message +
                       " (token actuel: " + tokenCourant.getValeur() + ")";
        erreurs.add(erreur);
    }
    
    private void recuperer() {
        // Avance jusqu'au prochain point-virgule ou accolade fermante pour continuer l'analyse
        while (tokenCourant.getType() != TypeToken.POINT_VIRGULE &&
               tokenCourant.getType() != TypeToken.ACCOLADE_FERMANTE &&
               tokenCourant.getType() != TypeToken.FIN_FICHIER) {
            avancer();
        }
        
        if (tokenCourant.getType() == TypeToken.POINT_VIRGULE ||
            tokenCourant.getType() == TypeToken.ACCOLADE_FERMANTE) {
            avancer();
        }
    }
    
    public List<String> getErreurs() {
        return erreurs;
    }
}