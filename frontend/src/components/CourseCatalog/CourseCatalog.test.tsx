import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import {
  renderWithStore,
  mockStudent,
  mockSection,
  mockElectiveSection,
  mockEnrollment,
} from '../../test-utils';
import CourseCatalog from './CourseCatalog';

// Prevent thunks from hitting the real API
jest.mock('../../api/api-client', () => ({
  sectionsApi: {
    getAll: jest.fn().mockResolvedValue({ data: [] }),
  },
  enrollmentsApi: {
    enroll: jest.fn().mockResolvedValue({ data: {} }),
    drop: jest.fn().mockResolvedValue({ data: {} }),
  },
}));

const baseState = {
  student: { profile: mockStudent, schedule: [], progress: null, loading: false, error: null },
  sections: { items: [mockSection, mockElectiveSection], loading: false, error: null },
  enrollment: { loading: false, error: null, successMessage: null },
};

describe('CourseCatalog', () => {
  it('shows a loading message while sections are being fetched', () => {
    renderWithStore(<CourseCatalog />, {
      preloadedState: {
        ...baseState,
        sections: { items: [], loading: true, error: null },
      },
    });
    expect(screen.getByText('Loading sections…')).toBeInTheDocument();
  });

  it('renders a card for each available section', () => {
    renderWithStore(<CourseCatalog />, { preloadedState: baseState });
    expect(screen.getByText('ENG101')).toBeInTheDocument();
    expect(screen.getByText('ART201')).toBeInTheDocument();
  });

  it('displays the section count in the heading', () => {
    renderWithStore(<CourseCatalog />, { preloadedState: baseState });
    expect(screen.getByText(/2 sections/)).toBeInTheDocument();
  });

  it('filters sections by course name as the user types', async () => {
    const user = userEvent.setup();
    renderWithStore(<CourseCatalog />, { preloadedState: baseState });
    await user.type(screen.getByPlaceholderText('Search by name or code…'), 'English');
    expect(screen.getByText('ENG101')).toBeInTheDocument();
    expect(screen.queryByText('ART201')).not.toBeInTheDocument();
  });

  it('filters sections by course code', async () => {
    const user = userEvent.setup();
    renderWithStore(<CourseCatalog />, { preloadedState: baseState });
    await user.type(screen.getByPlaceholderText('Search by name or code…'), 'ART');
    expect(screen.getByText('ART201')).toBeInTheDocument();
    expect(screen.queryByText('ENG101')).not.toBeInTheDocument();
  });

  it('shows only core sections when the Core filter is active', async () => {
    const user = userEvent.setup();
    renderWithStore(<CourseCatalog />, { preloadedState: baseState });
    await user.click(screen.getByRole('button', { name: 'Core' }));
    expect(screen.getByText('ENG101')).toBeInTheDocument();
    expect(screen.queryByText('ART201')).not.toBeInTheDocument();
  });

  it('shows only elective sections when the Elective filter is active', async () => {
    const user = userEvent.setup();
    renderWithStore(<CourseCatalog />, { preloadedState: baseState });
    await user.click(screen.getByRole('button', { name: 'Elective' }));
    expect(screen.getByText('ART201')).toBeInTheDocument();
    expect(screen.queryByText('ENG101')).not.toBeInTheDocument();
  });

  it('shows "✓ Enrolled" and disables the button for already-enrolled sections', () => {
    renderWithStore(<CourseCatalog />, {
      preloadedState: {
        ...baseState,
        student: { ...baseState.student, schedule: [mockEnrollment] },
      },
    });
    // With one section enrolled and one not, there is exactly one "✓ Enrolled" button
    const enrolledBtn = screen.getByRole('button', { name: '✓ Enrolled' });
    expect(enrolledBtn).toBeDisabled();
  });

  it('shows "Enroll" and enables the button for sections not yet enrolled', () => {
    renderWithStore(<CourseCatalog />, {
      preloadedState: {
        ...baseState,
        student: { ...baseState.student, schedule: [mockEnrollment] },
      },
    });
    // With one section enrolled and one not, there is exactly one "Enroll" button
    const enrollBtn = screen.getByRole('button', { name: 'Enroll' });
    expect(enrollBtn).not.toBeDisabled();
  });

  it('shows an error toast when enrollment is rejected', () => {
    renderWithStore(<CourseCatalog />, {
      preloadedState: {
        ...baseState,
        enrollment: {
          loading: false,
          error: { type: 'prerequisite', message: 'Missing prerequisite' },
          successMessage: null,
        },
      },
    });
    expect(screen.getByText('PREREQUISITE: Missing prerequisite')).toBeInTheDocument();
  });

  it('shows a success toast when successMessage is set', () => {
    renderWithStore(<CourseCatalog />, {
      preloadedState: {
        ...baseState,
        enrollment: { loading: false, error: null, successMessage: 'Enrolled in English Literature' },
      },
    });
    expect(screen.getByText('Enrolled in English Literature')).toBeInTheDocument();
  });
});
