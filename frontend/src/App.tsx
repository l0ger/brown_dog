import './App.css';
import { useSelector } from 'react-redux';
import { RootState } from './store/store';
import StudentSelector from './components/StudentSelector';
import StudentProfile from './components/StudentProfile';
import Schedule from './components/Schedule';
import CourseCatalog from './components/CourseCatalog';

export default function App() {
  const profile = useSelector((s: RootState) => s.student.profile);

  return (
    <div className="appWrapper">
      <StudentSelector />
      <div className="app-body">
        {!profile ? (
          <div className="app-empty">
            <h2>Welcome to Maplewood Course Planning</h2>
            <p>Enter a student ID (1–400) above to load a student profile and start enrolling in courses.</p>
          </div>
        ) : (
          <>
            <div className="app-left">
              <StudentProfile />
              <Schedule />
            </div>
            <div className="app-right">
              <CourseCatalog />
            </div>
          </>
        )}
      </div>
    </div>
  );
}
