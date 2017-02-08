package trac.portableexpensesdiary.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String dateToString(Date date) {
        return android.text.format.DateFormat.format(
                "dd.MM.yyyy, HH:mm",
                date
        ).toString();
    }

    public static String timeToString(Long date) {
        return android.text.format.DateFormat.format(
                "HH:mm:ss",
                date
        ).toString();
    }

    public static String dateToString(Long date) {
        return android.text.format.DateFormat.format(
                "dd.MM.yyyy",
                new Date(date)
        ).toString();
    }

    public static String groupDateToString(Long date) {
        return android.text.format.DateFormat.format(
                "dd MMM yyyy",
                new Date(date)
        ).toString();
    }

    public static String dateToStringWithoutYear(Long date) {
        return android.text.format.DateFormat.format(
                "dd.MM.",
                new Date(date)
        ).toString();
    }

    public static String subtractDates(Date rootDate, Date subtractorDate) {
        int diffInDays =
                (int) ((rootDate.getTime() - subtractorDate.getTime()) / (1000 * 60 * 60 * 24));

        return String.valueOf(diffInDays).replaceAll("-", "");
    }

    public static int getDayOfMonth(Long date) {
        return Integer.parseInt(android.text.format.DateFormat.format(
                "dd",
                new Date(date)
        ).toString());
    }

    public static boolean isSelectedDateSameAsCurrentDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );
        calendar.set(
                Calendar.MINUTE,
                0
        );
        calendar.set(
                Calendar.SECOND,
                0
        );
        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        Date today = calendar.getTime();

        calendar.setTime(date);
        calendar.set(
                Calendar.HOUR_OF_DAY,
                0
        );
        calendar.set(
                Calendar.MINUTE,
                0
        );
        calendar.set(
                Calendar.SECOND,
                0
        );
        calendar.set(
                Calendar.MILLISECOND,
                0
        );

        Date selectedDate = calendar.getTime();

        return today.getTime() == selectedDate.getTime();
    }

    public static Date getDatePlusOneMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, +1);

        return calendar.getTime();
    }

    public static Date getDatePlusOneYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, +1);

        return calendar.getTime();
    }
}