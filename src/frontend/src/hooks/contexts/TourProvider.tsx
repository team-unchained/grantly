'use client';

import React, { createContext, useContext, ReactNode } from 'react';
import { useTour, TourStep } from '@grantly/hooks/useTour';
import { TourOverlay } from '@grantly/components/common/TourOverlay';

interface TourContextType {
  isOpen: boolean;
  currentStep: number;
  step: TourStep | null;
  steps: TourStep[];
  start: () => void;
  end: () => void;
  next: () => void;
  prev: () => void;
  register: (idx: number) => (el: HTMLElement | null) => void;
  targetRef: React.RefObject<HTMLElement | null>;
}

const TourContext = createContext<TourContextType | undefined>(undefined);

interface TourProviderProps {
  children: ReactNode;
  steps: TourStep[];
}

export const TourProvider: React.FC<TourProviderProps> = ({
  children,
  steps,
}) => {
  const tour = useTour(steps);

  return (
    <TourContext.Provider value={tour}>
      {children}
      <TourOverlay />
    </TourContext.Provider>
  );
};

export const useTourContext = () => {
  const context = useContext(TourContext);
  if (!context) {
    throw new Error('useTourContext must be used within a TourProvider');
  }
  return context;
};
