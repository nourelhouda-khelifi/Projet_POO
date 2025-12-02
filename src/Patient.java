import java.util.*;

/**
 * PatientAbstract :
 * - garde la liste des pathogènes (List<Pathogene> pathogenes)
 * - garde la map Medicament -> dose présente dans le corps (Map<Medicament,
 * Double> medicamentDosePresente)
 * - garde la map Pathogene -> It (Map<Pathogene, Double> immuneResponses)
 *
 * REMARQUE : Simulation est responsable d'appliquer les Traitements (doses
 * administrées).
 * Après application, la Simulation appellera patient.addDose(med, dose)
 * ou patient.setDose(med, newValue) selon le choix d'implémentation.
 */
public abstract class Patient {

    protected final String id;
    protected double beta; // paramètre de stimulation
    protected double coeffFatigue; // coefficient de fatigue

    // Structure demandée
    protected final List<> pathogenes = new ArrayList<>();
    protected final Map<Medicament, Double> medicamentDosePresente = new LinkedHashMap<>();

    // Réponse immunitaire par pathogène (It)
    protected final Map<Pathogene, Double> immuneResponses = new LinkedHashMap<>();

    public Patientgi(String id, double beta, double coeffFatigue) {
        this.id = Objects.requireNonNull(id);
        this.beta = beta;
        this.coeffFatigue = coeffFatigue;
    }

    /* -------------------- Gestion des pathogènes -------------------- */

    public void addPathogene(Pathogene p, double initialI) {
        if (p == null)
            throw new IllegalArgumentException("Pathogene null");
        if (!pathogenes.contains(p)) {
            pathogenes.add(p);
            immuneResponses.put(p, Math.max(0.0, initialI));
        }
    }

    public void removePathogene(Pathogene p) {
        pathogenes.remove(p);
        immuneResponses.remove(p);
    }

    public List<Pathogene> getPathogenes() {
        return Collections.unmodifiableList(pathogenes);
    }

    /* -------------------- Gestion des médicaments -------------------- */

    /**
     * Ajoute une dose (somme) pour le médicament dans le corps du patient.
     * Typiquement appelé par Simulation lorsqu'un Traitement administre une dose.
     */
    public void addDose(Medicament med, double doseToAdd) {
        if (med == null)
            throw new IllegalArgumentException("Medicament null");
        double before = medicamentDosePresente.getOrDefault(med, 0.0);
        double after = Math.max(0.0, before + doseToAdd);
        medicamentDosePresente.put(med, after);
        // Mettre à jour aussi l'objet Medicament si tu souhaites synchroniser :
        // med.concentration = after;
    }

    /**
     * Définit explicitement la dose/concentration courante du médicament
     * (remplace).
     */
    public void setDose(Medicament med, double newDose) {
        if (med == null)
            throw new IllegalArgumentException("Medicament null");
        medicamentDosePresente.put(med, Math.max(0.0, newDose));
    }

    /**
     * Retourne la dose actuelle d'un médicament pour ce patient.
     */
    public double getDose(Medicament med) {
        return medicamentDosePresente.getOrDefault(med, 0.0);
    }

    /**
     * Applique la décroissance des concentrations pour tous les médicaments selon
     * leur h.
     * Par exemple : D_{t+1} = h * D_t
     * (Simulation peut appeler cette méthode en fin de cycle si tu veux gérer
     * disparition côté patient)
     */
    public void appliquerDisparitionMedicaments() {
        Map<Medicament, Double> copy = new LinkedHashMap<>(medicamentDosePresente);
        for (Map.Entry<Medicament, Double> e : copy.entrySet()) {
            Medicament med = e.getKey();
            double oldD = e.getValue();
            double newD = med.h * oldD; // on suppose que Medicament expose 'h'
            medicamentDosePresente.put(med, Math.max(0.0, newD));
        }
    }

    /**
     * Fournit une vue Map<String, Double> des concentrations pour usage par
     * Pathogene.calculerChargeSuivante
     * (souvent les stratégies indexent par med.id).
     */
    public Map<String, Double> getConcentrationsParId() {
        Map<String, Double> map = new HashMap<>();
        for (Map.Entry<Medicament, Double> e : medicamentDosePresente.entrySet()) {
            map.put(e.getKey().id, e.getValue());
        }
        return map;
    }

    /* -------------------- Réponses immunitaires -------------------- */

    public double getImmuneResponse(Pathogene p) {
        return immuneResponses.getOrDefault(p, 0.0);
    }

    public void setImmuneResponse(Pathogene p, double value) {
        if (!immuneResponses.containsKey(p))
            throw new IllegalArgumentException("Pathogene non suivi");
        immuneResponses.put(p, Math.max(0.0, value));
    }

    /**
     * Méthode principale : la Simulation calcule L_{t+1} (par
     * Pathogene.calculerChargeSuivante)
     * et passe la map newLValues ici pour mettre à jour It+1 pour chaque pathogène.
     *
     * @param newLValues map Pathogene -> L_{t+1}
     * @return map Pathogene -> I_{t+1} (nouvelles réponses)
     */
    public Map<Pathogene, Double> updateImmuneResponses(Map<Pathogene, Double> newLValues) {
        Map<Pathogene, Double> newIs = new LinkedHashMap<>();
        for (Pathogene p : pathogenes) {
            double Lnext = newLValues.getOrDefault(p, p.L); // fallback à p.L si absent
            double It = immuneResponses.getOrDefault(p, 0.0);
            double Inext = calculerReponseImmunitaire(p, Lnext, It);
            Inext = Math.max(0.0, Inext);
            newIs.put(p, Inext);
        }
        // appliquer
        for (Map.Entry<Pathogene, Double> e : newIs.entrySet()) {
            immuneResponses.put(e.getKey(), e.getValue());
        }
        return newIs;
    }

    /*
     * -------------------- Méthode abstraite (formule selon l'âge)
     * --------------------
     */

    protected abstract double calculerReponseImmunitaire(Pathogene p, double Lnext, double It);

    /* -------------------- Debug / affichage -------------------- */

    public void afficherEtat() {
        System.out.println("Patient " + id + " — pathogènes:");
        for (Pathogene p : pathogenes) {
            System.out.printf("  - %s : L=%.4f | I=%.4f%n", p.id, p.L, getImmuneResponse(p));
        }
        System.out.println(" Médicaments présents :");
        for (Map.Entry<Medicament, Double> e : medicamentDosePresente.entrySet()) {
            System.out.printf("  - %s : dose=%.4f%n", e.getKey().id, e.getValue());
        }
    }
}
