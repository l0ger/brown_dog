import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store/store';
import { drop } from '../store/slices/enrollmentSlice';
import { fetchSchedule, fetchProgress } from '../store/slices/studentSlice';

export default function Schedule() {
  const dispatch = useDispatch<AppDispatch>();
  const { schedule, profile } = useSelector((s: RootState) => s.student);
  const { loading } = useSelector((s: RootState) => s.enrollment);

  if (!profile) return null;

  const handleDrop = async (enrollmentId: number) => {
    await dispatch(drop({ enrollmentId, studentId: profile.id }));
    dispatch(fetchSchedule(profile.id));
    dispatch(fetchProgress(profile.id));
  };

  return (
    <div style={styles.card}>
      <h4 style={styles.title}>Current Schedule ({schedule.length}/5)</h4>
      {schedule.length === 0 ? (
        <p style={{ color: '#888' }}>No courses enrolled yet.</p>
      ) : (
        <div style={styles.tableWrap}>
        <table style={styles.table}>
          <colgroup>
            <col style={{ width: '28%' }} />
            <col style={{ width: '20%' }} />
            <col style={{ width: '14%' }} />
            <col style={{ width: '20%' }} />
            <col style={{ width: '12%' }} />
            <col style={{ width: '6%' }} />
          </colgroup>
          <thead>
            <tr>
              {['Course', 'Teacher', 'Room', 'Days', 'Time', ''].map(h => (
                <th key={h} style={styles.th}>{h}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {schedule.map(e => (
              <tr key={e.id} style={styles.tr}>
                <td style={styles.td}>
                  <strong>{e.section.course.code}</strong>
                  <div style={{ fontSize: 12, color: '#666' }}>{e.section.course.name}</div>
                </td>
                <td style={styles.td}>{e.section.teacherName}</td>
                <td style={styles.td}>{e.section.classroom}</td>
                <td style={styles.td}>{e.section.daysOfWeek}</td>
                <td style={styles.td}>{e.section.startTime}–{e.section.endTime}</td>
                <td style={styles.td}>
                  <button
                    onClick={() => handleDrop(e.id)}
                    disabled={loading}
                    style={styles.dropBtn}
                  >
                    Drop
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        </div>
      )}
    </div>
  );
}

const styles: Record<string, React.CSSProperties> = {
  card: { background: '#fff', borderRadius: 8, padding: 16, boxShadow: '0 1px 4px rgba(0,0,0,.1)', overflow: 'hidden' },
  title: { margin: '0 0 12px', fontSize: 15 },
  tableWrap: { overflowX: 'auto' },
  table: { width: '100%', borderCollapse: 'collapse', fontSize: 13, tableLayout: 'fixed' },
  th: { textAlign: 'left', padding: '6px 8px', borderBottom: '2px solid #dee2e6', color: '#555', fontSize: 12 },
  tr: { borderBottom: '1px solid #f0f0f0' },
  td: { padding: '8px 8px', verticalAlign: 'top', wordBreak: 'break-word' },
  dropBtn: { padding: '3px 10px', background: '#dc3545', color: '#fff', border: 'none', borderRadius: 4, cursor: 'pointer', fontSize: 12, whiteSpace: 'nowrap' },
};
