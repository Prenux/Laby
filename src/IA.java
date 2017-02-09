import java.util.ArrayList;
import java.util.Scanner;

//  Classe pour gerer les strateges d'intelligence artificielle
/*
Une fois implémenté cette première stratégie simple, vous pouvez essayer de faire d'autres versions utilisant des stratégies possiblement plus efficaces... Voici quelques idées simples :
        ·        Mémoriser les endroits où le personnage a été, et les directions prises de chaque position, afin de ne pas répéter le même déplacement (de préférence) ;
        ·        Le personnage choisit toujours de suivre les murs à sa gauche (en touchant le mur à sa main gauche mais sans sortir par l’entrée) ;
        Si vous avez implémenté plusieurs stratégies pour votre intelligence artificielle, proposez à l'utilisateur un choix de stratégie après qu'il a pressé 'o'.
*/
public class IA
{
    //Temps attente entre mouvements perso
    static final int SLEEPTIME = 1000;

    //initialiser openSet et closedSet
    ArrayList<Node> openSet = new ArrayList<>();
    ArrayList<Node> closedSet = new ArrayList<>();

    //declaration variables endCoord
    int endCoordX;
    int endCoordY;

    private Labyrinthe laby;
    private Personnage perso;
    private int vies;
    private int heigthCase;
    private int widthCase;

    IA(Labyrinthe l, Personnage p, int v, int h, int w)
    {
        laby = l;
        perso = p;
        vies = v;
        heigthCase = h;
        widthCase = w;
    }

    //Fonction gestion des strategies IA, retourne true si victoire, false si defaite
    public boolean gestionIA()
    {
        //User input pour choix strategie IA
        Scanner reader = new Scanner(System.in);
        char c;
        int posX, posY;

        //Message de choix de strategie
        System.out.println("SVP, choisissez une strategie d'intelligence artificielle:");
        System.out.println("\tDeplacements au hasard     \th");
        System.out.println("\tChemin le plus rapide      \tr");

        while (true) {
            c = reader.next().charAt(0);

            if (c == 'h') { return deplacementsHasard(); }
            if (c == 'r') { return cheminRapide(); }
        }

    }

    public boolean deplacementsHasard()
    {
        int random, posX, posY;
        boolean entree, sortie;
        String deplacement = "aucun deplacement effectue";

        while (true) {
            //Message statut
            laby.afficheStatut(perso, vies);
            System.out.println("L'ordinateur controle le personnage au hasard");
            System.out.println("Dernier deplacement:\t" + deplacement + ".");
            sleep(SLEEPTIME); //patienter 3 secondes avant prochain deplacement

            //coordonnees du personnage
            posX = perso.getPosX();
            posY = perso.getPosY();
            entree = false;

            //nbr entre 0 et 3 pour deplacement (4 choix)
            random = (int) (Math.random() * 4);

            //Si 0 --> deplacer gauche
            if (random == 0) {
                deplacement = "gauche";
                if (!laby.aMuretAGauche(posX, posY) && !(entree = laby.aEntreeAGauche(posX, posY))) {
                    perso.deplacerGauche();
                }

                //Si 1 --> deplacer haut
            } else if (random == 1) {
                deplacement = "haut";
                if (!laby.aMuretEnHaut(posX, posY)) perso.deplacerHaut();

                //Si 2 --> deplacer droite
            } else if (random == 2) {
                deplacement = "droite";
                if (laby.aSortieADroite(posX, posY)) return true;
                else if (!laby.aMuretADroite(posX, posY)) perso.deplacerDroite();

                //Si 3 --> deplacer bas
            } else if (random == 3) {
                deplacement = "bas";
                if (!laby.aMuretEnBas(posX, posY)) perso.deplacerBas();
            }

            //Test si personnage doit perdre une vie
            if (posX == perso.getPosX() && posY == perso.getPosY() && !entree) {
                perso.perdreVie();
                if (perso.getVies() == 0) return false;
            }
        }

    }


    public boolean cheminRapide()
    {
            //Coordonnees du node "start"
            int IAposX = perso.getPosX();
            int IAposY = perso.getPosY();

            //Coordonnees du node "end"
            endCoordX = widthCase -1;
            endCoordY = laby.getCoordOuverture();

            //distance vol d'oiseau start --> end
            int minGlobalPath = getVectorH(IAposX, IAposY);

            //Creer noeud debut (uniqueID == 0)
            Node start = new Node(null, IAposX, IAposY, " ", 0, minGlobalPath);

            openSet.add(start); //ne sait pas si marche
            Node current;

            while (openSet.size() > 0) {
                current = getLowestFscore(openSet);

                //Si current est la fin on a trouver un + court chemin
                if (current.posX == endCoordX && current.posY == endCoordY) {
                    String chemin = reconstructPath(current);
                    deplacerPersoRapide(chemin);
                    return true;
                }

                openSet.remove(current);
                closedSet.add(current);

                getNeighbour(current);
            }
            // On n'a pas trouver de plus court chemin
            return false;

    }

    //deplacer le personnage dans le labyrinthe
    public void deplacerPersoRapide(String chemin) {
        for (int i = 0; i < chemin.length(); i++) {
            //Message statut
            laby.afficheStatut(perso, vies);
            System.out.println("L'ordinateur a trouve le chemin le plus rapide");
            sleep(SLEEPTIME); //patienter 3 secondes avant prochain deplacement

            //coordonnees du personnage
            int posX = perso.getPosX();
            int posY = perso.getPosY();

            //Si g --> deplacer gauche
            if (chemin.charAt(i) == 'g') perso.deplacerGauche();

            //Si h --> deplacer haut
            if (chemin.charAt(i) == 'h') perso.deplacerHaut();

            //Si d --> deplacer droite
            if (chemin.charAt(i) == 'd') perso.deplacerDroite();

            //Si b --> deplacer bas
            if (chemin.charAt(i) == 'b') perso.deplacerBas();
        }
    }


    //reconstruire le chemin
    public String reconstructPath(Node n) {
        if (n.revDir == " ") return "";
        return reconstructPath(n.previous) + n.revDir;
    }

    public void getNeighbour(Node n) {
        System.out.println(n.posX + " " + n.posY + "\n");
        //muret gauche
        if (!laby.aEntreeAGauche(n.posX, n.posY) && !laby.aMuretAGauche(n.posX, n.posY) && !isInClosedSet(n.posX-1,n.posY)) {
            openSet.add(new Node(n, n.posX-1, n.posY, "g", n.gScore++, getVectorH(n.posX-1,n.posY)));
        }

        //muret haut
        if (!laby.aMuretEnHaut(n.posX, n.posY) && !isInClosedSet(n.posX,n.posY-1)) {
            openSet.add(new Node(n, n.posX, n.posY-1, "h", n.gScore++, getVectorH(n.posX,n.posY-1)));
        }

        //muret droite
        if (!laby.aMuretADroite(n.posX, n.posY) && !isInClosedSet(n.posX+1,n.posY)) {
            openSet.add(new Node(n, n.posX+1, n.posY, "d", n.gScore++, getVectorH(n.posX+1,n.posY)));
        }

        //muret bas
        if (!laby.aMuretEnBas(n.posX, n.posY) && !isInClosedSet(n.posX,n.posY+1)) {
            openSet.add(new Node(n, n.posX, n.posY+1, "b", n.gScore++, getVectorH(n.posX,n.posY+1)));
        }
    }

    public boolean isInClosedSet(int posX, int posY) {
        for (Node n : closedSet) {
            if (n.posX == posX && n.posY == posY) return true;
        }
        return false;
    }

    public int getVectorH(int posX, int posY) {
        return Math.abs(endCoordX - posX) + Math.abs(endCoordY - posY);
    }

    public Node getLowestFscore(ArrayList<Node> al) {
        double minFscore = Double.POSITIVE_INFINITY;
        Node current = null;

        for (Node n : al) {
            if(n.fScore < minFscore) {
                minFscore = n.fScore;
                current = n;
            }
        }

        return current;
    }

    //Sleep entre deplacements pour montrer les differents choix
    public static void sleep(long millisecondes)
    {
        try {
            Thread.sleep(millisecondes);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrompu");
        }
    }
}
