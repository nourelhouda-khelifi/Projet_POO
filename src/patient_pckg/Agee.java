package patient;

public class Agee extends PatientAbstract {

    public Agee(String id, double beta, double coeffFatigue) {
        super(id, beta, coeffFatigue);
    }

    @Override
    protected double calculerReponseImmunitaire(Pathogene p, double Lnext, double It) {
        return It + beta * Lnext - coeffFatigue * It * It;
    }
}
