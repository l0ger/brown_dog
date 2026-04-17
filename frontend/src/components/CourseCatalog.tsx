import React, { useEffect, useState } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { RootState, AppDispatch } from '../store/store';
import { fetchSections } from '../store/slices/sectionsSlice';
import { enroll, clearEnrollmentStatus } from '../store/slices/enrollmentSlice';
import { fetchSchedule, fetchProgress } from '../store/slices/studentSlice';
import type { Section } from '../types/types';

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
    const matchText = filter === '' ||
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

  if (sectionsLoading) return <div style={styles.card}>Loading sections…</div>;

  return (
    <div style={styles.card}>
      <h4 style={styles.title}>Course Catalog — Fall 2024 ({filtered.length} sections)</h4>

      {(successMessage || error) && (
        <div style={{ ...styles.toast, background: error ? '#f8d7da' : '#d4edda', color: error ? '#721c24' : '#155724' }}>
          {error ? `${error.type.toUpperCase()}: ${error.message}` : successMessage}
        </div>
      )}

      <div style={styles.filters}>
        <input
          placeholder="Search by name or code…"
          value={filter}
          onChange={e => setFilter(e.target.value)}
          style={styles.input}
        />
        {(['all', 'core', 'elective'] as const).map(t => (
          <button
            key={t}
            onClick={() => setTypeFilter(t)}
            style={{ ...styles.chip, background: typeFilter === t ? '#0f3460' : '#e9ecef', color: typeFilter === t ? '#fff' : '#333' }}
          >
            {t.charAt(0).toUpperCase() + t.slice(1)}
          </button>
        ))}
      </div>

      <div style={styles.grid}>
        {filtered.map(section => {
          const isEnrolled = enrolledSectionIds.has(section.id);
          return (
            <div key={section.id} style={{ ...styles.courseCard, opacity: isEnrolled ? 0.65 : 1 }}>
              <div style={styles.codeRow}>
                <span style={styles.code}>{section.course.code}</span>
                <span style={{ ...styles.badge, background: section.course.courseType === 'core' ? '#0f3460' : '#6c757d' }}>
                  {section.course.courseType}
                </span>
              </div>
              <div style={styles.name}>{section.course.name}</div>
              <div style={styles.meta}>
                {section.daysOfWeek} · {section.startTime}–{section.endTime}
              </div>
              <div style={styles.meta}>
                {section.teacherName} · {section.classroom}
              </div>
              <div style={styles.meta}>
                Gr. {section.course.gradeLevelMin}–{section.course.gradeLevelMax} · {section.course.credits} cr
                {section.course.prerequisiteName && (
                  <span style={styles.prereq}> · Prereq: {section.course.prerequisiteName}</span>
                )}
              </div>
              <button
                disabled={!profile || isEnrolled || loading}
                onClick={() => handleEnroll(section)}
                style={{
                  ...styles.enrollBtn,
                  background: isEnrolled ? '#28a745' : '#0f3460',
                  cursor: !profile || isEnrolled ? 'default' : 'pointer',
                }}
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

const styles: Record<string, React.CSSProperties> = {
  card: { background: '#f8f9fa', borderRadius: 8, padding: 16 },
  title: { margin: '0 0 12px', fontSize: 15 },
  toast: { padding: '10px 14px', borderRadius: 6, marginBottom: 12, fontSize: 13 },
  filters: { display: 'flex', gap: 8, marginBottom: 16, flexWrap: 'wrap', alignItems: 'center' },
  input: { padding: '6px 10px', borderRadius: 4, border: '1px solid #ced4da', flex: 1, minWidth: 180 },
  chip: { padding: '5px 12px', borderRadius: 20, border: 'none', cursor: 'pointer', fontSize: 13 },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(240px, 1fr))', gap: 12 },
  courseCard: { background: '#fff', borderRadius: 8, padding: 14, boxShadow: '0 1px 3px rgba(0,0,0,.08)', display: 'flex', flexDirection: 'column', gap: 4 },
  codeRow: { display: 'flex', justifyContent: 'space-between', alignItems: 'center' },
  code: { fontWeight: 700, color: '#0f3460', fontSize: 13 },
  badge: { fontSize: 10, padding: '2px 7px', borderRadius: 10, color: '#fff' },
  name: { fontWeight: 600, fontSize: 13, lineHeight: 1.3 },
  meta: { fontSize: 11, color: '#666' },
  prereq: { color: '#dc3545' },
  enrollBtn: { marginTop: 8, padding: '6px 0', color: '#fff', border: 'none', borderRadius: 4, fontSize: 13, width: '100%' },
};
