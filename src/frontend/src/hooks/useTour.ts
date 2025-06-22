import { useCallback, useRef, useState, useMemo, useEffect } from 'react';

export interface TourStep {
  key: string;
  title: string;
  description: string;
}

const TOUR_COMPLETED_KEY = 'grantly_tour_completed';

export function useTour(steps: TourStep[]) {
  const [isOpen, setIsOpen] = useState(false);
  const [currentStep, setCurrentStep] = useState(-1);
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
    localStorage.setItem(TOUR_COMPLETED_KEY, 'true');
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

  useEffect(() => {
    if (localStorage.getItem(TOUR_COMPLETED_KEY) === 'true') return undefined;
    // 약간의 지연을 두어 페이지가 완전히 로드된 후 시작
    const timer = setTimeout(() => {
      start();
    }, 1000);

    return () => clearTimeout(timer);
  }, [start]);

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
