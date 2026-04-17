import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { AppDispatch } from '../store/store';
import { fetchStudent, fetchSchedule, fetchProgress, clearStudent } from '../store/slices/studentSlice';
import { fetchSections } from '../store/slices/sectionsSlice';

export default function StudentSelector() {
  const dispatch = useDispatch<AppDispatch>();
  const [inputId, setInputId] = useState('');

  const handleLoad = () => {
    const id = parseInt(inputId, 10);
    if (!id) return;
    dispatch(fetchStudent(id));
    dispatch(fetchSchedule(id));
    dispatch(fetchProgress(id));
    dispatch(fetchSections());
  };

  const handleClear = () => {
    dispatch(clearStudent());
    setInputId('');
  };

  return (
    <div style={styles.bar}>
      <strong style={{ marginRight: 12 }}>Maplewood Course Planning</strong>
      <input
        type="number"
        placeholder="Student ID (e.g. 1)"
        value={inputId}
        onChange={e => setInputId(e.target.value)}
        onKeyDown={e => e.key === 'Enter' && handleLoad()}
        style={styles.input}
      />
      <button onClick={handleLoad} style={styles.btn}>Load Student</button>
      <button onClick={handleClear} style={{ ...styles.btn, background: '#6c757d' }}>Clear</button>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  bar: { display: 'flex', alignItems: 'center', gap: 8, padding: '12px 20px', background: '#1a1a2e', color: '#fff' },
  input: { padding: '6px 10px', borderRadius: 4, border: 'none', width: 180 },
  btn: { padding: '6px 14px', borderRadius: 4, border: 'none', background: '#0f3460', color: '#fff', cursor: 'pointer' },
};
