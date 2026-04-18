import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import axios from 'axios';
import { enrollmentsApi } from '../../api/api-client';
import type { ApiError } from '../../types/types';

export const enroll = createAsyncThunk(
  'enrollment/enroll',
  async ({ studentId, sectionId }: { studentId: number; sectionId: number }, { rejectWithValue }) => {
    try {
      const res = await enrollmentsApi.enroll(studentId, sectionId);
      return res.data;
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        return rejectWithValue(err.response?.data as ApiError);
      }
      return rejectWithValue({ type: 'error', message: 'An unexpected error occurred' } as ApiError);
    }
  }
);

export const drop = createAsyncThunk(
  'enrollment/drop',
  async ({ enrollmentId, studentId }: { enrollmentId: number; studentId: number }, { rejectWithValue }) => {
    try {
      await enrollmentsApi.drop(enrollmentId, studentId);
      return enrollmentId;
    } catch (err: unknown) {
      if (axios.isAxiosError(err)) {
        return rejectWithValue(err.response?.data as ApiError);
      }
      return rejectWithValue({ type: 'error', message: 'An unexpected error occurred' } as ApiError);
    }
  }
);

interface EnrollmentState {
  loading: boolean;
  error: ApiError | null;
  successMessage: string | null;
}

const initialState: EnrollmentState = { loading: false, error: null, successMessage: null };

const enrollmentSlice = createSlice({
  name: 'enrollment',
  initialState,
  reducers: {
    clearEnrollmentStatus: (state) => {
      state.error = null;
      state.successMessage = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(enroll.pending, (state) => { state.loading = true; state.error = null; state.successMessage = null; })
      .addCase(enroll.fulfilled, (state, action) => {
        state.loading = false;
        state.successMessage = `Enrolled in ${action.payload.section.course.name}`;
      })
      .addCase(enroll.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload as ApiError;
      })
      .addCase(drop.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(drop.fulfilled, (state) => { state.loading = false; state.successMessage = 'Course dropped successfully'; })
      .addCase(drop.rejected, (state, action) => { state.loading = false; state.error = action.payload as ApiError; });
  },
});

export const { clearEnrollmentStatus } = enrollmentSlice.actions;
export default enrollmentSlice.reducer;
