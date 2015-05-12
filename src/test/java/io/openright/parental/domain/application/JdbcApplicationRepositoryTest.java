package io.openright.parental.domain.application;

import io.openright.parental.domain.users.ApplicationUser;
import io.openright.parental.domain.users.ApplicationUserRole;
import io.openright.parental.server.ParentalBenefitsTestConfig;
import org.json.JSONObject;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbcApplicationRepositoryTest {

    private static final Random random = new Random();
    private final ParentalBenefitsTestConfig testConfig = ParentalBenefitsTestConfig.instance();
    private final ApplicationRepository repository = new JdbcApplicationRepository(testConfig);
    private final ApplicationUser caseWorker = new ApplicationUser(samplePersonId(), ApplicationUserRole.CASE_WORKER);
    private final ApplicationUser user = new ApplicationUser(samplePersonId(), null);

    @Test
    public void shouldFindSavedApplication() throws Exception {
        Application application = sampleApplication(user);
        repository.insert(application);

        ApplicationUser.setCurrent(user);
        assertThat(repository.retrieve(application.getId())).contains(application);
        assertThat(repository.retrieve(13243L)).isEmpty();
    }

    @Test
    public void caseWorkerSeesAllApplications() throws Exception {
        Application application1 = insert(sampleApplication(user));
        Application application2 = insert(sampleApplication(user));

        ApplicationUser.setCurrent(caseWorker);
        assertThat(repository.list()).contains(application1, application2);
    }

    @Test
    public void userOnlySeesOwnApplications() throws Exception {
        ApplicationUser them = new ApplicationUser(samplePersonId(), null);

        Application mine = insert(sampleApplication(user));
        Application theirs = insert(sampleApplication(them));

        ApplicationUser.setCurrent(user);
        assertThat(repository.list())
                .contains(mine)
                .doesNotContain(theirs);
    }

    private static String samplePersonId() {
        LocalDate birthDate = sampleDate(LocalDate.now().minusYears(50), LocalDate.now().minusYears(20));
        return birthDate.format(DateTimeFormatter.ofPattern("ddMMyy"))
                + randomNumeric(5);
    }

    private static String randomNumeric(int digits) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            result.append(randomInt(10));
        }
        return result.toString();
    }

    private static int randomInt(int max) {
        return random.nextInt(max);
    }

    private static LocalDate sampleDate(LocalDate start, LocalDate end) {
        return start.plusDays(random(end.toEpochDay() - start.toEpochDay()));
    }

    private static long random(long max) {
        return random.nextLong()%max;
    }

    private Application sampleApplication(ApplicationUser user) {
        JSONObject applicationForm = new JSONObject().put("application", new JSONObject().put("name", "some name"));
        return new Application(user.getPersonId(), Instant.now(), Instant.now(), applicationForm);
    }

    private Application insert(Application application) {
        repository.insert(application);
        return application;
    }
}