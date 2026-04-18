import React from 'react';
import clsx from 'clsx';
import shared from '../../styles/shared.module.css';

interface CardProps {
  variant?: 'default' | 'muted';
  overflow?: boolean;
  className?: string;
  children: React.ReactNode;
}

export default function Card({ variant = 'default', overflow = false, className, children }: CardProps) {
  return (
    <div className={clsx(
      variant === 'muted' ? shared.cardMuted : shared.card,
      overflow && shared.cardOverflow,
      className,
    )}>
      {children}
    </div>
  );
}
