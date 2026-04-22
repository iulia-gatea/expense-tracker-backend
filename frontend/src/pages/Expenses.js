import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { expenseService } from '../services/api';
import './Expenses.css';

const Expenses = () => {
  const [expenses, setExpenses] = useState([]);
  const [filteredExpenses, setFilteredExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({
    category: '',
    date: '',
    month: ''
  });

  useEffect(() => {
    loadExpenses();
  }, []);

  useEffect(() => {
    applyFilters();
  }, [expenses, filters]);

  const loadExpenses = async () => {
    try {
      setLoading(true);
      const [expensesData, categoriesData] = await Promise.all([
        expenseService.getAllExpenses(),
        expenseService.getExpenseCategories()
      ]);

      setExpenses(expensesData);
      setCategories(categoriesData);
    } catch (err) {
      setError('Failed to load expenses');
    } finally {
      setLoading(false);
    }
  };

  const applyFilters = () => {
    let filtered = [...expenses];

    if (filters.category) {
      filtered = filtered.filter(expense => expense.category === filters.category);
    }

    if (filters.date) {
      filtered = filtered.filter(expense => expense.date === filters.date);
    }

    if (filters.month) {
      filtered = filtered.filter(expense => expense.date.startsWith(filters.month));
    }

    setFilteredExpenses(filtered);
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const clearFilters = () => {
    setFilters({
      category: '',
      date: '',
      month: ''
    });
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this expense?')) {
      try {
        await expenseService.deleteExpense(id);
        setExpenses(expenses.filter(expense => expense.id !== id));
      } catch (err) {
        setError('Failed to delete expense');
      }
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString();
  };

  if (loading) {
    return (
      <div className="loading-container">
        <span className="loading"></span>
        <p>Loading expenses...</p>
      </div>
    );
  }

  return (
    <div className="expenses">
      <div className="expenses-header">
        <h1>All Expenses</h1>
        <Link to="/expenses/add" className="btn btn-primary">
          Add New Expense
        </Link>
      </div>

      {error && (
        <div className="alert alert-error">
          {error}
        </div>
      )}

      {/* Filters */}
      <div className="filters">
        <h3>Filter Expenses</h3>
        <div className="filter-controls">
          <div className="form-group">
            <label htmlFor="category">Category</label>
            <select
              id="category"
              name="category"
              value={filters.category}
              onChange={handleFilterChange}
            >
              <option value="">All Categories</option>
              {categories.map(category => (
                <option key={category} value={category}>{category}</option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="date">Date</label>
            <input
              type="date"
              id="date"
              name="date"
              value={filters.date}
              onChange={handleFilterChange}
            />
          </div>

          <div className="form-group">
            <label htmlFor="month">Month</label>
            <input
              type="month"
              id="month"
              name="month"
              value={filters.month}
              onChange={handleFilterChange}
            />
          </div>

          <button onClick={clearFilters} className="btn btn-secondary">
            Clear Filters
          </button>
        </div>
      </div>

      {/* Expenses Table */}
      <div className="expenses-table-container">
        {filteredExpenses.length === 0 ? (
          <div className="empty-state">
            <p>No expenses found. <Link to="/expenses/add">Add your first expense</Link></p>
          </div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Category</th>
                <th>Amount</th>
                <th>Account</th>
                <th>Note</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredExpenses.map(expense => (
                <tr key={expense.id}>
                  <td>{formatDate(expense.date)}</td>
                  <td>{expense.category}</td>
                  <td>${parseFloat(expense.amount).toFixed(2)}</td>
                  <td>{expense.account || '-'}</td>
                  <td>{expense.note || '-'}</td>
                  <td>
                    <div className="action-buttons">
                      <Link
                        to={`/expenses/edit/${expense.id}`}
                        className="btn btn-secondary btn-small"
                      >
                        Edit
                      </Link>
                      <button
                        onClick={() => handleDelete(expense.id)}
                        className="btn btn-danger btn-small"
                      >
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Summary */}
      {filteredExpenses.length > 0 && (
        <div className="expenses-summary">
          <h3>Summary</h3>
          <p>
            Total Expenses: <strong>${filteredExpenses.reduce((total, expense) => total + parseFloat(expense.amount || 0), 0).toFixed(2)}</strong>
          </p>
          <p>
            Number of Transactions: <strong>{filteredExpenses.length}</strong>
          </p>
        </div>
      )}
    </div>
  );
};

export default Expenses;
