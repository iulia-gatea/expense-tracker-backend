import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { expenseService } from '../services/api';
import './ExpenseForm.css';

const EditExpense = () => {
  const { id } = useParams();
  const [formData, setFormData] = useState({
    date: '',
    category: '',
    amount: '',
    account: '',
    note: ''
  });
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(true);
  const [error, setError] = useState('');

  const navigate = useNavigate();

  useEffect(() => {
    loadExpense();
    loadCategories();
  }, [id]);

  const loadExpense = async () => {
    try {
      setFetchLoading(true);
      const expense = await expenseService.getExpenseById(id);
      setFormData({
        date: expense.date,
        category: expense.category,
        amount: expense.amount,
        account: expense.account || '',
        note: expense.note || ''
      });
    } catch (err) {
      setError('Failed to load expense');
    } finally {
      setFetchLoading(false);
    }
  };

  const loadCategories = async () => {
    try {
      const categoriesData = await expenseService.getExpenseCategories();
      setCategories(categoriesData);
    } catch (err) {
      // Categories are optional, so we don't set an error
      console.warn('Failed to load categories:', err);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // Validate required fields
      if (!formData.date || !formData.category || !formData.amount) {
        throw new Error('Date, category, and amount are required');
      }

      // Validate amount is a positive number
      const amount = parseFloat(formData.amount);
      if (isNaN(amount) || amount <= 0) {
        throw new Error('Amount must be a positive number');
      }

      const expenseData = {
        ...formData,
        amount: amount.toString(),
        expenseType: 1 // Default expense type
      };

      await expenseService.updateExpense(id, expenseData);
      navigate('/expenses');
    } catch (err) {
      setError(err.message || 'Failed to update expense');
    } finally {
      setLoading(false);
    }
  };

  if (fetchLoading) {
    return (
      <div className="loading-container">
        <span className="loading"></span>
        <p>Loading expense...</p>
      </div>
    );
  }

  return (
    <div className="expense-form-container">
      <div className="expense-form-card">
        <h1>Edit Expense</h1>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="date">Date *</label>
            <input
              type="date"
              id="date"
              name="date"
              value={formData.date}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="category">Category *</label>
            <select
              id="category"
              name="category"
              value={formData.category}
              onChange={handleChange}
              required
            >
              <option value="">Select a category</option>
              {categories.map(category => (
                <option key={category} value={category}>{category}</option>
              ))}
              <option value="Other">Other</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="amount">Amount *</label>
            <input
              type="number"
              id="amount"
              name="amount"
              value={formData.amount}
              onChange={handleChange}
              placeholder="0.00"
              step="0.01"
              min="0.01"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="account">Account</label>
            <input
              type="text"
              id="account"
              name="account"
              value={formData.account}
              onChange={handleChange}
              placeholder="e.g., Cash, Credit Card, Bank Account"
            />
          </div>

          <div className="form-group">
            <label htmlFor="note">Note</label>
            <textarea
              id="note"
              name="note"
              value={formData.note}
              onChange={handleChange}
              placeholder="Additional notes about this expense"
              rows="3"
            />
          </div>

          <div className="form-actions">
            <button
              type="button"
              onClick={() => navigate('/expenses')}
              className="btn btn-secondary"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
            >
              {loading ? (
                <>
                  <span className="loading"></span>
                  Updating...
                </>
              ) : (
                'Update Expense'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditExpense;
