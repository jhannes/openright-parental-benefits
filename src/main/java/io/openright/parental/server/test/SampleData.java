package io.openright.parental.server.test;

import io.openright.parental.domain.applicant.Applicant;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class SampleData {

    private static final Random random = new Random();

    public static String sampleName() {
        return random("Johanne", "Nina", "Tine", "Fredike", "Andrea") + " "
                + random("Persson", "Olsen", "Pedersen", "Carlsen", "Karlsen");
    }

    @SafeVarargs
    public static <T> T random(T... alteratives) {
        return alteratives[random.nextInt(alteratives.length)];
    }

    public static String samplePersonId() {
        LocalDate birthDate = sampleDate(LocalDate.now().minusYears(60), LocalDate.now().minusYears(15));
        return birthDate.format(DateTimeFormatter.ofPattern("ddMMyy"))
                + sampleNumeric(5);
    }

    private static String sampleNumeric(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    private static LocalDate sampleDate(LocalDate lowerBound, LocalDate upperBound) {
        int daysBetween = (int) Duration.between(lowerBound.atStartOfDay(), upperBound.atStartOfDay()).toDays();
        return lowerBound.plusDays(random.nextInt(daysBetween));
    }

    public static Applicant sampleApplicant() {
        return sampleApplicant(samplePersonId(), random("0314", "0315", "0231"));
    }

    public static Applicant sampleApplicant(String id, String navOffice) {
        Applicant applicant = new Applicant();
        applicant.setId(id);
        applicant.setName(sampleName());
        applicant.setStreetAdress(random("Storgata 1", "Kirkegata 1", "Kirkeveien 1"));
        applicant.setPostalCode(random("0101", "0102", "0301"));
        applicant.setPostalArea("Øslø");
        applicant.setNavOffice(navOffice);
        return applicant;
    }
}
