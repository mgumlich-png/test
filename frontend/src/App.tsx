import { Route, Routes, Navigate, Link, useNavigate } from 'react-router-dom';
import LoginPage from './components/LoginPage';
import Dashboard from './components/Dashboard';
import CaseList from './components/CaseList';
import CaseDetail from './components/CaseDetail';
import { useEffect, useState } from 'react';

const App = () => {
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const navigate = useNavigate();

  useEffect(() => {
    const stored = localStorage.getItem('token');
    if (stored) {
      setToken(stored);
    }
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    setToken(null);
    navigate('/login');
  };

  return (
    <div className="app">
      <header className="topbar">
        <div>GKS Operator Console</div>
        {token && <button onClick={handleLogout}>Logout</button>}
      </header>
      <nav className="nav">
        <Link to="/dashboard">Dashboard</Link>
        <Link to="/cases">Vorgänge</Link>
      </nav>
      <main>
        <Routes>
          <Route path="/login" element={<LoginPage onLogin={setToken} />} />
          <Route path="/dashboard" element={token ? <Dashboard /> : <Navigate to="/login" replace />} />
          <Route path="/cases" element={token ? <CaseList token={token} /> : <Navigate to="/login" replace />} />
          <Route path="/cases/:id" element={token ? <CaseDetail token={token} /> : <Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to={token ? '/dashboard' : '/login'} replace />} />
        </Routes>
      </main>
    </div>
  );
};

export default App;
