package org.example.dto;

import lombok.Data;

@Data
public class ExpenseDTO {
    private String date;
    private Long categoryId;
    private String amount;
    private String account;
    private String note;
    private int expenseType;
}
