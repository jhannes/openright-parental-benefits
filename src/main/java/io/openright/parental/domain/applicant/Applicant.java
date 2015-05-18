package io.openright.parental.domain.applicant;

import lombok.Data;
import org.json.JSONObject;

@Data
public class Applicant {

    private String id;
    private String name = "Johannes M Brodwall";
    private String streetAdress = "Somewhere road 1";
    private String postalCode = "1182";
    private String postalArea;
    private String contactPhone;
    private String email;
    private String accountNumber;
    private String navOffice;

    public Applicant() {
        setPostalArea("Oslo");
        setContactPhone("999 00 999");
        setEmail("johannes@brodwall.com");
        setAccountNumber("1234 56 12345");
        setNavOffice("0318");
    }

    public JSONObject toJSON() {
        return new JSONObject()
                .put("name", getName())
                .put("address", new JSONObject()
                        .put("streetAddress", getStreetAdress())
                        .put("postalCode", getPostalCode())
                        .put("postalArea", getPostalArea()))
                .put("contactPhone", getContactPhone())
                .put("email", getEmail())
                .put("accountNumber", getAccountNumber())
                .put("navOffice", getNavOffice());
    }
}
