import studentReducer, { clearStudent, fetchStudent, fetchSchedule, fetchProgress } from './studentSlice';
import { mockStudent, mockProgress, mockEnrollment } from '../../test-utils';

const initialState = {
  profile: null,
  schedule: [],
  progress: null,
  loading: false,
  error: null,
};

describe('studentSlice', () => {
  describe('initial state', () => {
    it('returns the correct initial state', () => {
      expect(studentReducer(undefined, { type: '@@INIT' })).toEqual(initialState);
    });
  });

  describe('clearStudent', () => {
    it('resets all fields to initial state', () => {
      const loadedState = {
        profile: mockStudent,
        schedule: [mockEnrollment],
        progress: mockProgress,
        loading: false,
        error: null,
      };
      expect(studentReducer(loadedState, clearStudent())).toEqual(initialState);
    });
  });

  describe('fetchStudent thunk', () => {
    it('sets loading on pending and clears error', () => {
      const state = studentReducer(
        { ...initialState, error: 'previous error' },
        fetchStudent.pending('id', 1),
      );
      expect(state.loading).toBe(true);
      expect(state.error).toBeNull();
    });

    it('sets profile on fulfilled', () => {
      const state = studentReducer(initialState, fetchStudent.fulfilled(mockStudent, 'id', 1));
      expect(state.loading).toBe(false);
      expect(state.profile).toEqual(mockStudent);
    });

    it('sets error message on rejected', () => {
      const action = fetchStudent.rejected(new Error('Student not found'), 'id', 1);
      const state = studentReducer(initialState, action);
      expect(state.loading).toBe(false);
      expect(state.error).toBe('Student not found');
      expect(state.profile).toBeNull();
    });

    it('uses fallback message when error has no message', () => {
      const action = fetchStudent.rejected(new Error(), 'id', 1);
      action.error.message = undefined;
      const state = studentReducer(initialState, action);
      expect(state.error).toBe('Student not found');
    });
  });

  describe('fetchSchedule thunk', () => {
    it('sets schedule on fulfilled', () => {
      const state = studentReducer(initialState, fetchSchedule.fulfilled([mockEnrollment], 'id', 1));
      expect(state.schedule).toEqual([mockEnrollment]);
    });
  });

  describe('fetchProgress thunk', () => {
    it('sets progress on fulfilled', () => {
      const state = studentReducer(initialState, fetchProgress.fulfilled(mockProgress, 'id', 1));
      expect(state.progress).toEqual(mockProgress);
    });
  });
});
