import { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import clsx from 'clsx';
import { RootState, AppDispatch } from '../../store/store';
import { fetchSections } from '../../store/slices/sectionsSlice';
import { enroll, clearEnrollmentStatus } from '../../store/slices/enrollmentSlice';
import { fetchSchedule, fetchProgress } from '../../store/slices/studentSlice';
import type { Section } from '../../types/types';
import shared from '../../styles/shared.module.css';
import styles from './CourseCatalog.module.css';

export default function CourseCatalog() {
  const dispatch = useDispatch<AppDispatch>();
  const { items: sections, loading: sectionsLoading } = useSelector((s: RootState) => s.sections);
  const { profile, schedule } = useSelector((s: RootState) => s.student);
  const { loading, error, successMessage } = useSelector((s: RootState) => s.enrollment);

  const [filter, setFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState<'all' | 'core' | 'elective'>('all');

  useEffect(() => {
    if (sections.length === 0) dispatch(fetchSections());
  }, [dispatch, sections.length]);

  useEffect(() => {
    if (successMessage || error) {
      const t = setTimeout(() => dispatch(clearEnrollmentStatus()), 4000);
      return () => clearTimeout(t);
    }
  }, [successMessage, error, dispatch]);

  const enrolledSectionIds = new Set(schedule.map(e => e.section.id));

  const filtered = sections.filter(s => {
    const matchText =
      filter === '' ||
      s.course.name.toLowerCase().includes(filter.toLowerCase()) ||
      s.course.code.toLowerCase().includes(filter.toLowerCase());
    const matchType = typeFilter === 'all' || s.course.courseType === typeFilter;
    return matchText && matchType;
  });

  const handleEnroll = async (section: Section) => {
    if (!profile) return;
    await dispatch(enroll({ studentId: profile.id, sectionId: section.id }));
    dispatch(fetchSchedule(profile.id));
    dispatch(fetchProgress(profile.id));
  };

  if (sectionsLoading) return <div className={shared.cardMuted}>Loading sections…</div>;

  return (
    <div className={shared.cardMuted}>
      <h4 className={styles.title}>Course Catalog — Fall 2024 ({filtered.length} sections)</h4>

      {(successMessage || error) && (
        <div className={clsx(shared.toast, error ? shared.toastError : shared.toastSuccess)}>
          {error ? `${error.type.toUpperCase()}: ${error.message}` : successMessage}
        </div>
      )}

      <div className={styles.filters}>
        <input
          placeholder="Search by name or code…"
          value={filter}
          onChange={e => setFilter(e.target.value)}
          className={styles.input}
        />
        {(['all', 'core', 'elective'] as const).map(t => (
          <button
            key={t}
            onClick={() => setTypeFilter(t)}
            className={clsx(styles.chip, typeFilter === t && styles.chipActive)}
          >
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      <div className={styles.grid}>
        {filtered.map(section => {
          const isEnrolled = enrolledSectionIds.has(section.id);
          const isCore = section.course.courseType === 'core';

          return (
            <div
              key={section.id}
              className={clsx(styles.courseCard, isEnrolled && styles.courseCardEnrolled)}
            >
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
                disabled={!profile || isEnrolled || loading}
                onClick={() => handleEnroll(section)}
                className={clsx(
                  styles.enrollBtn,
                  isEnrolled && styles.enrollBtnEnrolled,
                  (!profile || isEnrolled) && styles.enrollBtnDisabled,
                )}
              >
                {isEnrolled ? '✓ Enrolled' : 'Enroll'}
              </button>
            </div>
          );
        })}
      </div>
    </div>
  );
}
