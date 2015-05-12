package io.openright.parental.domain.users;

import io.openright.infrastructure.rest.RequestException;
import lombok.Getter;

public class ApplicationUser {
    private static ThreadLocal<ApplicationUser> current = new ThreadLocal<>();

    @Getter
    private final String personId;

    @Getter
    private final ApplicationUserRole userRole;

    public ApplicationUser(String personId, ApplicationUserRole userRole) {
        this.personId = personId;
        this.userRole = userRole;
    }

    public static void setCurrent(ApplicationUser applicationUser) {
        current.set(applicationUser);
    }

    public static ApplicationUser getCurrent() {
        if (current.get() == null) {
            throw new RequestException(401, "Unauthorized");
        }
        return current.get();
    }
}
