
public class Labyrinthe
{
    private static final int LMURET = 8;
    private static final int HMURET = 4;
    private int heigthCase; //
    private int widthCase;
    private int heigth;
    private int width;
    private char[][] tab; //[hauteur][largeur]

    // Prend en paramètre hauteur et largeur (en nombre de cases) et initialise le tableau
    public Labyrinthe(int h, int w)
    {
        heigthCase = h;
        widthCase = w;
        heigth = h * HMURET + 1;
        width = w * LMURET + 1;
        creeTableau(heigth, width);
    }

    //Deep cloning pour avoir deux objets differents (pas meme reference)
    //Copy le tableau de char manuellement car pas du type primitive
    public Labyrinthe(Labyrinthe l)
    {
        heigthCase = l.heigthCase;
        widthCase = l.widthCase;
        heigth = l.heigth;
        width = l.width;

        tab = new char[l.heigth][];
        for (int i = 0; i < l.heigth; i++) {
            tab[i] = new char[l.tab[i].length];
            System.arraycopy(l.tab[i], 0, this.tab[i], 0, l.tab[i].length);
        }
    }

    //Prend en paramètre hauteur et largeur en nbr murets, invoquer par constructeur
    public void creeTableau(int hauteur, int largeur)
    {
        int i, j;
        tab = new char[hauteur][largeur];

        //boucles pour creer separateurs mur haut et bas, et '|' permanents
        for (j = 0; j < heigth; j += (heigth - 1)) {
            for (i = 0; i < width; i++) {
                if (i == 0 || i == (width - 1)) tab[j][i] = '+';
                else tab[j][i] = '-';
            }
        }

        //remplit les autres lignes avec espaces
        effaceTableau();

        //dessine murets d'enceinte pour cote droit sans ouverture
        dessineMurdEnceinte();
    }

    // Remplit le tableau de caractères espace (' ', excluant les murs d'enceinte et separateurs
    public void effaceTableau()
    {
        int i, j;

        for (j = 1; j < (heigth - 1); j++) {
            for (i = 1; i < (width - 1); i++) {
                tab[j][i] = ' ';
            }
        }
    }

    // Dessine mur d'enceinte de droite completement ferme
    // Dessine les '|' static du mur d'enceinte gauche et ' ' sinon
    public void dessineMurdEnceinte()
    {
        int j;

        for (j = 1; j < (heigth - 1); j++) {
            tab[j][width - 1] = '|';
            if (j % HMURET == 0) tab[j][0] = '|';
            else tab[j][0] = ' ';
        }
    }

    // Prend en paramètre la position verticale j (en nombre de cases) de l'ouverture de droite
    // et la crée en effaçant la portion du mur d'enceinte correspondante.
    public void dessineOuverture(int j)
    {
        int indexH = j * HMURET + 1;
        while (indexH % HMURET != 0) tab[indexH++][width - 1] = ' ';
    }

    // Reçoit en paramètre la position i et j de la case où on veut dessiner un muret vertical a gauche
    public void dessineMuretVertical(int i, int j)
    {
        int indexH = HMURET * j + 1;
        int indexL = LMURET * i;

        while (indexH % HMURET != 0) tab[indexH++][indexL] = '|';
    }

    // Reçoit en paramètre la position i et j de la case où on veut dessiner un muret horizontal en haut
    public void dessineMuretHorizontal(int i, int j)
    {
        int indexH = HMURET * j;
        int indexL = LMURET * i + 1;

        while (indexL % LMURET != 0) tab[indexH][indexL++] = '-';
    }

    // Dessine perso avec '@' au centre case
    public void dessinePersonnage(Personnage p)
    {
        int posX = p.getPosX();
        int posY = p.getPosY();

        int indexL = LMURET * posX + (LMURET / 2);
        int indexH = HMURET * posY + (HMURET / 2);
        tab[indexH][indexL] = '@';
    }

    //Efface perso du centre de la case en remplacant '@' par ' '
    public void effacePersonnage(Personnage p)
    {
        int posX = p.getPosX();
        int posY = p.getPosY();

        int indexL = LMURET * posX + (LMURET / 2);
        int indexH = HMURET * posY + (HMURET / 2);
        tab[indexH][indexL] = ' ';
    }

    //Affiche *200* lignes vides pour évacuer vers le haut ce qui était auparavant visible
    public static void effaceEcran()
    {
        for (int i = 0; i < 200; i++) System.out.println();
    }

    //Affiche le tableau de caractères à l'écran
    //suggestion, construisez une String contenant votre tableau puis affichez-la
    public void affiche()
    {
        //"Efface" ce qui avait sur le terminal avant d'imprimer un nouveau tableau
        effaceEcran();

        int i, j;
        String result = "";

        for (j = 0; j < heigth; j++) {
            for (i = 0; i < width; i++) {
                result += tab[j][i];
                if (i == width - 1) result += '\n';
            }
        }
        System.out.println(result);
    }

    //fonction pour afficher plus de details
    public void afficheStatut(Personnage p, int v)
    {
        affiche();
        System.out.println("Il vous reste " + p.getVies() + " vies sur " + v + ".\n");
    }

    //une méthode qui prend en paramètre la "desité" et construit des murets aléatoirement
    //pratique une ouverture (au hasard) sur le mur d'enceinte de droite
    public void construitLabyrintheAleatoire(double densite)
    {
        int i, j;

        //dessine ouverture mur enceinte droite, nbr entre 0 et heigthCase - 1
        dessineOuverture((int) (Math.random() * heigthCase));

        //dessine murets selon les cases
        for (j = 0; j < heigthCase; j++) {
            for (i = 0; i < widthCase; i++) {
                //Construit murets horizontal (excluant 1ere rangee)
                if ((j > 0) && Math.random() <= densite) dessineMuretHorizontal(i, j);

                //Construit murets verticaux (excluant 1ere colonne)
                if ((i > 0) && Math.random() <= densite) dessineMuretVertical(i, j);
            }
        }
    }

    //reçoit en paramètre la position i et j d'une case. Retourne true si sur le bord gauche
    // de cette case, apparaît un muret ou un mur d'enceinte
    boolean aMuretAGauche(int i, int j)
    {
        int indexH = HMURET * j + 1;
        int indexL = LMURET * i;

        return tab[indexH][indexL] == '|';
    }

    // reçoit en paramètre la position i et j d'une case. Retourne true si sur le bord a droite
    // de cette case, apparaît un muret ou un mur d'enceinte
    boolean aMuretADroite(int i, int j)
    {
        int indexH = HMURET * j + 1;
        int indexL = (LMURET * i) + LMURET;

        return tab[indexH][indexL] == '|';
    }

    //reçoit en paramètre la position i et j d'une case. Retourne true si sur le bord en haut
    // de cette case, apparaît un muret ou un mur d'enceinte
    boolean aMuretEnHaut(int i, int j)
    {
        int indexH = HMURET * j;
        int indexL = LMURET * i + 1;

        return tab[indexH][indexL] == '-';
    }

    // reçoit en paramètre la position i et j d'une case. Retourne true si sur le bord en bas
    //de cette case, apparaît un muret ou un mur d'enceinte
    boolean aMuretEnBas(int i, int j)
    {
        int indexH = (HMURET * j) + HMURET;
        int indexL = LMURET * i + 1;

        return tab[indexH][indexL] == '-';
    }

    // reçoit en paramètre la position i et j d'une case. Retourne true si sur le bord à gauche
    // de cette case, c’est l’entrée du labyrinthe
    boolean aEntreeAGauche(int i, int j)
    {
        int indexH = HMURET * j + 1;
        int indexL = LMURET * i;

        return indexL == 0 && tab[indexH][indexL] == ' ';
    }

    // reçoit en paramètre la position i et j d'une case. Retourne true si sur le bord à droite
    // de cette case, apparaît la sortie
    boolean aSortieADroite(int i, int j)
    {
        int indexH = HMURET * j + 1;
        int indexL = (LMURET * i) + LMURET;

        return indexL == width - 1 && tab[indexH][indexL] == ' ';
    }

    int getCoordOuverture() {

        for (int i = 0; i < heigthCase; i++) {
            if (aSortieADroite(widthCase-1, i)) return i;
        }
        return 0; //n'arrive jamais
    }
}
