package tools;

public interface Acceptable {

    public final String TOUR_ID_VALID = "^T\\d{5}$";
    public final String HOMESTAY_ID_VALID = "^HS\\d{4}$";
    public final String INTEGER_VALID = "^[1-9]\\d*$";
    public final String DOUBLE_VALID = "^[+-]?\\d+(\\.\\d+)?";

    public final String NAME_VALID = "^.+$";
    public final String TIME_VALID = "^.+$";
    public final String DATE_VALID = "^\\d{2}/\\d{2}/\\d{4}$";

    public final String BOOKING_ID_VALID = "^B\\d{5}$";
    public final String PHONE_VALID = "^0\\d{9}$";

    public static boolean isValid(String data, String pattern) {
        return data.matches(pattern);
    }
}
