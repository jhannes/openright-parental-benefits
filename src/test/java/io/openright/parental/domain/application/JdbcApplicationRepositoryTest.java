package io.openright.parental.domain.application;

import io.openright.parental.domain.applicant.Applicant;
import io.openright.parental.domain.users.ApplicationUser;
import io.openright.parental.domain.users.ApplicationUserRole;
import io.openright.parental.server.ParentalBenefitsTestConfig;
import io.openright.parental.server.test.SampleData;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class JdbcApplicationRepositoryTest {

    private static final Random random = new Random();
    private final ParentalBenefitsTestConfig testConfig = ParentalBenefitsTestConfig.instance();
    private final ApplicationRepository repository = new JdbcApplicationRepository(testConfig);

    private final Applicant applicant = SampleData.sampleApplicant();
    private final ApplicationUser user = new ApplicationUser(applicant.getId(), null);
    private final ApplicationUser caseWorker = new ApplicationUser(samplePersonId(), ApplicationUserRole.CASE_WORKER);

    @Before
    public void runAsUser() {
        ApplicationUser.setCurrent(user);
    }

    @Test
    public void shouldFindSavedApplication() throws Exception {
        Application application = insert(sampleApplication(applicant));

        assertThat(repository.retrieve(application.getId()).get()).isEqualTo(application);
        assertThat(repository.retrieve(application.getId()).get())
                .isEqualToComparingFieldByField(application);
        assertThat(repository.retrieve(13243L)).isEmpty();
    }

    @Test
    public void caseWorkerSeesCompletedApplications() throws Exception {
        Application draftApplication = insert(sampleApplication(applicant, "draft"));
        Application completedApplication = insert(sampleApplication(applicant, "submit"));

        ApplicationUser.setCurrent(caseWorker);
        assertThat(repository.list())
                .doesNotContain(draftApplication)
                .contains(completedApplication);
    }

    @Test
    public void otherUserShouldNotSeeMyApplication() throws Exception {
        ApplicationUser otherUser = new ApplicationUser(samplePersonId(), null);

        Application application = insert(sampleApplication(applicant));

        ApplicationUser.setCurrent(otherUser);
        assertThat(repository.list()).doesNotContain(application);
        assertThat(repository.retrieve(application.getId())).isEmpty();
    }

    @Test
    public void shouldUpdateApplication() throws Exception {
        Application application = sampleApplication(applicant);
        insert(application);
        Instant originalUpdatedAt = application.getUpdatedAt();

        application.setStatus("approved");
        Thread.sleep(10);
        repository.update(application.getId(), application);
        assertThat(application.getUpdatedAt()).isGreaterThan(originalUpdatedAt);

        assertThat(repository.retrieve(application.getId()).get()).isEqualTo(application);
    }

    @Test
    public void shouldAddRevision() throws Exception {
        Application application = sampleApplication(applicant);
        assertThat(application.getApplicationForm().keySet()).isEmpty();
        application.addRevision("draft", new JSONObject().put("amount", 321));
        application.addRevision("approved", new JSONObject().put("amount", 123));
        assertThat(application.getStatus()).isEqualTo("approved");
        assertThat(application.getApplicationForm().getInt("amount")).isEqualTo(123);
    }

    @Test
    public void shouldAddNewRevisions() throws Exception {
        Application application = sampleApplication(applicant);
        application.addRevision("draft", new JSONObject().put("title", "originalTitle"));
        Thread.sleep(10);
        application.addRevision("draft", new JSONObject().put("title", "updatedTitle"));
        insert(application);

        Thread.sleep(10);
        application.addRevision("submit", new JSONObject().put("title", "submittedTitle"));
        repository.update(application.getId(), application);

        ApplicationUser.setCurrent(caseWorker);
        application = repository.retrieve(application.getId()).get();
        Thread.sleep(10);
        application.addRevision("updated", new JSONObject().put("title", "updatedTitle"));
        repository.update(application.getId(), application);

        application = repository.retrieve(application.getId()).get();
        assertThat(application.getStatus()).isEqualTo("updated");
        assertThat(application.getApplicationForm().getString("title")).isEqualTo("updatedTitle");
        assertThat(application.getApplicationHistory()).extracting("status")
                .containsExactly("draft", "draft", "submit", "updated");
        assertThat(application.getApplicationHistory()).extracting("userId")
                .containsExactly(user.getPersonId(), user.getPersonId(), user.getPersonId(), caseWorker.getPersonId());
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

    private Application sampleApplication(Applicant applicant) {
        return sampleApplication(applicant, SampleData.random("draft", "submit", "updated", "approved"));
    }

    private Application sampleApplication(Applicant applicant, String status) {
        String applicationType = SampleData.random("maternity-benefits-single", "maternity-benefits", "paternity-benefits-single", "paternity-benefits");
        String office = SampleData.sampleNumeric(4);
        Application application = new Application(applicant, applicationType);
        application.setStatus(status);
        return application;
    }

    private Application insert(Application application) {
        repository.insert(application);
        return application;
    }
}