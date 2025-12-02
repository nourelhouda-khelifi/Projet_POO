
public class Jeune extends Patient {

    public Jeune(String id, double beta, double coeffFatigue) {
        super(id, beta, coeffFatigue);
    }

    @Override
    protected double calculerReponseImmunitaire(Pathogene p, double Lnext, double It) {
        return It + beta * Math.sqrt(Math.max(0, Lnext)) - coeffFatigue * It;
    }
}
