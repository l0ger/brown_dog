import { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import clsx from 'clsx';
import { RootState, AppDispatch } from '../../store/store';
import { fetchSections } from '../../store/slices/sectionsSlice';
import { enroll, clearEnrollmentStatus } from '../../store/slices/enrollmentSlice';
import { useStudentRefresh } from '../../store/hooks/useStudentRefresh';
import type { Section } from '../../types/types';
import Card from '../shared/Card';
import Toast from '../shared/Toast';
import CourseCard from './CourseCard';
import shared from '../../styles/shared.module.css';
import styles from './CourseCatalog.module.css';

export default function CourseCatalog() {
  const dispatch = useDispatch<AppDispatch>();
  const refreshStudent = useStudentRefresh();
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
    refreshStudent(profile.id);
  };

  if (sectionsLoading) return <Card variant="muted">Loading sections…</Card>;

  return (
    <Card variant="muted">
      <h4 className={shared.title}>Course Catalog — Fall 2024 ({filtered.length} sections)</h4>

      {(successMessage || error) && (
        <Toast
          variant={error ? 'error' : 'success'}
          message={error ? `${error.type.toUpperCase()}: ${error.message}` : successMessage!}
        />
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
        {filtered.map(section => (
          <CourseCard
            key={section.id}
            section={section}
            isEnrolled={enrolledSectionIds.has(section.id)}
            hasProfile={!!profile}
            loading={loading}
            onEnroll={() => handleEnroll(section)}
          />
        ))}
      </div>
    </Card>
  );
}
