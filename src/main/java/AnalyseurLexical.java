// AnalyseurLexical.java
import java.util.ArrayList;
import java.util.List;

public class AnalyseurLexical {
    private String codeSource;
    private int position;
    private int ligne;
    private int colonne;
    private List<String> erreurs;
    
    public AnalyseurLexical(String codeSource) {
        this.codeSource = codeSource;
        this.position = 0;
        this.ligne = 1;
        this.colonne = 1;
        this.erreurs = new ArrayList<>();
    }
    
    public List<Token> analyser() {
        List<Token> tokens = new ArrayList<>();
        
        while (position < codeSource.length()) {
            ignorerEspacesEtCommentaires();
            
            if (position >= codeSource.length()) {
                break;
            }
            
            Token token = lireProchainToken();
            if (token != null) {
                tokens.add(token);
            }
        }
        
        tokens.add(new Token(TypeToken.FIN_FICHIER, "", ligne, colonne));
        return tokens;
    }
    
    private Token lireProchainToken() {
        char caractereActuel = codeSource.charAt(position);
        int ligneDeb = ligne;
        int colonneDeb = colonne;
        
        // Identificateurs et mots-clés
        if (estLettre(caractereActuel) || caractereActuel == '_') {
            return lireIdentificateurOuMotCle(ligneDeb, colonneDeb);
        }
        
        // Nombres
        if (estChiffre(caractereActuel)) {
            return lireNombre(ligneDeb, colonneDeb);
        }
        
        // Caractères entre apostrophes
        if (caractereActuel == '\'') {
            return lireCaractere(ligneDeb, colonneDeb);
        }
        
        // Chaînes de caractères
        if (caractereActuel == '"') {
            return lireChaine(ligneDeb, colonneDeb);
        }
        
        // Opérateurs et délimiteurs
        return lireOperateurOuDelimiteur(ligneDeb, colonneDeb);
    }
    
    private Token lireIdentificateurOuMotCle(int ligneDeb, int colonneDeb) {
        StringBuilder sb = new StringBuilder();
        
        while (position < codeSource.length()) {
            char c = codeSource.charAt(position);
            if (estLettre(c) || estChiffre(c) || c == '_') {
                sb.append(c);
                avancer();
            } else {
                break;
            }
        }
        
        String lexeme = sb.toString();
        TypeToken type = obtenirTypeMotCle(lexeme);
        
        if (type == null) {
            type = TypeToken.IDENTIFICATEUR;
        }
        
        return new Token(type, lexeme, ligneDeb, colonneDeb);
    }
    
    private TypeToken obtenirTypeMotCle(String lexeme) {
        // Mots-clés personnalisés
        if (comparerChaines(lexeme, "ouaret") || comparerChaines(lexeme, "OUARET")) {
            return TypeToken.OUARET;
        }
        if (comparerChaines(lexeme, "abdelamine") || comparerChaines(lexeme, "ABDELAMINE")) {
            return TypeToken.ABDELAMINE;
        }
        
        // Types de données
        if (comparerChaines(lexeme, "int")) return TypeToken.INT;
        if (comparerChaines(lexeme, "float")) return TypeToken.FLOAT;
        if (comparerChaines(lexeme, "char")) return TypeToken.CHAR;
        if (comparerChaines(lexeme, "double")) return TypeToken.DOUBLE;
        if (comparerChaines(lexeme, "void")) return TypeToken.VOID;
        
        // Structures de contrôle
        if (comparerChaines(lexeme, "if")) return TypeToken.IF;
        if (comparerChaines(lexeme, "else")) return TypeToken.ELSE;
        if (comparerChaines(lexeme, "while")) return TypeToken.WHILE;
        if (comparerChaines(lexeme, "do")) return TypeToken.DO;
        if (comparerChaines(lexeme, "for")) return TypeToken.FOR;
        if (comparerChaines(lexeme, "switch")) return TypeToken.SWITCH;
        if (comparerChaines(lexeme, "case")) return TypeToken.CASE;
        if (comparerChaines(lexeme, "default")) return TypeToken.DEFAULT;
        if (comparerChaines(lexeme, "break")) return TypeToken.BREAK;
        if (comparerChaines(lexeme, "continue")) return TypeToken.CONTINUE;
        if (comparerChaines(lexeme, "return")) return TypeToken.RETURN;
        
        return null;
    }
    
    private Token lireNombre(int ligneDeb, int colonneDeb) {
        StringBuilder sb = new StringBuilder();
        boolean estReel = false;
        
        while (position < codeSource.length()) {
            char c = codeSource.charAt(position);
            
            if (estChiffre(c)) {
                sb.append(c);
                avancer();
            } else if (c == '.' && !estReel) {
                estReel = true;
                sb.append(c);
                avancer();
            } else {
                break;
            }
        }
        
        TypeToken type = estReel ? TypeToken.NOMBRE_REEL : TypeToken.NOMBRE_ENTIER;
        return new Token(type, sb.toString(), ligneDeb, colonneDeb);
    }
    
    private Token lireCaractere(int ligneDeb, int colonneDeb) {
        avancer(); // Sauter '
        
        if (position >= codeSource.length()) {
            erreurs.add("Erreur ligne " + ligneDeb + ", col " + colonneDeb + ": Caractère non terminé");
            return new Token(TypeToken.ERREUR, "'", ligneDeb, colonneDeb);
        }
        
        char c = codeSource.charAt(position);
        avancer();
        
        if (position >= codeSource.length() || codeSource.charAt(position) != '\'') {
            erreurs.add("Erreur ligne " + ligneDeb + ", col " + colonneDeb + ": Caractère non terminé correctement");
            return new Token(TypeToken.ERREUR, "'" + c, ligneDeb, colonneDeb);
        }
        
        avancer(); // Sauter '
        return new Token(TypeToken.CARACTERE, String.valueOf(c), ligneDeb, colonneDeb);
    }
    
    private Token lireChaine(int ligneDeb, int colonneDeb) {
        StringBuilder sb = new StringBuilder();
        avancer(); // Sauter "
        
        while (position < codeSource.length()) {
            char c = codeSource.charAt(position);
            
            if (c == '"') {
                avancer();
                return new Token(TypeToken.CHAINE, sb.toString(), ligneDeb, colonneDeb);
            } else if (c == '\\') {
                avancer();
                if (position < codeSource.length()) {
                    sb.append(codeSource.charAt(position));
                    avancer();
                }
            } else {
                sb.append(c);
                avancer();
            }
        }
        
        erreurs.add("Erreur ligne " + ligneDeb + ", col " + colonneDeb + ": Chaîne non terminée");
        return new Token(TypeToken.ERREUR, sb.toString(), ligneDeb, colonneDeb);
    }
    
    private Token lireOperateurOuDelimiteur(int ligneDeb, int colonneDeb) {
        char c = codeSource.charAt(position);
        avancer();
        
        // Opérateurs à deux caractères
        if (position < codeSource.length()) {
            char suivant = codeSource.charAt(position);
            String deuxCaracteres = String.valueOf(c) + suivant;
            
            if (comparerChaines(deuxCaracteres, "++")) {
                avancer();
                return new Token(TypeToken.INCREMENT, "++", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "--")) {
                avancer();
                return new Token(TypeToken.DECREMENT, "--", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "==")) {
                avancer();
                return new Token(TypeToken.EGAL, "==", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "!=")) {
                avancer();
                return new Token(TypeToken.DIFFERENT, "!=", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "<=")) {
                avancer();
                return new Token(TypeToken.INFERIEUR_EGAL, "<=", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, ">=")) {
                avancer();
                return new Token(TypeToken.SUPERIEUR_EGAL, ">=", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "&&")) {
                avancer();
                return new Token(TypeToken.ET_LOGIQUE, "&&", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "||")) {
                avancer();
                return new Token(TypeToken.OU_LOGIQUE, "||", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "+=")) {
                avancer();
                return new Token(TypeToken.PLUS_EGAL, "+=", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "-=")) {
                avancer();
                return new Token(TypeToken.MOINS_EGAL, "-=", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "*=")) {
                avancer();
                return new Token(TypeToken.MULT_EGAL, "*=", ligneDeb, colonneDeb);
            }
            if (comparerChaines(deuxCaracteres, "/=")) {
                avancer();
                return new Token(TypeToken.DIV_EGAL, "/=", ligneDeb, colonneDeb);
            }
        }
        
        // Opérateurs à un caractère
        if (c == '+') return new Token(TypeToken.PLUS, "+", ligneDeb, colonneDeb);
        if (c == '-') return new Token(TypeToken.MOINS, "-", ligneDeb, colonneDeb);
        if (c == '*') return new Token(TypeToken.MULTIPLICATION, "*", ligneDeb, colonneDeb);
        if (c == '/') return new Token(TypeToken.DIVISION, "/", ligneDeb, colonneDeb);
        if (c == '%') return new Token(TypeToken.MODULO, "%", ligneDeb, colonneDeb);
        if (c == '<') return new Token(TypeToken.INFERIEUR, "<", ligneDeb, colonneDeb);
        if (c == '>') return new Token(TypeToken.SUPERIEUR, ">", ligneDeb, colonneDeb);
        if (c == '=') return new Token(TypeToken.AFFECTATION, "=", ligneDeb, colonneDeb);
        if (c == '!') return new Token(TypeToken.NON_LOGIQUE, "!", ligneDeb, colonneDeb);
        
        // Délimiteurs
        if (c == '(') return new Token(TypeToken.PARENTHESE_OUVRANTE, "(", ligneDeb, colonneDeb);
        if (c == ')') return new Token(TypeToken.PARENTHESE_FERMANTE, ")", ligneDeb, colonneDeb);
        if (c == '{') return new Token(TypeToken.ACCOLADE_OUVRANTE, "{", ligneDeb, colonneDeb);
        if (c == '}') return new Token(TypeToken.ACCOLADE_FERMANTE, "}", ligneDeb, colonneDeb);
        if (c == '[') return new Token(TypeToken.CROCHET_OUVRANT, "[", ligneDeb, colonneDeb);
        if (c == ']') return new Token(TypeToken.CROCHET_FERMANT, "]", ligneDeb, colonneDeb);
        if (c == ';') return new Token(TypeToken.POINT_VIRGULE, ";", ligneDeb, colonneDeb);
        if (c == ',') return new Token(TypeToken.VIRGULE, ",", ligneDeb, colonneDeb);
        if (c == ':') return new Token(TypeToken.DEUX_POINTS, ":", ligneDeb, colonneDeb);
        
        erreurs.add("Erreur ligne " + ligneDeb + ", col " + colonneDeb + ": Caractère non reconnu: '" + c + "'");
        return new Token(TypeToken.ERREUR, String.valueOf(c), ligneDeb, colonneDeb);
    }
    
    private void ignorerEspacesEtCommentaires() {
        while (position < codeSource.length()) {
            char c = codeSource.charAt(position);
            
            // Espaces blancs
            if (c == ' ' || c == '\t' || c == '\r') {
                avancer();
                continue;
            }
            
            if (c == '\n') {
                avancer();
                ligne++;
                colonne = 1;
                continue;
            }
            
            // Commentaires
            if (c == '/' && position + 1 < codeSource.length()) {
                char suivant = codeSource.charAt(position + 1);
                
                // Commentaire ligne //
                if (suivant == '/') {
                    while (position < codeSource.length() && codeSource.charAt(position) != '\n') {
                        avancer();
                    }
                    continue;
                }
                
                // Commentaire bloc /* */
                if (suivant == '*') {
                    avancer(); // /
                    avancer(); // *
                    
                    while (position + 1 < codeSource.length()) {
                        if (codeSource.charAt(position) == '*' && codeSource.charAt(position + 1) == '/') {
                            avancer(); // *
                            avancer(); // /
                            break;
                        }
                        if (codeSource.charAt(position) == '\n') {
                            ligne++;
                            colonne = 0;
                        }
                        avancer();
                    }
                    continue;
                }
            }
            
            break;
        }
    }
    
    private void avancer() {
        if (position < codeSource.length()) {
            position++;
            colonne++;
        }
    }
    
    private boolean estLettre(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }
    
    private boolean estChiffre(char c) {
        return c >= '0' && c <= '9';
    }
    
    private boolean comparerChaines(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        }
        
        for (int i = 0; i < s1.length(); i++) {
            if (s1.charAt(i) != s2.charAt(i)) {
                return false;
            }
        }
        
        return true;
    }
    
    public List<String> getErreurs() {
        return erreurs;
    }
}