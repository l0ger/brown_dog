import { screen } from '@testing-library/react';
import { renderWithStore, mockStudent, mockProgress } from '../../test-utils';
import StudentProfile from './StudentProfile';

describe('StudentProfile', () => {
  it('shows a loading message while the student is being fetched', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: null, schedule: [], progress: null, loading: true, error: null } },
    });
    expect(screen.getByText('Loading student…')).toBeInTheDocument();
  });

  it('shows the error message when fetching fails', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: null, schedule: [], progress: null, loading: false, error: 'Student not found' } },
    });
    expect(screen.getByText('Student not found')).toBeInTheDocument();
  });

  it('renders nothing when no profile is loaded', () => {
    const { container } = renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: null, schedule: [], progress: null, loading: false, error: null } },
    });
    expect(container).toBeEmptyDOMElement();
  });

  it('renders the student full name and grade', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: mockStudent, schedule: [], progress: null, loading: false, error: null } },
    });
    expect(screen.getByText('Alice Smith')).toBeInTheDocument();
    expect(screen.getByText('Grade 10')).toBeInTheDocument();
  });

  it('renders the student email and capitalised status', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: mockStudent, schedule: [], progress: null, loading: false, error: null } },
    });
    expect(screen.getByText('alice@maplewood.edu')).toBeInTheDocument();
    expect(screen.getByText('active')).toBeInTheDocument();
  });

  it('renders GPA and credit counts when progress is available', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: mockStudent, schedule: [], progress: mockProgress, loading: false, error: null } },
    });
    expect(screen.getByText('3.75')).toBeInTheDocument();
    expect(screen.getByText('12 / 24')).toBeInTheDocument();
  });

  it('shows "In Progress" graduation status when canGraduate is false', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: mockStudent, schedule: [], progress: { ...mockProgress, canGraduate: false }, loading: false, error: null } },
    });
    expect(screen.getByText('In Progress')).toBeInTheDocument();
  });

  it('shows "✓ Eligible" when the student can graduate', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: mockStudent, schedule: [], progress: { ...mockProgress, canGraduate: true }, loading: false, error: null } },
    });
    expect(screen.getByText('✓ Eligible')).toBeInTheDocument();
  });

  it('renders the correct graduation percentage', () => {
    renderWithStore(<StudentProfile />, {
      preloadedState: { student: { profile: mockStudent, schedule: [], progress: mockProgress, loading: false, error: null } },
    });
    // 12 / 24 = 50%
    expect(screen.getByText('50% toward graduation')).toBeInTheDocument();
  });
});
