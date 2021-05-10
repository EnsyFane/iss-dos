package utils;

public class Constants {
    public static final Integer SALT_LENGTH = 64;
    public static final Integer ENCRYPTED_PASSWORD_LENGTH = 64;
    public static final Long DISTANCE_BETWEEN_PASSWORD_CHANGES = 6 * 2629800000L; // 6 Months
    @SuppressWarnings("PointlessArithmeticExpression")
    public static final Long DEFAULT_DRUG_DELIVERY_ETA = 1 * 86400000L; // 1 Day
}
