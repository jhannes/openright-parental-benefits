package io.openright.parental.server;

import io.openright.infrastructure.db.Database;

import java.net.URI;

public interface ParentalBenefitsConfig {
    Database getDatabase();

    int getHttpPort();

    URI getApplicantEndpointUrl();
}
