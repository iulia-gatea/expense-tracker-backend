package org.example.controller;

import org.example.model.AppUser;
import org.example.model.Expense;
import org.example.service.ExpenseService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/expenses")
@CrossOrigin(origins = "*")
public class ExpenseController {
    private final ExpenseService expenseService;
    private final UserService userService;


    public ExpenseController(ExpenseService expenseService, UserService userService) {
        this.userService = userService;
        this.expenseService = expenseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Expense>> getExpenseById(@PathVariable Long id, Authentication authentication){
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        return ResponseEntity.ok(expenseService.getExpenseById(id, user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<Expense>> getExpenses(Authentication authentication){
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        return ResponseEntity.ok(expenseService.getAllUserExpenses(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Expense> addExpense(@RequestBody Expense expense, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);
        expense.setUser(user);
        Expense newExpense = expenseService.addExpense(expense);

        return new ResponseEntity<>(newExpense, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id,
                                                 @RequestBody Expense expenseDetails, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);
        expenseDetails.setId(id);
        if(expenseService.updateExpense(expenseDetails, user.getId())) {
            return new ResponseEntity<>(expenseDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Expense> deleteExpense(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        HttpStatus httpStatus = expenseService.deleteExpense(id, user.getId())
                ? HttpStatus.NO_CONTENT
                : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(httpStatus);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllExpenseCategories(Authentication authentication){
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);
        List<String> categories = expenseService.getAllExpenseCategories(user.getId());

        if(categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/day/{date}")
    public ResponseEntity<List<Expense>> getExpenseByDay(@PathVariable String date, Authentication authentication) {
        String username = authentication.getName();
        System.out.println("username = " + username);
        AppUser user = userService.findByUsename(username);
        List<Expense> expenses = expenseService.getExpenseByDate(date, user.getId());

        return ResponseEntity.ok(expenses);
    }

    @GetMapping("/category/{category}/month")
    public ResponseEntity<List<Expense>> getExpenseByCategoryAndMonth(@PathVariable String category, @RequestParam String month, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        return ResponseEntity.ok(expenseService.getExpenseByCategoryAndMonth(category, month, user.getId()));
    }
}
