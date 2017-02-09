

public class Personnage
{
    //coordonnees, reference labyrinthe et nbr de vies
    private int posX;
    private int posY;
    private Labyrinthe laby;
    private int vies;

    public Personnage(Labyrinthe l, int i, int j, int v)
    {
        laby = l;
        posX = i;
        posY = j;
        vies = v;
    }

    public int getPosX() { return posX; }

    public int getPosY() { return posY; }

    public int getVies() { return this.vies; }

    public void perdreVie() { this.vies--; }

    public void setLaby(Labyrinthe l) { this.laby = l; }

    //Methodes pour deplacer personnage dans toutes les directions
    //Efface personnage dans labyInvisible
    //Ajuste les coordonnees et redessine perso dans labyInvisible
    public void deplacerGauche()
    {
        this.laby.effacePersonnage(this);
        this.posX--;
        this.laby.dessinePersonnage(this);
    }

    public void deplacerDroite()
    {
        this.laby.effacePersonnage(this);
        this.posX++;
        this.laby.dessinePersonnage(this);
    }

    public void deplacerHaut()
    {
        this.laby.effacePersonnage(this);
        this.posY--;
        this.laby.dessinePersonnage(this);
    }

    public void deplacerBas()
    {
        this.laby.effacePersonnage(this);
        this.posY++;
        this.laby.dessinePersonnage(this);
    }
}

