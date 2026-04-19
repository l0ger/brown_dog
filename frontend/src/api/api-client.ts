import axios from 'axios';
import type { Course, Student, StudentProgress, Section, Enrollment } from '../types/types';

const apiClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

export const coursesApi = {
  getAll: (gradeLevel?: number) =>
    apiClient.get<Course[]>('/courses', { params: gradeLevel ? { gradeLevel } : {} }),
  getById: (id: number) => apiClient.get<Course>(`/courses/${id}`),
};

export const studentsApi = {
  getById: (id: number) => apiClient.get<Student>(`/students/${id}`),
  getSchedule: (id: number) => apiClient.get<Enrollment[]>(`/students/${id}/schedule`),
  getProgress: (id: number) => apiClient.get<StudentProgress>(`/students/${id}/progress`),
};

export const sectionsApi = {
  getAll: (gradeLevel?: number) =>
    apiClient.get<Section[]>('/sections', { params: gradeLevel ? { gradeLevel } : {} }),
};

export const enrollmentsApi = {
  enroll: (studentId: number, sectionId: number) =>
    apiClient.post<Enrollment>('/enrollments', { studentId, sectionId }),
  drop: (enrollmentId: number, studentId: number) =>
    apiClient.delete(`/enrollments/${enrollmentId}`, { params: { studentId } }),
};
