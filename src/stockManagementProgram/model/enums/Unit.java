package stockManagementProgram.model.enums;

public enum Unit {
    PIECE("Piece"),
    KG("Kilogram"),
    LITER("Liter"),
    METER("Meter");

    private final String label;

    Unit(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}