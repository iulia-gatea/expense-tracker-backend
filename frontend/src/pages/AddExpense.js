import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { expenseService, categoryService } from '../services/api';
import './ExpenseForm.css';

const AddExpense = () => {
  const [formData, setFormData] = useState({
    date: new Date().toISOString().split('T')[0], // Today's date
    category: '',
    amount: '',
    account: '',
    note: '',
    expenseType: '0'
  });
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const navigate = useNavigate();

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    try {
      const categoriesData = await categoryService.getAllCategories();
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
        date: formData.date,
        categoryId: formData.category,
        amount: amount.toString(),
        account: formData.account,
        note: formData.note,
        expenseType: parseInt(formData.expenseType)
      };

      await expenseService.createExpense(expenseData);
      navigate('/expenses');
    } catch (err) {
      setError(err.message || 'Failed to add expense');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="expense-form-container">
      <div className="expense-form-card">
        <h1>Add New Transaction</h1>

        {error && (
          <div className="alert alert-error">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>

          <div className="form-group">
            <label htmlFor="expenseType">Type</label>
            <select
              id="expenseType"
              name="expenseType"
              value={formData.expenseType}
              onChange={handleChange}
            >
              <option value="0">Expense</option>
              <option value="1">Income</option>
            </select>
          </div>

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
                <option key={category.id} value={category.id}>{category.name}</option>
              ))}
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
            <label htmlFor="account">Account *</label>
            <select
              id="account"
              name="account"
              value={formData.account}
              onChange={handleChange}
              required
            >
              <option value="">Select an account</option>
              <option value="Cash">Cash</option>
              <option value="Card">Card</option>
              <option value="Bank">Bank Account</option>
            </select>
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
                  Adding...
                </>
              ) : (
                'Add Transaction'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AddExpense;
