package io.openright.parental.server;

import io.openright.infrastructure.db.Database;

public interface ParentalBenefitsConfig {
    Database getDatabase();
}
