package io.openright.infrastructure.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface Controller {
    void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException;
}
