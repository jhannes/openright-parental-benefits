package io.openright.parental.domain.application;

import java.util.Optional;

public interface ApplicationRepository {
    void insert(Application application);
    Optional<Application> retrieve(Long id);
    void update(Long id, Application application);
}
