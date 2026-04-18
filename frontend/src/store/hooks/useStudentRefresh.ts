import { useDispatch } from 'react-redux';
import { AppDispatch } from '../store';
import { fetchSchedule, fetchProgress } from '../slices/studentSlice';

/**
 * Returns a function that refreshes both the student's schedule and progress.
 * Call after any enrollment action (enroll or drop).
 */
export function useStudentRefresh() {
  const dispatch = useDispatch<AppDispatch>();
  return (studentId: number) => {
    dispatch(fetchSchedule(studentId));
    dispatch(fetchProgress(studentId));
  };
}
