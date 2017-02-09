/*
Ce programme est un jeu de labytinthe invisible.

Travail realise par:

Samuel Blais-Dowdy et Remi Langevin
*/

import java.util.Scanner;

public class Laby
{
    public static void main(String[] args)
    {
        //Verification du nbr de parametres
        if (args.length < 5) usage();

        //definir et valider les parametres cmd lines
        int heigth = Integer.parseInt(args[0]); //hauteur nbr cases
        int width = Integer.parseInt(args[1]); //largeur nbr cases
        double densite = Double.parseDouble(args[2]); //densite 0 -> 1
        long seconds = Long.parseLong(args[3]); //secondes affichage
        int vies = Integer.parseInt(args[4]); //vies restantes du personnage

        //validation elementaire des arguments
        validateArgs(heigth, width, densite, seconds, vies);

        //Lance le jeu
        jeuLabyrintheInvisible(heigth, width, densite, seconds, vies);
    }

    //Fonction qui s'occupe de toute la gestion du jeu
    public static void jeuLabyrintheInvisible(int heigth, int width, double densite, long seconds, int vies)
    {
        // Créer un labyrinthe de la bonne taille avec mur enceinte de droite sans ouverture
        Labyrinthe labyMurVisible = new Labyrinthe(heigth, width);

        //Cree ouverture mur de droite (sortie) et initialise tous les murets internes
        labyMurVisible.construitLabyrintheAleatoire(densite);

        //Creation mur enceinte gauche et initialisation du personnage
        int posInitialePerso = creerEntreeLabyrinthe(labyMurVisible, heigth);

        //Creer une copie de labyMurVisible
        Labyrinthe labyMurInvisible = new Labyrinthe(labyMurVisible);
        labyMurInvisible.effaceTableau();

        //Creer personnage et initialisation vies, associer au labyMurInvisible
        Personnage perso = new Personnage(labyMurInvisible, 0, posInitialePerso, vies);

        //Dessine le personnage dans les 2 labyrinthes car affiche labyMurVisible au debut
        labyMurVisible.dessinePersonnage(perso);
        labyMurInvisible.dessinePersonnage(perso);

        //Afficher le labyrinthe "visible" pendant le temps voulu, avant de l'afficher "invisible"
        labyMurVisible.afficheStatut(perso, vies);
        sleep(seconds * 1000);
        labyMurInvisible.afficheStatut(perso, vies);

        //efface perso sinon double lorsque copie (option 'v' ou 'o')
        labyMurVisible.effacePersonnage(perso);

        //Declaration des variables en dehors de la boucle
        Scanner reader = new Scanner(System.in);
        char c;
        boolean entreeLabyrinthe, choixValide;
        int posX, posY;

        while (true) {
            //Message de choix d'options
            System.out.println("Quelle direction souhaitez vous prendre?");
            System.out.println("(droite: d; gauche: g ou s; haut: h ou e; bas: b ou x)");
            System.out.println("Autres options:\n\t\tQuitter Partie: \tq\n\t\tNouvelle Partie: \tp" +
                    "\n\t\tVoir Labyrinthe: \tv\n\t\tOrdinateur (IA): \to");

            //variables
            entreeLabyrinthe = false;
            choixValide = false;
            posX = perso.getPosX();
            posY = perso.getPosY();

            c = reader.next().charAt(0);

            if (c == 'd') { //peut seulement gagner en se deplacant a droite
                choixValide = true;

                //un muret a droite dessine si pas derniere colonne
                if (labyMurVisible.aMuretADroite(posX, posY)) {
                    if (posX != width - 1) labyMurInvisible.dessineMuretVertical(posX + 1, posY);
                } else {
                    //pas de muret a droite test victoire et derniere colonne
                    if (posX == width - 1 && labyMurVisible.aSortieADroite(posX, posY)) {
                        finPartie(labyMurInvisible, perso, true, heigth, width, densite, seconds, vies);
                    } else {
                        perso.deplacerDroite();
                    }
                }
            } else if (c == 'g' || c == 's') { //deplacer a gauche
                choixValide = true;

                //un muret a gauche dessine si pas premiere colonne
                if (labyMurVisible.aMuretAGauche(posX, posY)) {
                    if (posX != 0) labyMurInvisible.dessineMuretVertical(posX, posY);
                } else {
                    //pas de muret a gauche, deplace si pas entree Labyrinthe
                    if (!(entreeLabyrinthe = labyMurVisible.aEntreeAGauche(posX, posY))) {
                        perso.deplacerGauche();
                    }
                }
            } else if (c == 'h' || c == 'e') { //deplacer en haut
                choixValide = true;

                //un muret en haut dessine si pas 1ere rangee
                if (posY == 0 || labyMurVisible.aMuretEnHaut(posX, posY)) {
                    if (posY != 0) labyMurInvisible.dessineMuretHorizontal(posX, posY);
                } else {
                    //pas de muret en haut
                    perso.deplacerHaut();
                }
            } else if (c == 'b' || c == 'x') {
                choixValide = true;

                //un muret en bas dessine si pas derniere rangee
                if (posY == heigth - 1 || labyMurVisible.aMuretEnBas(posX, posY)) {
                    if (posY != heigth - 1) labyMurInvisible.dessineMuretHorizontal(posX, posY + 1);
                } else {
                    //pas de muret en bas
                    perso.deplacerBas();
                }

            } else if (c == 'q') {
                //message d'adieu et quitte le programme
                System.out.println("Merci d'avoir jouer, a la prochaine!");
                System.exit(0);

            } else if (c == 'p') {
                //commencer une nouvelle partie en regenerant un nouveau labyrinthe
                jeuLabyrintheInvisible(heigth, width, densite, seconds, vies);

            } else if (c == 'v' || c == 'o') {
                //rendre labyrinthe visible et controle reste a utilisateur
                //cree une nouvelle copie de labyMurVisible et reassocie perso
                labyMurInvisible = new Labyrinthe(labyMurVisible);
                perso.setLaby(labyMurInvisible);
                labyMurInvisible.dessinePersonnage(perso);

                //passe le controle a l'ordinateur
                if (c == 'o') {
                    IA ordi = new IA(labyMurInvisible, perso, vies, heigth, width);
                    labyMurInvisible.afficheStatut(perso, vies);

                    //ordi.gestionIA() retourne true si victoire et false sinon
                    finPartie(labyMurInvisible, perso, ordi.gestionIA(), heigth, width, densite, seconds, vies);
                }

            }
            //le personnage ne s'est pas deplacer, et a rencontre un muret
            //ne s'est pas deplacer vers entree labyrinthe
            if (choixValide && posX == perso.getPosX() && posY == perso.getPosY() && !entreeLabyrinthe) {
                perso.perdreVie();
                if (perso.getVies() == 0) {
                    finPartie(labyMurInvisible, perso, false, heigth, width, densite, seconds, vies);
                }
            }

            //Reaffiche le labyrinthe et statut car pas victoire, pas defaite
            labyMurInvisible.afficheStatut(perso, vies);
        }
    }

    //Fonction termine programme par la defaite ou victoire
    private static void finPartie(Labyrinthe l, Personnage p, boolean victoire, int heigth, int width,
                                  double densite, long seconds, int vies)
    {
        l.afficheStatut(p, vies);

        if (victoire) {
            int erreurs = vies - p.getVies();
            String msg = " erreur";
            if (erreurs > 1) msg += "s";

            System.out.println("Bravo, vous êtes parvenu jusqu'à la sortie en commettant seulement " +
                    erreurs + msg + ".");
        } else {
            System.out.println("Vous avez perdu, vous avez épuisé vos " + vies + " vies!");
        }

        //Detection de nouvelle partie ou non
        System.out.println("\n\nVoulez-vous jouer une nouvelle partie?");
        System.out.println("(oui: o; non: n)");

        Scanner newGame = new Scanner(System.in);
        char d;

        while (true) {
            d = newGame.next().charAt(0);

            //nouvelle partie avec meme specification
            if (d == 'o') jeuLabyrintheInvisible(heigth, width, densite, seconds, vies);

                //fin partie
            else if (d == 'n') System.exit(0);
        }
    }

    private static int creerEntreeLabyrinthe(Labyrinthe laby, int heigth)
    {
        //Nbr aleatoire entre 0 et heigth-1 pour position vert
        int posVert = (int) (Math.random() * heigth);

        //Construction mur enceinte de gauche et entree labyrinthe
        for (int j = 0; j < heigth; j++) {
            if (j != posVert) laby.dessineMuretVertical(0, j);
        }

        return posVert;
    }

    //Fonction qui fait une pause dans le code pour affichage murets temporairement
    public static void sleep(long millisecondes)
    {
        try {
            Thread.sleep(millisecondes);
        } catch (InterruptedException e) {
            System.out.println("Sleep interrompu");
        }
    }

    //Validation des parametre du command line
    public static void validateArgs(int h, int w, double d, long s, int v)
    {
        if (h < 0 || w < 0 || d < 0 || d > 1 || s < 0 || v < 0) usage();
    }

    public static void usage()
    {
        System.out.println("Nombre de paramètres incorrects ou valeurs erronees.");
        System.out.println(" Utilisation: java Laby <hauteur> " +
                "<largeur> <densite> <duree visible> <nb vies>");
        System.out.println("Ex: java Laby 10 20 0.20 10 5");
        System.exit(0);
    }
}
