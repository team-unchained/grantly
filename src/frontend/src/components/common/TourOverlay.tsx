'use client';

import React, { useMemo } from 'react';
import { useTourContext } from '@grantly/hooks/contexts/TourProvider';

export const TourOverlay: React.FC = () => {
  const { isOpen, step, targetRef, end, next, prev, currentStep, steps } =
    useTourContext();

  const modalPosition = useMemo(() => {
    if (!targetRef.current) {
      return null;
    }

    const targetElement = targetRef.current;
    const rect = targetElement.getBoundingClientRect();
    const modalWidth = 300; // max-w-[300px]
    const modalHeight = 200; // 예상 높이
    const margin = 16;

    // 화면 크기
    const viewportWidth = window.innerWidth;
    const viewportHeight = window.innerHeight;

    let top = rect.bottom + margin;
    let { left } = rect;

    // 하단 경계 체크 - 모달이 화면 하단을 벗어나는 경우
    if (top + modalHeight > viewportHeight) {
      // 타겟 요소 위에 배치
      top = rect.top - modalHeight - margin;
    }

    // 상단 경계 체크 - 모달이 화면 상단을 벗어나는 경우
    if (top < 0) {
      // 화면 중앙에 배치
      top = Math.max(20, (viewportHeight - modalHeight) / 2);
    }

    // 좌측 경계 체크 - 모달이 화면 좌측을 벗어나는 경우
    if (left < 0) {
      left = margin;
    }

    // 우측 경계 체크 - 모달이 화면 우측을 벗어나는 경우
    if (left + modalWidth > viewportWidth) {
      left = viewportWidth - modalWidth - margin;
    }

    return { top, left };
  }, [targetRef]);

  if (!isOpen || !step || !targetRef.current || !modalPosition) {
    return null;
  }

  const targetElement = targetRef.current;
  const rect = targetElement.getBoundingClientRect();

  // 내부에서 계산
  const isFirst = currentStep === 0;
  const isLast = currentStep === steps.length - 1;

  return (
    <div className="fixed inset-0 z-[1000]">
      {/* 강조 영역 */}
      <div
        className="absolute border-2 border-black rounded shadow-[0_0_0_9999px_rgba(0,0,0,0.5)] z-[1001]"
        style={{
          top: rect.top,
          left: rect.left,
          width: rect.width,
          height: rect.height,
        }}
      />

      {/* 설명 모달 */}
      <div
        className="fixed bg-white p-5 rounded-lg shadow-md z-[1002] max-w-[300px]"
        style={{
          top: modalPosition.top,
          left: modalPosition.left,
        }}
      >
        <h2 className="text-xl font-semibold mb-2">{step.title}</h2>
        <p className="mb-4">{step.description}</p>
        <div className="flex gap-2 justify-end">
          <button
            type="button"
            onClick={end}
            className="px-3 py-1.5 bg-gray-100 border-none rounded cursor-pointer hover:bg-gray-200"
          >
            종료
          </button>
          <button
            type="button"
            onClick={prev}
            disabled={isFirst}
            className={`px-3 py-1.5 bg-gray-100 border-none rounded ${
              isFirst
                ? 'cursor-not-allowed opacity-50'
                : 'cursor-pointer hover:bg-gray-200'
            }`}
          >
            이전
          </button>
          <button
            type="button"
            onClick={next}
            disabled={isLast}
            className={`px-3 py-1.5 bg-blue-600 text-white border-none rounded ${
              isLast
                ? 'cursor-not-allowed opacity-50'
                : 'cursor-pointer hover:bg-blue-700'
            }`}
          >
            다음
          </button>
        </div>
      </div>
    </div>
  );
};
