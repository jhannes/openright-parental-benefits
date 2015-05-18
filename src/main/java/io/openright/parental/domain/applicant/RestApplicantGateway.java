package io.openright.parental.domain.applicant;

import io.openright.infrastructure.util.IOUtil;
import io.openright.parental.server.ParentalBenefitsConfig;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.net.URI;
import java.util.Optional;

public class RestApplicantGateway implements ApplicantGateway {
    private ParentalBenefitsConfig config;

    public RestApplicantGateway(ParentalBenefitsConfig config) {
        this.config = config;
    }

    @Override
    @SneakyThrows
    public Optional<Applicant> retrieve(@NonNull String applicantId) {
        URI uri = config.getApplicantEndpointUrl().resolve(applicantId);
        return IOUtil.toString(uri).map(s -> toApplicant(new JSONObject(s)));
    }

    private Applicant toApplicant(JSONObject json) {
        Applicant applicant = new Applicant();
        applicant.setId(json.getString("id"));
        applicant.setName(json.getString("name"));
        applicant.setStreetAdress(json.getString("streetAddress"));
        applicant.setPostalCode(json.getString("postalCode"));
        applicant.setPostalArea(json.getString("postalArea"));
        applicant.setNavOffice(json.getString("navOffice"));
        return applicant;
    }
}
