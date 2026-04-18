import clsx from 'clsx';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/store';
import shared from '../../styles/shared.module.css';
import styles from './StudentProfile.module.css';

export default function StudentProfile() {
  const { profile, progress, loading, error } = useSelector((s: RootState) => s.student);

  if (loading) return <div className={shared.card}>Loading student…</div>;
  if (error)   return <div className={clsx(shared.card, styles.cardError)}>{error}</div>;
  if (!profile) return null;

  const fillWidth = `${Math.min(100, (progress?.creditsEarned ?? 0) / (progress?.creditsRequired ?? 1) * 100)}%`;
  const fillPercent = Math.round((progress?.creditsEarned ?? 0) / (progress?.creditsRequired ?? 1) * 100);

  return (
    <div className={shared.card}>
      <h3 className={styles.heading}>{profile.firstName} {profile.lastName}</h3>
      <div className="profile-row">
        <span>Grade {profile.gradeLevel}</span>
        <span className="profile-email">{profile.email}</span>
        <span className={styles.statusCapitalize}>{profile.status}</span>
      </div>
      {progress && (
        <div className={styles.progress}>
          <div className={styles.stat}>
            <span className={styles.label}>GPA</span>
            <span className={styles.value}>{progress.gpa.toFixed(2)}</span>
          </div>
          <div className={styles.stat}>
            <span className={styles.label}>Credits Earned</span>
            <span className={styles.value}>{progress.creditsEarned} / {progress.creditsRequired}</span>
          </div>
          <div className={styles.stat}>
            <span className={styles.label}>Graduation</span>
            <span className={progress.canGraduate ? styles.graduationEligible : styles.graduationInProgress}>
              {progress.canGraduate ? '✓ Eligible' : 'In Progress'}
            </span>
          </div>
          <div className={styles.barContainer}>
            <div className={styles.barBg}>
              <div className={styles.barFill} style={{ width: fillWidth }} />
            </div>
            <small>{fillPercent}% toward graduation</small>
          </div>
        </div>
      )}
    </div>
  );
}
