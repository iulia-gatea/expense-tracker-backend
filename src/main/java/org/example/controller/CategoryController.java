package org.example.controller;

import org.example.model.AppUser;
import org.example.model.Category;
import org.example.model.Expense;
import org.example.service.CategoryService;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<Category>> getCategories(Authentication authentication){
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        return ResponseEntity.ok(categoryService.getAllUserCategories(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Category> addCategory(@RequestBody Category category, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);
        category.setUser(user);
        Category newCategory = categoryService.addCategory(category);

        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id,
                                                 @RequestBody Category categoryDetails, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);
        categoryDetails.setId(id);
        if(categoryService.updateCategory(categoryDetails, user.getId())) {
            return new ResponseEntity<>(categoryDetails, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Category> deleteCategory(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        AppUser user = userService.findByUsename(username);

        HttpStatus httpStatus = categoryService.deleteCategory(id, user.getId())
                ? HttpStatus.NO_CONTENT
                : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(httpStatus);
    }
}
