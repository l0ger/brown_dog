import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store/store';
import { drop } from '../store/slices/enrollmentSlice';
import { fetchSchedule, fetchProgress } from '../store/slices/studentSlice';
import styles from './Schedule.module.css';

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
    <div className={styles.card}>
      <h4 className={styles.title}>Current Schedule ({schedule.length}/5)</h4>
      {schedule.length === 0 ? (
        <p className={styles.empty}>No courses enrolled yet.</p>
      ) : (
        <div className={styles.tableWrap}>
          <table className={styles.table}>
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
                  <th key={h} className={styles.th}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {schedule.map(e => (
                <tr key={e.id} className={styles.tr}>
                  <td className={styles.td}>
                    <strong>{e.section.course.code}</strong>
                    <div className={styles.courseName}>{e.section.course.name}</div>
                  </td>
                  <td className={styles.td}>{e.section.teacherName}</td>
                  <td className={styles.td}>{e.section.classroom}</td>
                  <td className={styles.td}>{e.section.daysOfWeek}</td>
                  <td className={styles.td}>{e.section.startTime}–{e.section.endTime}</td>
                  <td className={styles.td}>
                    <button
                      onClick={() => handleDrop(e.id)}
                      disabled={loading}
                      className={styles.dropBtn}
                      aria-label={`Drop ${e.section.course.name}`}
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
