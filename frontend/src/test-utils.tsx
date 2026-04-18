import { render, RenderOptions } from '@testing-library/react';
import { configureStore, combineReducers } from '@reduxjs/toolkit';
import { Provider } from 'react-redux';
import { PropsWithChildren, ReactElement } from 'react';
import coursesReducer from './store/slices/coursesSlice';
import sectionsReducer from './store/slices/sectionsSlice';
import studentReducer from './store/slices/studentSlice';
import enrollmentReducer from './store/slices/enrollmentSlice';
import type { RootState } from './store/store';
import type { Course, Student, StudentProgress, Section, Enrollment } from './types/types';

// ── Shared test fixtures ─────────────────────────────────────────────────────

export const mockStudent: Student = {
  id: 1,
  firstName: 'Alice',
  lastName: 'Smith',
  email: 'alice@maplewood.edu',
  gradeLevel: 10,
  enrollmentYear: 2020,
  expectedGraduationYear: 2024,
  status: 'active',
};

export const mockProgress: StudentProgress = {
  studentId: 1,
  fullName: 'Alice Smith',
  gradeLevel: 10,
  gpa: 3.75,
  creditsEarned: 12,
  creditsRequired: 24,
  canGraduate: false,
};

export const mockCourse: Course = {
  id: 1,
  code: 'ENG101',
  name: 'English Literature',
  description: 'Introduction to English Literature',
  credits: 3,
  hoursPerWeek: 4,
  courseType: 'core',
  gradeLevelMin: 9,
  gradeLevelMax: 12,
  semesterOrder: 1,
  specialization: '',
  prerequisiteId: null,
  prerequisiteName: null,
};

export const mockElectiveCourse: Course = {
  ...mockCourse,
  id: 2,
  code: 'ART201',
  name: 'Studio Art',
  courseType: 'elective',
};

export const mockSection: Section = {
  id: 1,
  course: mockCourse,
  teacherName: 'Mr. Johnson',
  classroom: 'Room 101',
  daysOfWeek: 'MWF',
  startTime: '09:00',
  endTime: '10:00',
};

export const mockElectiveSection: Section = {
  id: 2,
  course: mockElectiveCourse,
  teacherName: 'Ms. Rivera',
  classroom: 'Room 204',
  daysOfWeek: 'TTh',
  startTime: '11:00',
  endTime: '12:30',
};

export const mockEnrollment: Enrollment = {
  id: 1,
  studentId: 1,
  section: mockSection,
  status: 'active',
};

// ── Store factory ────────────────────────────────────────────────────────────

const rootReducer = combineReducers({
  courses: coursesReducer,
  sections: sectionsReducer,
  student: studentReducer,
  enrollment: enrollmentReducer,
});

export function createTestStore(preloadedState?: Partial<RootState>) {
  return configureStore({
    reducer: rootReducer,
    preloadedState,
  });
}

// ── Custom render ────────────────────────────────────────────────────────────

interface ExtendedRenderOptions extends Omit<RenderOptions, 'queries'> {
  preloadedState?: Partial<RootState>;
}

export function renderWithStore(
  ui: ReactElement,
  { preloadedState, ...renderOptions }: ExtendedRenderOptions = {}
) {
  const store = createTestStore(preloadedState);

  function Wrapper({ children }: PropsWithChildren) {
    return <Provider store={store}>{children}</Provider>;
  }

  return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}

export * from '@testing-library/react';
