---
name: frontend
description: Best practices guide for React + TypeScript frontend development, covering component design, typing, state management, and styling.Load for any React, TypeScript, or CSS task: creating components, writing hooks, typing props, or styling UI
tools: Read, Grep, Glob, Bash, Edit, Write
---

# Frontend Agent — React + TypeScript Best Practices

React 18 + TypeScript frontend. Read existing components before modifying them. Never bypass the type system with `any`. Keep components small and single-purpose.

---

## TypeScript Rules

### Type everything at the boundary, infer everything inside

- Define explicit types for props, API responses, and Redux state shapes.
- Let TypeScript infer local variables, return types of simple functions, and useState initial values.
- Never use `any`. Use `unknown` when the type is genuinely unknown, then narrow it.

```ts
// Bad
const handleClick = (e: any) => { ... }

// Good
const handleClick = (e: React.MouseEvent<HTMLButtonElement>) => { ... }
```

### Prefer interfaces for object shapes, types for unions and aliases

```ts
// Object shape → interface
interface Student {
  id: number;
  firstName: string;
  lastName: string;
  status: 'active' | 'inactive';   // union lives on the property
}

// Union / alias → type
type CourseType = 'core' | 'elective';
type LoadingState = 'idle' | 'loading' | 'success' | 'error';
```

### Use discriminated unions for API error / state modeling

```ts
type AsyncResult<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; error: string };
```

### Type component props explicitly — never use React.FC

`React.FC` implicitly adds `children` and hides the return type. Instead:

```ts
// Bad
const Card: React.FC<{ title: string }> = ({ title }) => ...

// Good
interface CardProps {
  title: string;
  children?: React.ReactNode;
}
function Card({ title, children }: CardProps) { ... }
```

### Avoid non-null assertions (`!`) — narrow with guards instead

```ts
// Bad
const name = student!.firstName;

// Good
if (!student) return null;
const name = student.firstName;
```

---

## Component Design

### One responsibility per component

A component does one thing. If it fetches data, formats it, AND handles user interaction, split it.

| Responsibility | Layer |
|---|---|
| Fetching & dispatching | Container / Page component or custom hook |
| Rendering & UI interaction | Presentational component |
| Reusable logic | Custom hook (`use*.ts`) |

### Presentational components receive data and callbacks via props

```ts
// Presentational — no Redux, no fetch, no side effects
function StudentCard({ student, onSelect }: StudentCardProps) {
  return (
    <div className="student-card" onClick={() => onSelect(student.id)}>
      <span>{student.firstName} {student.lastName}</span>
    </div>
  );
}
```

### Container components own state and dispatch

```ts
// Container — Redux-aware, passes data down
function StudentCardContainer({ studentId }: { studentId: number }) {
  const student = useSelector((s: RootState) => selectStudentById(s, studentId));
  const dispatch = useDispatch<AppDispatch>();
  return <StudentCard student={student} onSelect={(id) => dispatch(selectStudent(id))} />;
}
```

### Keep JSX readable — extract complex logic out of JSX

```tsx
// Bad — logic inside JSX
<div style={{ background: isEnrolled ? '#28a745' : loading ? '#aaa' : '#0f3460' }}>

// Good — compute before return
const btnBackground = isEnrolled ? '#28a745' : loading ? '#aaa' : '#0f3460';
<div style={{ background: btnBackground }}>
```

### Early returns for guard clauses

```tsx
function StudentProfile({ studentId }: Props) {
  const { profile, loading, error } = useSelector(...);

  if (loading) return <Spinner />;
  if (error)   return <ErrorMessage message={error} />;
  if (!profile) return null;

  return <div>...</div>;   // happy path, no nesting
}
```

### Colocate related state — avoid over-splitting useState

```ts
// Fragmented — harder to reason about
const [firstName, setFirstName] = useState('');
const [lastName, setLastName]   = useState('');
const [email, setEmail]         = useState('');

// Colocated — one update, one reset
const [form, setForm] = useState({ firstName: '', lastName: '', email: '' });
const updateField = (field: keyof typeof form) =>
  (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm(prev => ({ ...prev, [field]: e.target.value }));
```

---

## Hooks

### Rules of Hooks (non-negotiable)

- Only call hooks at the top level — never inside loops, conditions, or nested functions.
- Only call hooks from React function components or custom hooks.

### Custom hooks encapsulate complex logic

Extract any hook combination that is used in more than one place, or that makes a component hard to read.

```ts
// Before: logic spread across component
function CourseCatalog() {
  const dispatch = useDispatch<AppDispatch>();
  const sections = useSelector((s: RootState) => s.sections.items);
  const loading  = useSelector((s: RootState) => s.sections.loading);
  useEffect(() => { if (sections.length === 0) dispatch(fetchSections()); }, []);
  // ... more logic
}

// After: extracted hook
function useSections() {
  const dispatch = useDispatch<AppDispatch>();
  const { items, loading } = useSelector((s: RootState) => s.sections);
  useEffect(() => { if (items.length === 0) dispatch(fetchSections()); }, [dispatch, items.length]);
  return { sections: items, loading };
}
```

### Always specify useEffect dependency arrays — never omit them

```ts
// Bad — runs on every render
useEffect(() => { fetchData(); });

// Bad — stale closure risk
useEffect(() => { doSomethingWith(value); }, []);

// Good — explicit and complete
useEffect(() => { doSomethingWith(value); }, [value]);
```

### useCallback and useMemo — only when measurably needed

Do not wrap every function in `useCallback`. Apply it when:
- A callback is passed as a prop to a memoized child (`React.memo`).
- A function is a dependency of `useEffect`.

Apply `useMemo` only for expensive computations (sorting large lists, complex derivations), not for simple property access.

### Cleanup side effects

```ts
useEffect(() => {
  const timer = setTimeout(() => dispatch(clearStatus()), 4000);
  return () => clearTimeout(timer);   // always clean up
}, [successMessage, error, dispatch]);
```

---

## State Management (Redux Toolkit)

### Use `createAsyncThunk` for all API calls

```ts
export const fetchStudent = createAsyncThunk(
  'student/fetchById',
  async (id: number, { rejectWithValue }) => {
    try {
      const res = await studentsApi.getById(id);
      return res.data;
    } catch (err: unknown) {
      const message = err instanceof AxiosError ? err.response?.data?.message : 'Unknown error';
      return rejectWithValue(message);
    }
  }
);
```

### Slice state interfaces must be fully typed — no implicit `any`

```ts
interface SectionsState {
  items: Section[];
  loading: boolean;
  error: string | null;
}
```

### Selectors belong in the slice file or a dedicated `selectors.ts`

```ts
// In sectionsSlice.ts
export const selectFilteredSections = (state: RootState, filter: string): Section[] =>
  state.sections.items.filter(s =>
    s.course.name.toLowerCase().includes(filter.toLowerCase())
  );
```

### Typed dispatch and selector — never use the untyped versions

```ts
// Always import typed versions
import type { RootState, AppDispatch } from '../store/store';
const dispatch = useDispatch<AppDispatch>();
const profile  = useSelector((s: RootState) => s.student.profile);
```

---

## Styling

### Styling approach decision: pick one and stay consistent

| Approach | When to use |
|---|---|
| CSS Modules (`*.module.css`) | Component-scoped styles, no runtime cost, best for most React projects |
| Inline `style` objects | Truly dynamic values (e.g. progress bar width percentage) |
| Global CSS classes | Layout primitives, resets, typography — things shared across the app |
| CSS-in-JS (styled-components, Emotion) | Design-system libraries, heavy theming; avoid for simple apps |

### CSS Modules are the default for component styles

```tsx
// StudentProfile.module.css
.card { background: #fff; border-radius: 8px; padding: 16px; box-shadow: 0 1px 4px rgba(0,0,0,.1); }
.label { font-size: 11px; color: #888; text-transform: uppercase; letter-spacing: 0.5px; }

// StudentProfile.tsx
import styles from './StudentProfile.module.css';
<div className={styles.card}>
  <span className={styles.label}>GPA</span>
```

### Reserve inline styles for computed values only

```tsx
// Bad — static value inline
<div style={{ borderRadius: 8, padding: 16, background: '#fff' }}>

// Good — only the dynamic part is inline
const fillWidth = `${Math.min(100, (earned / required) * 100)}%`;
<div className={styles.barFill} style={{ width: fillWidth }} />
```

### Design tokens: use CSS custom properties for colors, spacing, and type

Define tokens once in `index.css` or a `tokens.css` file:

```css
:root {
  --color-primary:   #0f3460;
  --color-surface:   #ffffff;
  --color-bg:        #f0f2f5;
  --color-danger:    #dc3545;
  --color-success:   #28a745;
  --color-text-muted:#888888;

  --radius-sm: 4px;
  --radius-md: 8px;

  --space-xs: 4px;
  --space-sm: 8px;
  --space-md: 16px;
  --space-lg: 24px;
}
```

Reference tokens in CSS Modules:

```css
.card {
  background: var(--color-surface);
  border-radius: var(--radius-md);
  padding: var(--space-md);
}
```

### Responsive design: mobile-first with breakpoints in CSS

```css
/* Base: mobile */
.appBody {
  display: flex;
  flex-direction: column;
  padding: var(--space-sm);
  gap: var(--space-sm);
}

/* Tablet ≥ 600px */
@media (min-width: 600px) {
  .appBody { padding: var(--space-md); }
}

/* Desktop ≥ 900px */
@media (min-width: 900px) {
  .appBody {
    flex-direction: row;
    padding: var(--space-lg);
  }
}
```

### Conditional class names — use a helper, not string concatenation

```tsx
// Bad — fragile concatenation
<button className={'btn' + (isActive ? ' btn--active' : '')}>

// Good — clsx or classnames library
import clsx from 'clsx';
<button className={clsx(styles.btn, isActive && styles.btnActive, disabled && styles.disabled)}>
```

### Accessibility is not optional

- Interactive elements must be focusable and keyboard-operable.
- Buttons must have a visible focus ring and descriptive label.
- Use semantic HTML (`<button>` not `<div onClick>`).
- Provide `aria-label` when the visual label is insufficient.
- Color alone must never convey meaning — pair with icon or text.

```tsx
// Bad
<div onClick={handleDrop} style={{ cursor: 'pointer' }}>Drop</div>

// Good
<button onClick={handleDrop} aria-label={`Drop ${courseName}`}>Drop</button>
```

---

## File and Folder Conventions

```
src/
├── api/                  # API client functions only — no business logic
├── components/           # Presentational components
├── pages/                # Route-level containers (if using a router)
├── hooks/                # Custom hooks (use*.ts)
├── store/
│   ├── store.ts
│   └── slices/           # One file per domain slice
├── types/                # Shared TypeScript interfaces and types
└── styles/               # Global CSS, tokens, resets
```

- One component per file. Filename matches component name: `StudentProfile.tsx`.
- CSS Module lives next to its component: `StudentProfile.module.css`.
- Custom hook lives in `hooks/`: `useEnrollment.ts`.
- Keep `index.ts` barrel files only where they genuinely simplify imports — never create them preemptively.

---

## Non-negotiable Rules

- No `any`. Use `unknown` + type guards or proper generics.
- No `React.FC` — use explicit prop interfaces and plain functions.
- No inline styles for static values — use CSS Modules or global classes.
- No direct DOM manipulation — let React own the DOM.
- No side effects outside `useEffect` or event handlers.
- Components must not call APIs directly — go through the Redux slice or a custom hook.
- Every `useEffect` must have a complete dependency array and must clean up subscriptions/timers.
- Accessibility: every interactive element must be keyboard-reachable with a visible focus state.
