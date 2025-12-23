import axios from 'axios';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

interface Props {
  token: string;
}

interface CaseRecord {
  id: number;
  caseType: string;
  status: string;
  reference?: string;
}

const CaseList = ({ token }: Props) => {
  const [cases, setCases] = useState<CaseRecord[]>([]);

  useEffect(() => {
    axios
      .get('/api/cases', { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => setCases(res.data))
      .catch(() => setCases([]));
  }, [token]);

  return (
    <div className="card">
      <h2>Vorgänge</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Typ</th>
            <th>Status</th>
            <th>Referenz</th>
          </tr>
        </thead>
        <tbody>
          {cases.map((c) => (
            <tr key={c.id}>
              <td>
                <Link to={`/cases/${c.id}`}>{c.id}</Link>
              </td>
              <td>{c.caseType}</td>
              <td>{c.status}</td>
              <td>{c.reference ?? '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default CaseList;
