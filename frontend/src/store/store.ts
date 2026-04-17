import { configureStore } from '@reduxjs/toolkit';
import coursesReducer from './slices/coursesSlice';
import sectionsReducer from './slices/sectionsSlice';
import studentReducer from './slices/studentSlice';
import enrollmentReducer from './slices/enrollmentSlice';

export const store = configureStore({
  reducer: {
    courses: coursesReducer,
    sections: sectionsReducer,
    student: studentReducer,
    enrollment: enrollmentReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
