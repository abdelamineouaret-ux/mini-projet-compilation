// Token.java

public class Token {
    private TypeToken type;
    private String valeur;
    private int ligne;
    private int colonne;
    
    public Token(TypeToken type, String valeur, int ligne, int colonne) {
        this.type = type;
        this.valeur = valeur;
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    public TypeToken getType() {
        return type;
    }
    
    public String getValeur() {
        return valeur;
    }
    
    public int getLigne() {
        return ligne;
    }
    
    public int getColonne() {
        return colonne;
    }
    
    @Override
    public String toString() {
        return "Token{" +
               "type=" + type +
               ", valeur='" + valeur + '\'' +
               ", ligne=" + ligne +
               ", colonne=" + colonne +
               '}';
    }
}

// TypeToken.java - Enumération des types de tokens
enum TypeToken {
    // Mots-clés du langage C
    INT, FLOAT, CHAR, DOUBLE, VOID,
    IF, ELSE, WHILE, DO, FOR,
    SWITCH, CASE, DEFAULT, BREAK, CONTINUE,
    RETURN,
    
    // Mots-clés personnalisés
    OUARET, ABDELAMINE,
    
    // Identificateurs et littéraux
    IDENTIFICATEUR,
    NOMBRE_ENTIER,
    NOMBRE_REEL,
    CARACTERE,
    CHAINE,
    
    // Opérateurs arithmétiques
    PLUS,           // +
    MOINS,          // -
    MULTIPLICATION, // *
    DIVISION,       // /
    MODULO,         // %
    INCREMENT,      // ++
    DECREMENT,      // --
    
    // Opérateurs de comparaison
    EGAL,           // ==
    DIFFERENT,      // !=
    INFERIEUR,      // <
    SUPERIEUR,      // >
    INFERIEUR_EGAL, // <=
    SUPERIEUR_EGAL, // >=
    
    // Opérateurs logiques
    ET_LOGIQUE,     // &&
    OU_LOGIQUE,     // ||
    NON_LOGIQUE,    // !
    
    // Opérateurs d'affectation
    AFFECTATION,    // =
    PLUS_EGAL,      // +=
    MOINS_EGAL,     // -=
    MULT_EGAL,      // *=
    DIV_EGAL,       // /=
    
    // Délimiteurs
    PARENTHESE_OUVRANTE,  // (
    PARENTHESE_FERMANTE,  // )
    ACCOLADE_OUVRANTE,    // {
    ACCOLADE_FERMANTE,    // }
    CROCHET_OUVRANT,      // [
    CROCHET_FERMANT,      // ]
    POINT_VIRGULE,        // ;
    VIRGULE,              // ,
    DEUX_POINTS,          // :
    
    // Spéciaux
    FIN_FICHIER,
    ERREUR
}