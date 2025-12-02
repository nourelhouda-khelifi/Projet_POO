import java.util.*;

/**
 * Classe abstraite représentant un patient.
 * Le patient ne gère PLUS les traitements (ils sont dans Simulation).
 * Il ne gère QUE ses pathogènes et ses réponses immunitaires.
 */
public abstract class Patient {

    protected String id;
    protected double beta; // coefficient de réponse immunitaire
    protected double coeffFatigue; // coefficient de fatigue

    // Association Pathogene -> Réponse immunitaire It
    protected Map<Pathogene, Double> immuneResponses = new LinkedHashMap<>();

    public PatientAbstract(String id, double beta, double coeffFatigue) {
        this.id = id;
        this.beta = beta;
        this.coeffFatigue = coeffFatigue;
    }

    /* ----------------------- Gestion des Pathogènes ----------------------- */

    public void addPathogene(Pathogene p, double I0) {
        immuneResponses.put(p, Math.max(0, I0));
    }

    public void removePathogene(Pathogene p) {
        immuneResponses.remove(p);
    }

    public Set<Pathogene> getPathogenes() {
        return immuneResponses.keySet();
    }

    /* ----------------------- Réponse Immunitaire ----------------------- */

    public double getImmuneResponse(Pathogene p) {
        return immuneResponses.getOrDefault(p, 0.0);
    }

    public void setImmuneResponse(Pathogene p, double value) {
        immuneResponses.put(p, Math.max(0, value));
    }

    /**
     * Mise à jour de toutes les réponses immunitaires du patient
     * 
     * @param newLValues : Map<Pathogene, Lt+1>
     * @return Map<Pathogene, It+1>
     */
    public Map<Pathogene, Double> updateImmuneResponses(Map<Pathogene, Double> newLValues) {
        Map<Pathogene, Double> newIs = new LinkedHashMap<>();

        for (Map.Entry<Pathogene, Double> entry : newLValues.entrySet()) {
            Pathogene p = entry.getKey();
            double Lnext = entry.getValue();
            double It = immuneResponses.get(p);

            double Inext = calculerReponseImmunitaire(p, Lnext, It);
            Inext = Math.max(0.0, Inext);

            newIs.put(p, Inext);
        }

        // Application des nouvelles valeurs
        for (Map.Entry<Pathogene, Double> e : newIs.entrySet()) {
            immuneResponses.put(e.getKey(), e.getValue());
        }

        return newIs;
    }

    /**
     * Méthode spécifique à chaque type de patient (Jeune, Adulte, Âgé)
     */
    protected abstract double calculerReponseImmunitaire(Pathogene p, double Lnext, double It);

    /* ----------------------- Affichage ----------------------- */

    public void afficherEtat() {
        System.out.println("Patient : " + id);
        for (Map.Entry<Pathogene, Double> entry : immuneResponses.entrySet()) {
            System.out.printf(" - Pathogene %s | I = %.4f%n", entry.getKey().id, entry.getValue());
        }
    }
}
