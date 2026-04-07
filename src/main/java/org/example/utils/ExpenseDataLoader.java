package org.example.utils;

import jakarta.annotation.PostConstruct;
import org.example.model.Expense;
import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExpenseDataLoader {

    private static List<Expense> expenses = new ArrayList<>();

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/expenses.json");

        expenses = mapper.readValue(inputStream,
                new TypeReference<List<Expense>>() {
                });
    }

    public static List<Expense> getExpenses() {
        return expenses;
    }
}
