package ngo.model;

public class Beneficiary {
    private final int id;
    private final String name;
    private final String need;
    private final String status;

    public Beneficiary(int id, String name, String need, String status) {
        this.id = id;
        this.name = name;
        this.need = need;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNeed() {
        return need;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Beneficiary #" + id + " | " + name + " | Need: " + need + " | Status: " + status;
    }
}
