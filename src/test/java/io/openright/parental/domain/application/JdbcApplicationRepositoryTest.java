package io.openright.parental.domain.application;

import io.openright.parental.server.ParentalBenefitsTestConfig;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbcApplicationRepositoryTest {

    private ParentalBenefitsTestConfig testConfig = ParentalBenefitsTestConfig.instance();
    private final ApplicationRepository repository = new JdbcApplicationRepository(testConfig);

    @Test
    public void shouldFindSavedApplication() throws Exception {
        Application application = sampleApplication();
        repository.insert(application);

        assertThat(repository.retrieve(application.getId())).contains(application);
        assertThat(repository.retrieve(13243L)).isEmpty();
    }

    private Application sampleApplication() {
        return new Application(new JSONObject().put("application", new JSONObject().put("name", "some name")));
    }
}