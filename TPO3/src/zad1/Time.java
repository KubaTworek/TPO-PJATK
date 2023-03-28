/**
 *
 *  @author Tworek Jakub S25646
 *
 */

package zad1;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {
    public static String passed(String from, String to) {
        try {
            if(from.length() == 16 && to.length() == 16) {
                LocalDateTime dateTimeFrom = LocalDateTime.parse(from);
                LocalDateTime dateTimeTo = LocalDateTime.parse(to);
                String dateFrom = dateTimeFrom.toLocalDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl")));
                String dateTo = dateTimeTo.toLocalDate().format(DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", new Locale("pl")));

                long xDays = dateTimeFrom.until(dateTimeTo, ChronoUnit.DAYS);
                double weeks = xDays / 7.0;
                long hours = dateTimeFrom.until(dateTimeTo, ChronoUnit.HOURS);
                long minutes = dateTimeFrom.until(dateTimeTo, ChronoUnit.MINUTES);

                String timePassed = "";
                if (xDays > 0) {
                    Period period = Period.between(dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate());
                    int years = period.getYears();
                    int months = period.getMonths();
                    int days = period.getDays();

                    String calendar = "";
                    if (years > 0) {
                        calendar += years + " " + chooseWordForm(years, "rok", "lata", "lat") + ", ";
                    }
                    if (months > 0) {
                        calendar += months + " " + chooseWordForm(months, "miesiąc", "miesiące", "miesięcy") + ", ";
                    }
                    if (days > 0) {
                        calendar += days + " " + chooseWordForm((int) days, "dzień", "dni", "dni") + ", ";
                    }
                    if (!calendar.isEmpty()) {
                        calendar = " - kalendarzowo: " + calendar.substring(0, calendar.length() - 2);
                    }

                    timePassed = "Od " + dateFrom + " godz. " + dateTimeFrom.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " do " +
                            dateTo + " godz. " + dateTimeTo.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                            " - mija: " + xDays + " " + chooseWordForm((int) days, "dzień", "dni", "dni") + ", tygodni " + String.format("%.2f", weeks) + "\n" +
                            " - godzin: " + hours + ", minut: " + minutes + "\n" +
                            calendar;
                } else {
                    timePassed = "Od " + dateFrom + " godz. " + dateTimeFrom.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + " do " +
                            dateTo + " godz. " + dateTimeTo.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                            " - mija: 0 dni, tygodni 0\n" +
                            " - godzin: 0, minut: 0";
                }

                return timePassed;
            } else {
                LocalDate dateTimeFrom = LocalDate.parse(from);
                LocalDate dateTimeTo = LocalDate.parse(to);
                String dateFrom = dateTimeFrom.format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("pl")));
                String dateTo = dateTimeTo.format(DateTimeFormatter.ofPattern("d MMMM yyyy", new Locale("pl")));

                long xDays = dateTimeFrom.until(dateTimeTo, ChronoUnit.DAYS);
                double weeks = xDays / 7.0;

                String timePassed = "";
                if (xDays > 0) {
                    Period period = Period.between(dateTimeFrom, dateTimeTo);
                    int years = period.getYears();
                    int months = period.getMonths();
                    int days = period.getDays();

                    String calendar = "";
                    if (years > 0) {
                        calendar += years + " " + chooseWordForm(years, "rok", "lata", "lat") + ", ";
                    }
                    if (months > 0) {
                        calendar += months + " " + chooseWordForm(months, "miesiąc", "miesiące", "miesięcy") + ", ";
                    }
                    if (days > 0) {
                        calendar += days + " " + chooseWordForm((int) days, "dzień", "dni", "dni") + ", ";
                    }
                    if (!calendar.isEmpty()) {
                        calendar = " - kalendarzowo: " + calendar.substring(0, calendar.length() - 2);
                    }

                    timePassed = "Od " + dateFrom + " do " + dateTo + "\n" +
                            " - mija: " + xDays + " " + chooseWordForm((int) days, "dzień", "dni", "dni") + ", tygodni " + String.format("%.2f", weeks) + "\n" +
                            calendar;
                } else {
                    timePassed = "Od " + dateFrom + " godz. " + dateTimeFrom.format(DateTimeFormatter.ofPattern("HH:mm")) + " do " +
                            dateTo + " godz. " + dateTimeTo.format(DateTimeFormatter.ofPattern("HH:mm")) + "\n" +
                            " - mija: 0 dni, tygodni 0\n" +
                            " - godzin: 0, minut: 0";
                }

                return timePassed;
            }
        } catch (DateTimeParseException e) {
            return "*** " + e + e.getMessage();
        }
    }

    private static String chooseWordForm(int number, String form1, String form2, String form3) {
        int absNumber = Math.abs(number);
        int mod10 = absNumber % 10;
        int mod100 = absNumber % 100;

        if (mod10 == 1 && mod100 != 11) {
            return form1;
        } else if ((mod10 >= 2 && mod10 <= 4) && (mod100 < 10 || mod100 >= 20)) {
            return form2;
        } else {
            return form3;
        }
    }
}
