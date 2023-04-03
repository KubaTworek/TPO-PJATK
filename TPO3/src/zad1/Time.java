/**
 *
 *  @author Tworek Jakub S25646
 *
 */

package zad1;


import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {

    private static final DecimalFormat formatter = new DecimalFormat("#.##");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl"));
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] WORD_FORMS_DAY = {"dzień", "dni", "dni"};
    private static final String[] WORD_FORMS_YEAR = {"rok", "lata", "lat"};
    private static final String[] WORD_FORMS_MONTH = {"miesiąc", "miesiące", "miesięcy"};


    public static String passed(String from, String to) {
        try {
            if (from.length() == 16 && to.length() == 16) {
                return calculateLocalDateTime(from, to);
            } else {
                return calculateLocalDate(from, to);
            }
        } catch (DateTimeParseException e) {
            return "*** " + e;
        }
    }

    private static String calculateLocalDateTime(String from, String to) {
        LocalDateTime dateTimeFrom = LocalDateTime.parse(from);
        LocalDateTime dateTimeTo = LocalDateTime.parse(to);

        String dateFrom = dateTimeFrom.format(DATE_FORMATTER);
        String dateTo = dateTimeTo.format(DATE_FORMATTER);
        String timeFrom = dateTimeFrom.format(TIME_FORMATTER);
        String timeTo = dateTimeTo.format(TIME_FORMATTER);

        long daysBetween = dateTimeFrom.until(dateTimeTo, ChronoUnit.DAYS);
        double weeks = daysBetween / 7.0;
        long hours = dateTimeFrom.until(dateTimeTo, ChronoUnit.HOURS);
        long minutes = dateTimeFrom.until(dateTimeTo, ChronoUnit.MINUTES);

        Period period = Period.between(dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate());
        String formattedPeriod = formatPeriod(period);
        String mija = daysBetween > 0 ? String.format("%d %s, tygodni %s", daysBetween, chooseWordForm(period.getDays(), WORD_FORMS_DAY), formatter.format(weeks)) : "0 dni, tygodni 0";
        return String.format("Od %s godz. %s do %s godz. %s\n - mija: %s\n - godzin: %d, minut: %d\n - kalendarzowo: %s", dateFrom, timeFrom, dateTo, timeTo, mija, hours, minutes, formattedPeriod);
    }

    private static String calculateLocalDate(String from, String to) {
        LocalDate dateTimeFrom = LocalDate.parse(from);
        LocalDate dateTimeTo = LocalDate.parse(to);

        String dateFrom = dateTimeFrom.format(DATE_FORMATTER);
        String dateTo = dateTimeTo.format(DATE_FORMATTER);

        long daysBetween = dateTimeFrom.until(dateTimeTo, ChronoUnit.DAYS);
        double weeks = daysBetween / 7.0;

        Period period = Period.between(dateTimeFrom, dateTimeTo);

        return (daysBetween > 0) ? String.format("Od %s do %s\n - mija: %d %s, tygodni %.1f\n - kalendarzowo: %s",
                dateFrom, dateTo, daysBetween, chooseWordForm(period.getDays(), WORD_FORMS_DAY), weeks, formatPeriod(period))
                : String.format("Od %s do %s\n - mija: 0 dni, tygodni 0\n - godzin: 0, minut: 0", dateFrom, dateTo);
    }

    private static String formatPeriod(Period period) {
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        StringBuilder sb = new StringBuilder();
        if (years > 0) {
            sb.append(years).append(" ").append(chooseWordForm(years, WORD_FORMS_YEAR)).append(", ");
        }
        if (months > 0) {
            sb.append(months).append(" ").append(chooseWordForm(months, WORD_FORMS_MONTH)).append(", ");
        }
        if (days > 0) {
            sb.append(days).append(" ").append(chooseWordForm(days, WORD_FORMS_DAY)).append(", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        return sb.toString();
    }

    private static String chooseWordForm(int number, String[] wordForms) {
        int formIndex = chooseWordFormIndex(number);
        return wordForms[formIndex];
    }

    private static int chooseWordFormIndex(int number) {
        if (number == 1) {
            return 0;
        } else if (number % 10 >= 2 && number % 10 <= 4 && (number % 100 < 10 || number % 100 >= 20)) {
            return 1;
        } else {
            return 2;
        }
    }
}
