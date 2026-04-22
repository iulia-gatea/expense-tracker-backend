import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { expenseService } from '../services/api';
import './Dashboard.css';

const Dashboard = () => {
  const [expenses, setExpenses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadDashboardData();
  }, []);

  const loadDashboardData = async () => {
    try {
      setLoading(true);
      const [expensesData, categoriesData] = await Promise.all([
        expenseService.getAllExpenses(),
        expenseService.getExpenseCategories()
      ]);

      setExpenses(expensesData);
      setCategories(categoriesData);
    } catch (err) {
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const getTotalExpenses = () => {
    return expenses.reduce((total, expense) => total + parseFloat(expense.amount || 0), 0);
  };

  const getRecentExpenses = () => {
    return expenses.slice(0, 5);
  };

  const getExpensesByCategory = () => {
    const categoryTotals = {};
    expenses.forEach(expense => {
      const category = expense.category || 'Uncategorized';
      categoryTotals[category] = (categoryTotals[category] || 0) + parseFloat(expense.amount || 0);
    });
    return Object.entries(categoryTotals)
      .map(([category, amount]) => ({ category, amount }))
      .sort((a, b) => b.amount - a.amount);
  };

  if (loading) {
    return (
      <div className="loading-container">
        <span className="loading"></span>
        <p>Loading dashboard...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="error-container">
        <div className="alert alert-error">
          {error}
        </div>
        <button onClick={loadDashboardData} className="btn btn-primary">
          Try Again
        </button>
      </div>
    );
  }

  const totalExpenses = getTotalExpenses();
  const recentExpenses = getRecentExpenses();
  const expensesByCategory = getExpensesByCategory();

  return (
    <div className="dashboard">
      <h1>Dashboard</h1>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="summary-card">
          <h3>Total Expenses</h3>
          <p className="amount">${totalExpenses.toFixed(2)}</p>
        </div>
        <div className="summary-card">
          <h3>Total Transactions</h3>
          <p className="count">{expenses.length}</p>
        </div>
        <div className="summary-card">
          <h3>Categories</h3>
          <p className="count">{categories.length}</p>
        </div>
      </div>

      <div className="dashboard-content">
        {/* Recent Expenses */}
        <div className="dashboard-section">
          <div className="section-header">
            <h2>Recent Expenses</h2>
            <Link to="/expenses" className="btn btn-secondary">
              View All
            </Link>
          </div>

          {recentExpenses.length === 0 ? (
            <div className="empty-state">
              <p>No expenses yet. <Link to="/expenses/add">Add your first expense</Link></p>
            </div>
          ) : (
            <div className="recent-expenses">
              {recentExpenses.map(expense => (
                <div key={expense.id} className="expense-item">
                  <div className="expense-info">
                    <h4>{expense.category}</h4>
                    <p>{expense.date}</p>
                  </div>
                  <div className="expense-amount">
                    ${parseFloat(expense.amount).toFixed(2)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Expenses by Category */}
        <div className="dashboard-section">
          <div className="section-header">
            <h2>Expenses by Category</h2>
          </div>

          {expensesByCategory.length === 0 ? (
            <div className="empty-state">
              <p>No category data available</p>
            </div>
          ) : (
            <div className="category-breakdown">
              {expensesByCategory.map(({ category, amount }) => (
                <div key={category} className="category-item">
                  <div className="category-info">
                    <span className="category-name">{category}</span>
                    <span className="category-amount">${amount.toFixed(2)}</span>
                  </div>
                  <div className="category-bar">
                    <div
                      className="category-bar-fill"
                      style={{
                        width: `${(amount / totalExpenses) * 100}%`
                      }}
                    ></div>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* Quick Actions */}
      <div className="quick-actions">
        <h2>Quick Actions</h2>
        <div className="action-buttons">
          <Link to="/expenses/add" className="btn btn-primary">
            Add Expense
          </Link>
          <Link to="/expenses" className="btn btn-secondary">
            View All Expenses
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
