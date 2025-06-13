import { useCallback, useRef, useState } from 'react';

export interface TourStep {
  key: string;
  title: string;
  description: string;
}

export function useTour(steps: TourStep[]) {
  const [isOpen, setIsOpen] = useState(false);
  const [current, setCurrent] = useState(0);
  const targets = useRef<(HTMLElement | null)[]>([]);
  const targetRef = useRef<HTMLElement | null>(null);

  // 스크롤 잠금
  const lockScroll = useCallback(() => {
    document.body.style.overflow = 'hidden';
  }, []);
  const unlockScroll = useCallback(() => {
    document.body.style.overflow = '';
  }, []);

  const start = useCallback(() => {
    setIsOpen(true);
    setCurrent(0);
    lockScroll();
  }, [lockScroll]);

  const end = useCallback(() => {
    setIsOpen(false);
    setCurrent(0);
    unlockScroll();
  }, [unlockScroll]);

  const next = useCallback(() => {
    if (current < steps.length - 1) {
      setCurrent(current + 1);
    }
  }, [current, steps.length]);

  const prev = useCallback(() => {
    if (current > 0) {
      setCurrent(current - 1);
    }
  }, [current]);

  // 각 타겟 ref 등록용 콜백
  const register = useCallback(
    (idx: number) => (el: HTMLElement | null) => {
      targets.current[idx] = el;
      if (idx === current) {
        targetRef.current = el;
      }
    },
    [current]
  );

  // current가 변경될 때마다 targetRef 업데이트
  const updateTargetRef = useCallback(() => {
    targetRef.current = targets.current[current];
  }, [current]);

  // current가 변경될 때마다 targetRef 업데이트
  updateTargetRef();

  return {
    isOpen,
    current,
    step: steps[current],
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
