package trac.portableexpensesdiary.utils;

import java.math.BigDecimal;

import trac.portableexpensesdiary.expense.SessionManager;
import trac.portableexpensesdiary.model.Currency;

public class RoundUtils {

    public static final String DECIMAL_REGEX = "(\\-)?([0-9]+?(\\.[0-9]+)?)\\.?";

    public static float round(float d, int decimalPlace) {
        float adjustDecimalPoints = 100001 / 100000;

        BigDecimal bigDecimal = new BigDecimal(
                Float.toString(
                        d * adjustDecimalPoints
                )
        );
        bigDecimal = bigDecimal.setScale(
                decimalPlace,
                BigDecimal.ROUND_HALF_UP
        );

        return bigDecimal.floatValue();
    }

    public static Float stringToFloat(String value) {
        float parsedFloat = 0.00f;

        if (value.trim().isEmpty() || value.trim().equals(".")) {
            return parsedFloat;
        }

        Currency currency = Currency.getCurrency(
                SessionManager.getInstance().getCurrentUser().getCurrencyId()
        );

        if(currency != null && value.contains(currency.getCurrencyName())) {
            return round(
                    Float.valueOf(
                            value.trim().replaceAll(currency.getCurrencyName(), "")
                    ),
                    2
            );
        }

        return round(
                Float.valueOf(
                        value.trim()
                ),
                2
        );
    }

    public static String floatToString(float value) {
        String parsedFloat = String.valueOf(round(value, 2));

        if (!parsedFloat.isEmpty() && parsedFloat.contains(".")) {
            int parsedFloatLength = parsedFloat.length() - 1;
            int decemicalPointAt = parsedFloat.indexOf(".");

            if (parsedFloatLength - decemicalPointAt == 1) {
                parsedFloat = parsedFloat.concat("0");
            }
        }

        return parsedFloat;
    }

    public static String floatToStringWithCurrency(float value) {
        String parsedFloat = floatToString(round(value, 2));

        Currency currency = Currency.getCurrency(
                SessionManager.getInstance().getCurrentUser().getCurrencyId()
        );

        if (currency != null) {
            parsedFloat =
                    parsedFloat.concat(" ").concat(currency.getCurrencyName());
        }

        return parsedFloat;
    }
}