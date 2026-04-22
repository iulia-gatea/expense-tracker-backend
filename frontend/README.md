# Expense Tracker - React Frontend

A modern React.js frontend for the Expense Tracker application, built with a clean design using white, light grey, and olive green (#6b8e23) color scheme.

## Features

- **User Authentication**: Login and signup functionality with JWT tokens
- **Dashboard**: Overview of expenses with summary cards and recent transactions
- **Expense Management**: Full CRUD operations for expenses
- **Filtering**: Filter expenses by category, date, and month
- **Responsive Design**: Works on desktop and mobile devices
- **Modern UI**: Clean, intuitive interface with consistent styling

## Tech Stack

- **React 18**: Modern React with hooks and functional components
- **React Router**: Client-side routing
- **Axios**: HTTP client for API communication
- **CSS Variables**: Consistent theming with custom properties
- **Responsive Design**: Mobile-first approach

## Getting Started

### Prerequisites

- Node.js (v16 or higher)
- npm or yarn
- Running Expense Tracker backend (Spring Boot) on `http://localhost:8080`

### Installation

1. **Install dependencies:**
   ```bash
   cd frontend
   npm install
   ```

2. **Start the development server:**
   ```bash
   npm start
   ```

3. **Open your browser:**
   Navigate to `http://localhost:3000`

### Backend Setup

Make sure the Spring Boot backend is running on port 8080. The frontend is configured to proxy API requests to the backend.

## Project Structure

```
frontend/
├── public/
│   └── index.html          # Main HTML template
├── src/
│   ├── components/         # Reusable components
│   │   └── Navbar.js       # Navigation component
│   ├── contexts/           # React contexts
│   │   └── AuthContext.js  # Authentication state management
│   ├── pages/              # Page components
│   │   ├── Login.js        # Login page
│   │   ├── Signup.js       # Signup page
│   │   ├── Dashboard.js    # Main dashboard
│   │   ├── Expenses.js     # Expenses list page
│   │   ├── AddExpense.js   # Add expense form
│   │   └── EditExpense.js  # Edit expense form
│   ├── services/           # API services
│   │   └── api.js          # Axios configuration and API calls
│   ├── utils/              # Utility functions
│   ├── App.js              # Main app component with routing
│   ├── App.css             # Global styles and CSS variables
│   └── index.js            # App entry point
└── package.json            # Dependencies and scripts
```

## Color Scheme

The application uses a consistent color scheme:
- **Primary**: `#6b8e23` (Olive Drab Green)
- **Secondary**: `#f5f5f5` (Light Grey)
- **Background**: `#ffffff` (White)
- **Text**: `#333333` (Dark Grey)

## API Integration

The frontend communicates with the Spring Boot backend through REST APIs:

- **Authentication**: `/auth/login`, `/auth/signup`
- **Expenses**: `/expenses` (GET, POST, PUT, DELETE)
- **Categories**: `/expenses/categories`

All API calls include JWT authentication headers when the user is logged in.

## Available Scripts

- `npm start`: Runs the app in development mode
- `npm build`: Builds the app for production
- `npm test`: Launches the test runner
- `npm eject`: Ejects from Create React App (irreversible)

## Features Overview

### Authentication
- Secure login/signup with form validation
- JWT token management with automatic logout on expiration
- Protected routes for authenticated users only

### Dashboard
- Summary cards showing total expenses and transaction count
- Recent expenses list
- Category breakdown with visual progress bars
- Quick action buttons

### Expense Management
- View all expenses in a sortable table
- Advanced filtering by category, date, and month
- Add new expenses with form validation
- Edit existing expenses
- Delete expenses with confirmation
- Responsive table design

### Responsive Design
- Mobile-first approach
- Flexible layouts that work on all screen sizes
- Touch-friendly buttons and forms

## Development Notes

- The app uses React functional components with hooks
- State management is handled through React Context for authentication
- API calls are centralized in the `services/api.js` file
- CSS uses custom properties (variables) for consistent theming
- Error handling includes user-friendly messages and loading states

## Contributing

1. Follow the existing code structure and naming conventions
2. Use functional components with hooks
3. Maintain consistent styling with the defined color scheme
4. Add proper error handling and loading states
5. Test on multiple screen sizes for responsiveness
