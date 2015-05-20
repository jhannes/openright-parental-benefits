package io.openright.parental.domain.applicant;

import lombok.Data;
import org.json.JSONObject;

@Data
public class Applicant {

    private final String id;
    private final String name;
    private final String officeId;
    private String streetAddress;
    private String postalCode;
    private String postalArea;
    private String contactPhone;
    private String email;
    private String accountNumber;

    public JSONObject toJSON() {
        return new JSONObject()
                .put("name", getName())
                .put("address", new JSONObject()
                        .put("streetAddress", getStreetAddress())
                        .put("postalCode", getPostalCode())
                        .put("postalArea", getPostalArea()))
                .put("contactPhone", getContactPhone())
                .put("email", getEmail())
                .put("accountNumber", getAccountNumber())
                .put("officeId", getOfficeId());
    }
}
