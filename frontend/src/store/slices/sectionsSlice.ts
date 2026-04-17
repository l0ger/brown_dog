import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { sectionsApi } from '../../api/api-client';
import type { Section } from '../../types/types';

export const fetchSections = createAsyncThunk('sections/fetchAll', async () => {
  const res = await sectionsApi.getAll();
  return res.data;
});

interface SectionsState {
  items: Section[];
  loading: boolean;
  error: string | null;
}

const initialState: SectionsState = { items: [], loading: false, error: null };

const sectionsSlice = createSlice({
  name: 'sections',
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchSections.pending, (state) => { state.loading = true; state.error = null; })
      .addCase(fetchSections.fulfilled, (state, action) => { state.loading = false; state.items = action.payload; })
      .addCase(fetchSections.rejected, (state, action) => { state.loading = false; state.error = action.error.message ?? 'Failed to load sections'; });
  },
});

export default sectionsSlice.reducer;
