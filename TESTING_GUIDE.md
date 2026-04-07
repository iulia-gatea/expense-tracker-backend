# Unit Testing Guide for ExpenseTracker

## Quick Start

### Prerequisites
Mockito and AssertJ are already added to `build.gradle`. No additional setup needed.

### Running Tests
```bash
./gradlew test                    # Run all tests
./gradlew test --tests "*Test"    # Run tests matching pattern
./gradlew test --info             # Run with detailed output
```

---

## Project Test Structure

```
src/test/java/org/example/
├── controller/
│   ├── AuthControllerTest.java
│   └── ExpenseControllerTest.java
├── service/
│   ├── AuthServiceImplTest.java
│   ├── ExpenseServiceImplTest.java
│   └── UserServiceImplTest.java
├── security/
│   └── JwtUtilTest.java
├── model/
│   ├── AppUserTest.java
│   └── ExpenseTest.java
└── dto/
    └── DTOTest.java
```

---

## Test Template

### Service Test Template
```java
package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MyServiceImplTest {

    @Mock
    private SomeDependency dependency;

    @InjectMocks
    private MyServiceImpl service;

    private TestData testData;

    @BeforeEach
    public void setUp() {
        testData = new TestData();
    }

    @Test
    public void testMethod_WithCondition_ShouldBehavior() {
        // Arrange
        when(dependency.someMethod()).thenReturn(testData);

        // Act
        Result result = service.methodToTest();

        // Assert
        assertThat(result).isNotNull();
        verify(dependency, times(1)).someMethod();
    }
}
```

### Controller Test Template
```java
@ExtendWith(MockitoExtension.class)
public class MyControllerTest {

    @Mock
    private MyService myService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MyController controller;

    @Test
    public void testEndpoint_WithCondition_ShouldReturn() {
        // Arrange
        when(authentication.getName()).thenReturn("username");
        when(myService.findById(1L)).thenReturn(testData);

        // Act
        ResponseEntity<Data> response = controller.endpoint(1L, authentication);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
```

### Model Test Template
```java
public class MyModelTest {

    private MyModel model;

    @BeforeEach
    public void setUp() {
        model = new MyModel();
    }

    @Test
    public void testSetAndGetField_ShouldWorkCorrectly() {
        // Arrange
        String value = "test";

        // Act
        model.setField(value);

        // Assert
        assertThat(model.getField()).isEqualTo(value);
    }
}
```

---

## Common Assertions (AssertJ)

```java
// Basic assertions
assertThat(value).isNotNull();
assertThat(value).isEqualTo(expected);
assertThat(value).isNull();

// Collection assertions
assertThat(list).hasSize(2);
assertThat(list).contains(item1, item2);
assertThat(list).isEmpty();
assertThat(list).doesNotHaveDuplicates();

// String assertions
assertThat(string).contains("text");
assertThat(string).startsWith("prefix");
assertThat(string).endsWith("suffix");

// Optional assertions
assertThat(optional).isPresent();
assertThat(optional).isEmpty();
assertThat(optional.get()).isEqualTo(value);

// HTTP response assertions
assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
assertThat(response.getBody()).isNotNull();

// Chaining assertions
assertThat(value).isNotNull().hasSize(2).contains(item);
```

---

## Common Mockito Patterns

### Setup Mocks
```java
@Mock
private UserRepository userRepository;

@InjectMocks
private UserServiceImpl userService;
```

### Mock Return Values
```java
// Single return value
when(userRepository.findById(1L)).thenReturn(Optional.of(user));

// Multiple return values
when(userRepository.findAll())
    .thenReturn(Arrays.asList(user1, user2));

// Throw exception
when(userRepository.save(null))
    .thenThrow(new IllegalArgumentException("User cannot be null"));

// Return for any argument
when(userRepository.save(any(AppUser.class)))
    .thenReturn(user);
```

### Verify Interactions
```java
// Verify method was called
verify(userRepository, times(1)).save(user);

// Verify method was never called
verify(userRepository, never()).delete(any());

// Verify argument matching
verify(userRepository).save(argThat(u -> 
    u.getUsername().equals("testuser")
));

// Verify call order
InOrder inOrder = inOrder(repository1, repository2);
inOrder.verify(repository1).save(data);
inOrder.verify(repository2).update(data);
```

---

## ExpenseTracker-Specific Testing Patterns

### User Isolation Pattern
Always pass `userId` to verify user-scoped access:

```java
@Test
public void testGetExpensesByUser_ShouldFilterByUserId() {
    // Arrange
    Long userId = 1L;
    when(expenseRepository.findByUserIdOrderByDateDesc(userId))
        .thenReturn(userExpenses);

    // Act
    List<Expense> expenses = expenseService.getAllUserExpenses(userId);

    // Assert
    assertThat(expenses).hasSize(2);
    verify(expenseRepository).findByUserIdOrderByDateDesc(userId);
}
```

### Authentication Context Pattern
Extract and validate username from Authentication object:

```java
@Test
public void testEndpoint_ShouldExtractUsernameFromAuth() {
    // Arrange
    when(authentication.getName()).thenReturn("testuser");
    when(userService.findByUsename("testuser")).thenReturn(testUser);

    // Act
    ResponseEntity<?> response = controller.endpoint(authentication);

    // Assert - verify username extraction
    verify(userService).findByUsename("testuser");
}
```

### HTTP Status Code Pattern
Test both success and error paths:

```java
@Test
public void testCreateExpense_Success_ShouldReturn201() {
    // ... setup
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
}

@Test
public void testDeleteExpense_NotFound_ShouldReturn404() {
    when(expenseService.deleteExpense(999L, userId)).thenReturn(false);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
}
```

---

## Adding New Tests

### Step 1: Identify the Layer
- **Service**: Business logic, use `@Mock` for dependencies
- **Controller**: HTTP handling, use `@Mock` for services and `Authentication`
- **Model**: Simple getters/setters
- **Utility**: Specific functionality (JWT, etc.)

### Step 2: Create Test Class
```bash
# Service test
touch src/test/java/org/example/service/MyServiceImplTest.java

# Controller test
touch src/test/java/org/example/controller/MyControllerTest.java
```

### Step 3: Implement Tests
- Follow Arrange-Act-Assert pattern
- Use meaningful test method names
- Mock external dependencies
- Verify critical interactions

### Step 4: Run Tests
```bash
./gradlew test --tests MyServiceImplTest
```

---

## Best Practices

### 1. Test Naming
Use clear, descriptive names following the pattern:
```
testMethod_WithCondition_ShouldBehavior()
testCreateExpense_WithValidData_ShouldReturn201()
testDeleteExpense_WithInvalidId_ShouldReturnFalse()
```

### 2. Setup and Teardown
```java
@BeforeEach
public void setUp() {
    // Initialize test data and mocks
    testExpense = new Expense();
    testExpense.setId(1L);
    // ...
}

// @AfterEach if cleanup needed
```

### 3. One Assertion Concept Per Test
```java
// ✅ Good: One concept
@Test
public void testGetExpenseById_ShouldReturnExpense() {
    // ...
    assertThat(result).isPresent();
}

// ❌ Bad: Multiple concepts
@Test
public void testExpense() {
    assertThat(result).isPresent();
    assertThat(result.getAmount()).isEqualTo(25.0);
    assertThat(result.getCategory()).isEqualTo("Food");
}
```

### 4. Avoid Test Interdependencies
Tests should run independently and be idempotent.

### 5. Use Descriptive Variable Names
```java
// ✅ Good
AppUser testUser = createTestUser();
Expense testExpense = createTestExpense();

// ❌ Bad
AppUser u = new AppUser();
Expense e = new Expense();
```

---

## Debugging Tests

### Run Single Test
```bash
./gradlew test --tests ExpenseServiceImplTest.testAddExpense*
```

### View Test Report
```
build/reports/tests/test/index.html
```

### Debug Mode
Add breakpoints in IDE and run:
```bash
./gradlew test --debug
```

---

## Common Pitfalls

### 1. Forgetting @ExtendWith(MockitoExtension.class)
```java
// ❌ Won't work
public class MyTest {
    @Mock
    private Repository repo;
}

// ✅ Correct
@ExtendWith(MockitoExtension.class)
public class MyTest {
    @Mock
    private Repository repo;
}
```

### 2. Not Mocking Service Dependencies
```java
// ❌ Wrong - expenseRepository is null
@InjectMocks
private ExpenseServiceImpl expenseService;

// ✅ Correct
@Mock
private ExpenseRepository expenseRepository;

@InjectMocks
private ExpenseServiceImpl expenseService;
```

### 3. Comparing Objects Without Equals
```java
// ❌ Wrong
assertThat(result == testUser).isTrue();

// ✅ Correct
assertThat(result).isEqualTo(testUser);
```

### 4. Not Verifying Mock Interactions
```java
// ❌ Incomplete test
when(repo.save(user)).thenReturn(user);
service.saveUser(user);
// Missing verification!

// ✅ Complete test
verify(repo, times(1)).save(user);
```

---

## Integration with CI/CD

The test suite is ready for continuous integration:

```yaml
# Example CI/CD configuration
test:
  script:
    - ./gradlew test
  artifacts:
    reports:
      junit: build/test-results/test/TEST-*.xml
```

---

## Resources

- **JUnit 5 Documentation**: https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation**: https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **AssertJ Documentation**: https://assertj.github.io/assertj-core-features-highlight.html

