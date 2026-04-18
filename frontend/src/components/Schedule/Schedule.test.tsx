import { screen } from '@testing-library/react';
import { renderWithStore, mockStudent, mockEnrollment } from '../../test-utils';
import Schedule from './Schedule';

const baseStudentState = { profile: mockStudent, schedule: [], progress: null, loading: false, error: null };
const baseEnrollmentState = { loading: false, error: null, successMessage: null };

describe('Schedule', () => {
  it('renders nothing when no profile is loaded', () => {
    const { container } = renderWithStore(<Schedule />, {
      preloadedState: { student: { profile: null, schedule: [], progress: null, loading: false, error: null } },
    });
    expect(container).toBeEmptyDOMElement();
  });

  it('shows the empty state message when schedule is empty', () => {
    renderWithStore(<Schedule />, {
      preloadedState: {
        student: baseStudentState,
        enrollment: baseEnrollmentState,
      },
    });
    expect(screen.getByText('No courses enrolled yet.')).toBeInTheDocument();
  });

  it('shows the enrollment count in the title', () => {
    renderWithStore(<Schedule />, {
      preloadedState: {
        student: { ...baseStudentState, schedule: [mockEnrollment] },
        enrollment: baseEnrollmentState,
      },
    });
    expect(screen.getByText('Current Schedule (1/5)')).toBeInTheDocument();
  });

  it('renders the course code and name for each enrolled section', () => {
    renderWithStore(<Schedule />, {
      preloadedState: {
        student: { ...baseStudentState, schedule: [mockEnrollment] },
        enrollment: baseEnrollmentState,
      },
    });
    expect(screen.getByText('ENG101')).toBeInTheDocument();
    expect(screen.getByText('English Literature')).toBeInTheDocument();
  });

  it('renders teacher name, classroom, and days for each enrollment', () => {
    renderWithStore(<Schedule />, {
      preloadedState: {
        student: { ...baseStudentState, schedule: [mockEnrollment] },
        enrollment: baseEnrollmentState,
      },
    });
    expect(screen.getByText('Mr. Johnson')).toBeInTheDocument();
    expect(screen.getByText('Room 101')).toBeInTheDocument();
    expect(screen.getByText('MWF')).toBeInTheDocument();
  });

  it('renders a Drop button for each enrolled course', () => {
    renderWithStore(<Schedule />, {
      preloadedState: {
        student: { ...baseStudentState, schedule: [mockEnrollment] },
        enrollment: baseEnrollmentState,
      },
    });
    expect(screen.getByRole('button', { name: /drop english literature/i })).toBeInTheDocument();
  });

  it('disables the Drop button while an enrollment action is loading', () => {
    renderWithStore(<Schedule />, {
      preloadedState: {
        student: { ...baseStudentState, schedule: [mockEnrollment] },
        enrollment: { loading: true, error: null, successMessage: null },
      },
    });
    expect(screen.getByRole('button', { name: /drop english literature/i })).toBeDisabled();
  });
});
