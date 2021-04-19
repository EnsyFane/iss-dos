package domain.models;

public enum UserType {
    Admin(0),
    PharmacyStaff(1),
    HospitalStaff(2);

    private final Integer databaseRepresentation;

    UserType(final Integer databaseRepresentation) {
        this.databaseRepresentation = databaseRepresentation;
    }
    public Integer toDatabaseRepresentation() {
        return databaseRepresentation;
    }
    public static UserType fromDatabaseRepresentation(Integer databaseRepresentation) {
        return switch (databaseRepresentation) {
            case 0 -> Admin;
            case 1 -> PharmacyStaff;
            case 2 -> HospitalStaff;
            default -> throw new IndexOutOfBoundsException();
        };
    }
}
