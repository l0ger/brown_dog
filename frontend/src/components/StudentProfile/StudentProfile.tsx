import React from 'react';
import { useSelector } from 'react-redux';
import { RootState } from '../../store/store';
import Card from '../shared/Card';
import shared from '../../styles/shared.module.css';
import styles from './StudentProfile.module.css';

function StatItem({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className={styles.stat}>
      <span className={styles.label}>{label}</span>
      <span className={styles.value}>{children}</span>
    </div>
  );
}

export default function StudentProfile() {
  const { profile, progress, loading, error } = useSelector((s: RootState) => s.student);

  if (loading) return <Card>Loading student…</Card>;
  if (error)   return <Card className={styles.cardError}>{error}</Card>;
  if (!profile) return null;

  const fillWidth = `${Math.min(100, (progress?.creditsEarned ?? 0) / (progress?.creditsRequired ?? 1) * 100)}%`;
  const fillPercent = Math.round((progress?.creditsEarned ?? 0) / (progress?.creditsRequired ?? 1) * 100);

  return (
    <Card>
      <h3 className={styles.heading}>{profile.firstName} {profile.lastName}</h3>
      <div className="profile-row">
        <span>Grade {profile.gradeLevel}</span>
        <span className="profile-email">{profile.email}</span>
        <span className={styles.statusCapitalize}>{profile.status}</span>
      </div>
      {progress && (
        <div className={styles.progress}>
          <StatItem label="GPA">{progress.gpa.toFixed(2)}</StatItem>
          <StatItem label="Credits Earned">{progress.creditsEarned} / {progress.creditsRequired}</StatItem>
          <StatItem label="Graduation">
            <span className={progress.canGraduate ? styles.graduationEligible : styles.graduationInProgress}>
              {progress.canGraduate ? '✓ Eligible' : 'In Progress'}
            </span>
          </StatItem>
          <div className={styles.barContainer}>
            <div className={styles.barBg}>
              <div className={styles.barFill} style={{ width: fillWidth }} />
            </div>
            <small>{fillPercent}% toward graduation</small>
          </div>
        </div>
      )}
    </Card>
  );
}
