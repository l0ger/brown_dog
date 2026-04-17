import { useState } from 'react';
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
    <div className="topbar">
      <span className="topbar-title">Maplewood Course Planning</span>
      <div className="topbar-controls">
        <input
          type="number"
          placeholder="Student ID (e.g. 1)"
          value={inputId}
          onChange={e => setInputId(e.target.value)}
          onKeyDown={e => e.key === 'Enter' && handleLoad()}
        />
        <button onClick={handleLoad}>Load Student</button>
        <button onClick={handleClear} className="btn-clear">Clear</button>
      </div>
    </div>
  );
}
