package io.openright.parental.domain.application;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {
    List<Application> list();

    Optional<Application> retrieve(Long id);

    void insert(Application application);

    void update(Long id, Application application);
}
