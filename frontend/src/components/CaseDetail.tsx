import axios from 'axios';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

interface Props {
  token: string;
}

interface DocumentRecord {
  id: number;
  fileName: string;
  version: number;
  hash: string;
}

const CaseDetail = ({ token }: Props) => {
  const { id } = useParams();
  const [docs, setDocs] = useState<DocumentRecord[]>([]);
  const [file, setFile] = useState<File | null>(null);

  const loadDocs = () => {
    axios
      .get(`/api/documents/case/${id}`, { headers: { Authorization: `Bearer ${token}` } })
      .then((res) => setDocs(res.data));
  };

  useEffect(() => {
    loadDocs();
  }, [id]);

  const upload = async () => {
    if (!file) return;
    const formData = new FormData();
    formData.append('file', file);
    await axios.post(`/api/documents/case/${id}`, formData, {
      headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'multipart/form-data' },
    });
    setFile(null);
    loadDocs();
  };

  return (
    <div className="card">
      <h2>Vorgang {id}</h2>
      <div className="upload">
        <input type="file" onChange={(e) => setFile(e.target.files?.[0] ?? null)} />
        <button onClick={upload} disabled={!file}>
          Dokument hochladen
        </button>
      </div>
      <h3>Dokumente</h3>
      <ul>
        {docs.map((d) => (
          <li key={d.id}>
            {d.fileName} (v{d.version}) – Hash: {d.hash}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default CaseDetail;
