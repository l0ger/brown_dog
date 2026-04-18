import clsx from 'clsx';
import shared from '../../styles/shared.module.css';

interface ToastProps {
  message: string;
  variant: 'success' | 'error';
}

export default function Toast({ message, variant }: ToastProps) {
  return (
    <div className={clsx(shared.toast, variant === 'error' ? shared.toastError : shared.toastSuccess)}>
      {message}
    </div>
  );
}
