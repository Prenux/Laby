/**
 * Classe qui contient les fonctions des noeuds qui correspondent aux cases
 * du labyrinthe pour IA meilleur chemin
 */
public class Node {
    public Node previous;
    public String revDir; //reverse direction de ou on arrive (start = ' ')
    public int gScore;
    public int hScore;
    public int fScore;
    public int posX;
    public int posY;

    public Node(Node p, int posX, int posY, String rd, int gScore, int hScore) {
        this.previous = p;
        this.posX = posX;
        this.posY = posY;
        this.revDir = rd;
        this.gScore = gScore;
        this.hScore = hScore;
        this.fScore = gScore + hScore;
    }
}
