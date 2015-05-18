package io.openright.parental.domain.applicant;

import java.util.Optional;

public interface ApplicantGateway {
    Optional<Applicant> retrieve(String applicantId);
}
