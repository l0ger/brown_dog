import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../store/store';

export default function StudentProfile() {
  const { profile, progress, loading, error } = useSelector((s: RootState) => s.student);

  if (loading) return <div style={styles.card}>Loading student…</div>;
  if (error) return <div style={{ ...styles.card, color: 'red' }}>{error}</div>;
  if (!profile) return null;

  return (
    <div style={styles.card}>
      <h3 style={{ margin: '0 0 8px' }}>{profile.firstName} {profile.lastName}</h3>
      <div style={styles.row}>
        <span>Grade {profile.gradeLevel}</span>
        <span>{profile.email}</span>
        <span style={{ textTransform: 'capitalize' }}>{profile.status}</span>
      </div>
      {progress && (
        <div style={styles.progress}>
          <div style={styles.stat}>
            <span style={styles.label}>GPA</span>
            <span style={styles.value}>{progress.gpa.toFixed(2)}</span>
          </div>
          <div style={styles.stat}>
            <span style={styles.label}>Credits Earned</span>
            <span style={styles.value}>{progress.creditsEarned} / {progress.creditsRequired}</span>
          </div>
          <div style={styles.stat}>
            <span style={styles.label}>Graduation</span>
            <span style={{ ...styles.value, color: progress.canGraduate ? 'green' : '#888' }}>
              {progress.canGraduate ? '✓ Eligible' : 'In Progress'}
            </span>
          </div>
          <div style={{ flex: 1 }}>
            <div style={styles.barBg}>
              <div style={{ ...styles.barFill, width: `${Math.min(100, (progress.creditsEarned / progress.creditsRequired) * 100)}%` }} />
            </div>
            <small>{Math.round((progress.creditsEarned / progress.creditsRequired) * 100)}% toward graduation</small>
          </div>
        </div>
      )}
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  card: { background: '#fff', borderRadius: 8, padding: 16, boxShadow: '0 1px 4px rgba(0,0,0,.1)' },
  row: { display: 'flex', gap: 20, color: '#555', fontSize: 13, marginBottom: 12 },
  progress: { display: 'flex', gap: 20, alignItems: 'center', flexWrap: 'wrap' },
  stat: { display: 'flex', flexDirection: 'column', alignItems: 'center' },
  label: { fontSize: 11, color: '#888', textTransform: 'uppercase', letterSpacing: 0.5 },
  value: { fontSize: 18, fontWeight: 700 },
  barBg: { height: 8, background: '#e9ecef', borderRadius: 4, overflow: 'hidden', minWidth: 160 },
  barFill: { height: '100%', background: '#0f3460', borderRadius: 4, transition: 'width .3s' },
};
