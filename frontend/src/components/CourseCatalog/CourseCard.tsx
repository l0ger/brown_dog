import clsx from 'clsx';
import type { Section } from '../../types/types';
import styles from './CourseCatalog.module.css';

interface CourseCardProps {
  section: Section;
  isEnrolled: boolean;
  hasProfile: boolean;
  loading: boolean;
  onEnroll: () => void;
}

export default function CourseCard({ section, isEnrolled, hasProfile, loading, onEnroll }: CourseCardProps) {
  const isCore = section.course.courseType === 'core';

  return (
    <div className={clsx(styles.courseCard, isEnrolled && styles.courseCardEnrolled)}>
      <div className={styles.codeRow}>
        <span className={styles.code}>{section.course.code}</span>
        <span className={clsx(styles.badge, isCore ? styles.badgeCore : styles.badgeElective)}>
          {section.course.courseType}
        </span>
      </div>
      <div className={styles.name}>{section.course.name}</div>
      <div className={styles.meta}>
        {section.daysOfWeek} · {section.startTime}–{section.endTime}
      </div>
      <div className={styles.meta}>
        {section.teacherName} · {section.classroom}
      </div>
      <div className={styles.meta}>
        Gr. {section.course.gradeLevelMin}–{section.course.gradeLevelMax} · {section.course.credits} cr
        {section.course.prerequisiteName && (
          <span className={styles.prereq}> · Prereq: {section.course.prerequisiteName}</span>
        )}
      </div>
      <button
        disabled={!hasProfile || isEnrolled || loading}
        onClick={onEnroll}
        className={clsx(
          styles.enrollBtn,
          isEnrolled && styles.enrollBtnEnrolled,
          (!hasProfile || isEnrolled) && styles.enrollBtnDisabled,
        )}
      >
        {isEnrolled ? '✓ Enrolled' : 'Enroll'}
      </button>
    </div>
  );
}
