import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from './store/store';
import StudentSelector from './components/StudentSelector';
import StudentProfile from './components/StudentProfile';
import Schedule from './components/Schedule';
import CourseCatalog from './components/CourseCatalog';

export default function App() {
  const profile = useSelector((s: RootState) => s.student.profile);

  return (
    <div style={styles.app}>
      <StudentSelector />
      <div style={styles.body}>
        {!profile ? (
          <div style={styles.empty}>
            <h2>Welcome to Maplewood Course Planning</h2>
            <p>Enter a student ID (1–400) above to load a student profile and start enrolling in courses.</p>
          </div>
        ) : (
          <>
            <div style={styles.left}>
              <StudentProfile />
              <Schedule />
            </div>
            <div style={styles.right}>
              <CourseCatalog />
            </div>
          </>
        )}
      </div>
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  app: { minHeight: '100vh', background: '#f0f2f5', fontFamily: 'system-ui, sans-serif' },
  body: { display: 'flex', gap: 16, padding: 20, alignItems: 'flex-start', flexWrap: 'wrap' },
  left: { display: 'flex', flexDirection: 'column', gap: 16, flex: '0 0 420px', minWidth: 300 },
  right: { flex: 1, minWidth: 320 },
  empty: { margin: '80px auto', textAlign: 'center', color: '#555' },
};
