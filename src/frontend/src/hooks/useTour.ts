import { useCallback, useRef, useState, useMemo } from 'react';

export interface TourStep {
  key: string;
  title: string;
  description: string;
}

export function useTour(steps: TourStep[]) {
  const [isOpen, setIsOpen] = useState(false);
  const [currentStep, setCurrentStep] = useState(0);
  const targets = useRef<(HTMLElement | null)[]>([]);

  // 스크롤 잠금
  const lockScroll = useCallback(() => {
    document.body.style.overflow = 'hidden';
  }, []);
  const unlockScroll = useCallback(() => {
    document.body.style.overflow = '';
  }, []);

  const start = useCallback(() => {
    setIsOpen(true);
    setCurrentStep(0);
    lockScroll();
  }, [lockScroll]);

  const end = useCallback(() => {
    setIsOpen(false);
    setCurrentStep(0);
    unlockScroll();
  }, [unlockScroll]);

  const next = useCallback(() => {
    if (currentStep < steps.length - 1) {
      setCurrentStep(currentStep + 1);
    }
  }, [currentStep, steps.length]);

  const prev = useCallback(() => {
    if (currentStep > 0) {
      setCurrentStep(currentStep - 1);
    }
  }, [currentStep]);

  // 각 타겟 ref 등록용 콜백
  const register = useCallback(
    (idx: number) => (el: HTMLElement | null) => {
      targets.current[idx] = el;
    },
    []
  );

  const targetRef = useMemo(() => {
    return { current: targets.current[currentStep] };
  }, [currentStep]);

  return {
    isOpen,
    currentStep,
    step: steps[currentStep],
    steps,
    start,
    end,
    next,
    prev,
    register,
    targets: targets.current,
    targetRef,
  };
}
