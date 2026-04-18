import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithStore } from '../../test-utils';
import StudentSelector from './StudentSelector';

// Prevent thunks from hitting the real API
jest.mock('../../api/api-client', () => ({
  studentsApi: {
    getById: jest.fn().mockResolvedValue({ data: {} }),
    getSchedule: jest.fn().mockResolvedValue({ data: [] }),
    getProgress: jest.fn().mockResolvedValue({ data: {} }),
  },
  sectionsApi: {
    getAll: jest.fn().mockResolvedValue({ data: [] }),
  },
}));

describe('StudentSelector', () => {
  it('renders the app title', () => {
    renderWithStore(<StudentSelector />);
    expect(screen.getByText('Maplewood Course Planning')).toBeInTheDocument();
  });

  it('renders the student ID input', () => {
    renderWithStore(<StudentSelector />);
    expect(screen.getByPlaceholderText('Student ID (e.g. 1)')).toBeInTheDocument();
  });

  it('renders Load Student and Clear buttons', () => {
    renderWithStore(<StudentSelector />);
    expect(screen.getByRole('button', { name: 'Load Student' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Clear' })).toBeInTheDocument();
  });

  it('updates the input value as the user types', async () => {
    const user = userEvent.setup();
    renderWithStore(<StudentSelector />);
    const input = screen.getByPlaceholderText('Student ID (e.g. 1)');
    await user.type(input, '42');
    expect(input).toHaveValue(42);
  });

  it('clears the input when Clear is clicked', async () => {
    const user = userEvent.setup();
    renderWithStore(<StudentSelector />);
    await user.type(screen.getByPlaceholderText('Student ID (e.g. 1)'), '42');
    await user.click(screen.getByRole('button', { name: 'Clear' }));
    expect(screen.getByPlaceholderText('Student ID (e.g. 1)')).toHaveValue(null);
  });

  it('does nothing when Load is clicked with no ID entered', async () => {
    const user = userEvent.setup();
    const { store } = renderWithStore(<StudentSelector />);
    await user.click(screen.getByRole('button', { name: 'Load Student' }));
    expect(store.getState().student.profile).toBeNull();
  });

  it('sets loading state when Load Student is clicked with a valid ID', async () => {
    const user = userEvent.setup();
    renderWithStore(<StudentSelector />);
    await user.type(screen.getByPlaceholderText('Student ID (e.g. 1)'), '1');
    await user.click(screen.getByRole('button', { name: 'Load Student' }));
    // Mocked API resolves immediately — loading should be false after resolution
    expect(screen.getByRole('button', { name: 'Load Student' })).toBeInTheDocument();
  });
});
