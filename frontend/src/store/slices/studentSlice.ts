import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { studentsApi } from '../../api/api-client';
import type { Student, StudentProgress, Enrollment } from '../../types/types';

export const fetchStudent = createAsyncThunk('student/fetchById', async (id: number) => {
  const res = await studentsApi.getById(id);
  return res.data;
});

export const fetchSchedule = createAsyncThunk('student/fetchSchedule', async (id: number) => {
  const res = await studentsApi.getSchedule(id);
  return res.data;
});

export const fetchProgress = createAsyncThunk('student/fetchProgress', async (id: number) => {
  const res = await studentsApi.getProgress(id);
  return res.data;
});

interface StudentState {
  profile: Student | null;
  schedule: Enrollment[];
  progress: StudentProgress | null;
  loading: boolean;
  error: string | null;
}

const initialState: StudentState = {
  profile: null,
  schedule: [],
  progress: null,
  loading: false,
  error: null,
};

const studentSlice = createSlice({
  name: 'student',
  initialState,
  reducers: {
    clearStudent: () => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchStudent.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchStudent.fulfilled, (state, action) => { state.loading = false; state.profile = action.payload; })
      .addCase(fetchStudent.rejected, (state, action) => { state.loading = false; state.error = action.error.message ?? 'Student not found'; })
      .addCase(fetchSchedule.fulfilled, (state, action) => { state.schedule = action.payload; })
      .addCase(fetchProgress.fulfilled, (state, action) => { state.progress = action.payload; });
  },
});

export const { clearStudent } = studentSlice.actions;
export default studentSlice.reducer;
