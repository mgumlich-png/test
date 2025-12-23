import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

interface Props {
  onLogin: (token: string) => void;
}

const LoginPage = ({ onLogin }: Props) => {
  const [email, setEmail] = useState('admin@gk.local');
  const [password, setPassword] = useState('admin');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await axios.post('/api/auth/login', { email, password });
      localStorage.setItem('token', res.data.token);
      onLogin(res.data.token);
      navigate('/dashboard');
    } catch (err) {
      setError('Login fehlgeschlagen');
    }
  };

  return (
    <div className="card">
      <h2>Login</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={submit}>
        <label>Email</label>
        <input value={email} onChange={(e) => setEmail(e.target.value)} />
        <label>Passwort</label>
        <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit">Login</button>
      </form>
    </div>
  );
};

export default LoginPage;
