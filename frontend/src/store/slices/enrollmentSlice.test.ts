import enrollmentReducer, { clearEnrollmentStatus, enroll, drop } from './enrollmentSlice';
import type { ApiError } from '../../types/types';
import { mockEnrollment } from '../../test-utils';

const initialState = { loading: false, error: null, successMessage: null };

describe('enrollmentSlice', () => {
  describe('initial state', () => {
    it('returns the correct initial state', () => {
      expect(enrollmentReducer(undefined, { type: '@@INIT' })).toEqual(initialState);
    });
  });

  describe('clearEnrollmentStatus', () => {
    it('clears error and successMessage', () => {
      const dirtyState = {
        loading: false,
        error: { type: 'error' as const, message: 'something went wrong' },
        successMessage: 'Enrolled in English Literature',
      };
      expect(enrollmentReducer(dirtyState, clearEnrollmentStatus())).toEqual(initialState);
    });
  });

  describe('enroll thunk', () => {
    const arg = { studentId: 1, sectionId: 1 };

    it('sets loading on pending and clears previous status', () => {
      const state = enrollmentReducer(
        { loading: false, error: { type: 'error' as const, message: 'old error' }, successMessage: null },
        enroll.pending('id', arg),
      );
      expect(state.loading).toBe(true);
      expect(state.error).toBeNull();
    });

    it('sets successMessage with the enrolled course name on fulfilled', () => {
      const state = enrollmentReducer(initialState, enroll.fulfilled(mockEnrollment, 'id', arg));
      expect(state.loading).toBe(false);
      expect(state.successMessage).toBe('Enrolled in English Literature');
      expect(state.error).toBeNull();
    });

    it('sets error payload on rejected', () => {
      const err: ApiError = { type: 'prerequisite', message: 'Missing prerequisite' };
      const state = enrollmentReducer(initialState, enroll.rejected(null, 'id', arg, err));
      expect(state.loading).toBe(false);
      expect(state.error).toEqual(err);
      expect(state.successMessage).toBeNull();
    });
  });

  describe('drop thunk', () => {
    const arg = { enrollmentId: 1, studentId: 1 };

    it('sets loading on pending and clears error', () => {
      const state = enrollmentReducer(
        { loading: false, error: { type: 'error' as const, message: 'old' }, successMessage: null },
        drop.pending('id', arg),
      );
      expect(state.loading).toBe(true);
      expect(state.error).toBeNull();
    });

    it('sets successMessage on fulfilled', () => {
      const state = enrollmentReducer(initialState, drop.fulfilled(1, 'id', arg));
      expect(state.loading).toBe(false);
      expect(state.successMessage).toBe('Course dropped successfully');
    });

    it('sets error payload on rejected', () => {
      const err: ApiError = { type: 'not_found', message: 'Enrollment not found' };
      const state = enrollmentReducer(initialState, drop.rejected(null, 'id', arg, err));
      expect(state.loading).toBe(false);
      expect(state.error).toEqual(err);
    });
  });
});
