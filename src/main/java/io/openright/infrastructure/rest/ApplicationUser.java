package io.openright.infrastructure.rest;

public class ApplicationUser {
    private static ThreadLocal<ApplicationUser> current = new ThreadLocal<>();

    public ApplicationUser(String personId) {
    }

    public static void setCurrent(ApplicationUser applicationUser) {
        current.set(applicationUser);
    }
}
