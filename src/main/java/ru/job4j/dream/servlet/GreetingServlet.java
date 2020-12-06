package ru.job4j.dream.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.job4j.dream.servlet.dto.GreetingRequest;
import ru.job4j.dream.servlet.dto.GreetingResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class GreetingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String body = req.getReader().lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);
        GreetingRequest greetingRequest = objectMapper.readValue(body, GreetingRequest.class);

        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        String response = objectMapper.writeValueAsString(GreetingResponse.builder()
                .text(greetingRequest.getEmail())
                .build());

        writer.println(response);
        writer.flush();
        writer.close();
    }
}
