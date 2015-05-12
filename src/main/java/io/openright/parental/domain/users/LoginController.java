package io.openright.parental.domain.users;

import io.openright.infrastructure.server.Controller;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class LoginController implements Controller {
    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equals("POST")) {
            JSONObject jsonObject;
            try (BufferedReader reader = req.getReader()) {
                jsonObject = new JSONObject(new JSONTokener(reader));
            }
            req.getSession(true).setAttribute("personId", jsonObject.getString("personId"));
            resp.setStatus(200);
        } else if (req.getMethod().equals("DELETE")) {
            req.getSession(true).setAttribute("personId", null);
        } else {
            resp.setStatus(404);
        }
    }
}