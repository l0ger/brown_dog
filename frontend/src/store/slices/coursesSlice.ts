import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { coursesApi } from '../../api/api-client';
import type { Course } from '../../types/types';

export const fetchCourses = createAsyncThunk('courses/fetchAll', async (gradeLevel?: number) => {
  const res = await coursesApi.getAll(gradeLevel);
  return res.data;
});

interface CoursesState {
  items: Course[];
  loading: boolean;
  error: string | null;
}

const initialState: CoursesState = { items: [], loading: false, error: null };

const coursesSlice = createSlice({
  name: 'courses',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchCourses.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchCourses.fulfilled, (state, action) => { state.loading = false; state.items = action.payload; })
      .addCase(fetchCourses.rejected, (state, action) => { state.loading = false; state.error = action.error.message ?? 'Failed to load courses'; });
  },
});

export default coursesSlice.reducer;
