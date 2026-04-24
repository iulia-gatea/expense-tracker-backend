package org.example.controller;

import org.example.dto.ExpenseDTO;
import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.service.CategoryService;
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
    private final CategoryService categoryService;


    public ExpenseController(ExpenseService expenseService, UserService userService, CategoryService categoryService) {
        this.userService = userService;
        this.expenseService = expenseService;
        this.categoryService = categoryService;
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
    public ResponseEntity<Expense> addExpense(@RequestBody ExpenseDTO expenseDTO, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        // Convert DTO to Expense entity
        Expense expense = new Expense();
        expense.setDate(expenseDTO.getDate());
        expense.setAmount(Double.parseDouble(expenseDTO.getAmount()));
        expense.setAccount(expenseDTO.getAccount());
        expense.setNote(expenseDTO.getNote());
        expense.setExpenseType(expenseDTO.getExpenseType());

        // Get the category by ID
        Optional<Category> category = categoryService.getCategoryById(expenseDTO.getCategoryId(), user.getId());
        if (category.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        expense.setCategory(category.get());
        expense.setUser(user);

        Expense newExpense = expenseService.addExpense(expense);

        return new ResponseEntity<>(newExpense, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(@PathVariable Long id,
                                                 @RequestBody ExpenseDTO expenseDTO, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        // Get existing expense
        Optional<Expense> existingExpenseOpt = expenseService.getExpenseById(id, user.getId());
        if (existingExpenseOpt.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Expense existingExpense = existingExpenseOpt.get();

        // Update fields from DTO
        existingExpense.setDate(expenseDTO.getDate());
        existingExpense.setAmount(Double.parseDouble(expenseDTO.getAmount()));
        existingExpense.setAccount(expenseDTO.getAccount());
        existingExpense.setNote(expenseDTO.getNote());
        existingExpense.setExpenseType(expenseDTO.getExpenseType());

        // Update category if provided
        if (expenseDTO.getCategoryId() != null) {
            Optional<Category> category = categoryService.getCategoryById(expenseDTO.getCategoryId(), user.getId());
            if (category.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            existingExpense.setCategory(category.get());
        }

        if(expenseService.updateExpense(existingExpense, user.getId())) {
            return new ResponseEntity<>(existingExpense, HttpStatus.OK);
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
        List<Category> categories = expenseService.getAllExpenseCategories(user.getId());

        if(categories.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return ResponseEntity.ok(categories.stream().map(Category::getName).toList());
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
    public ResponseEntity<List<Expense>> getExpenseByCategoryIdAndMonth(@PathVariable Long categoryId, @RequestParam String month, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        return ResponseEntity.ok(expenseService.getExpenseByCategoryIdAndMonth(categoryId, month, user.getId()));
    }

    @GetMapping("expense_type/{type}")
    public ResponseEntity<List<Expense>> getExpensesByType(@PathVariable Integer type, Authentication authentication){
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        return ResponseEntity.ok(expenseService.getExpenseByType(type, user.getId()));
    }
}
