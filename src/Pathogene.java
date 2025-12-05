import java.util.Map;

public abstract class Pathogene { //car on va implementer 
    protected  double L; // pour l'héritage 
    protected double taux; 
    protected  double alpha; 
    protected Map<String, Double> medicamentResistance ;
    protected  Map<String, Double> medicamentSensibilite; 
    protected  double reactiviteB;
    protected  int idPathogene; 

// Constructeur 
public Pathogene(double L, double taux, double alpha, Map<String, Double> medicamentResistance, Map<String, Double> medicamentSensibilite, double reactiviteB, int idPathogene) {
    this.L = L;
    this.taux = taux;
    this.alpha = alpha;
    this.medicamentResistance = medicamentResistance;
    this.medicamentSensibilite = medicamentSensibilite;
    this.reactiviteB = reactiviteB;
    this.idPathogene = idPathogene;

}

//methode calculerCharge 

public abstract double calculerCharge(); // methode abstraite sans parametre car impl

}