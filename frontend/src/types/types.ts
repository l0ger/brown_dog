export interface Course {
  id: number;
  code: string;
  name: string;
  description: string;
  credits: number;
  hoursPerWeek: number;
  courseType: 'core' | 'elective';
  gradeLevelMin: number;
  gradeLevelMax: number;
  semesterOrder: number;
  specialization: string;
  prerequisiteId: number | null;
  prerequisiteName: string | null;
}

export interface Student {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  gradeLevel: number;
  enrollmentYear: number;
  expectedGraduationYear: number | null;
  status: string;
}

export interface StudentProgress {
  studentId: number;
  fullName: string;
  gradeLevel: number;
  gpa: number;
  creditsEarned: number;
  creditsRequired: number;
  canGraduate: boolean;
}

export interface Section {
  id: number;
  course: Course;
  teacherName: string;
  classroom: string;
  daysOfWeek: string;
  startTime: string;
  endTime: string;
}

export interface Enrollment {
  id: number;
  studentId: number;
  section: Section;
  status: string;
}

export interface ApiError {
  type: 'prerequisite' | 'conflict' | 'max_courses' | 'grade_level' | 'not_found' | 'error';
  message: string;
}
