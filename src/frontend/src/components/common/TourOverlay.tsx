'use client';

import React from 'react';
import { TourStep } from '@grantly/hooks/useTour';

interface TourOverlayProps {
  isOpen: boolean;
  step: TourStep | null;
  targetRef: React.RefObject<HTMLElement | null>;
  onClose: () => void;
  onNext: () => void;
  onPrev: () => void;
  isFirst: boolean;
  isLast: boolean;
}

export const TourOverlay: React.FC<TourOverlayProps> = ({
  isOpen,
  step,
  targetRef,
  onClose,
  onNext,
  onPrev,
  isFirst,
  isLast,
}) => {
  if (!isOpen || !step) return null;

  const targetElement = targetRef.current;
  if (!targetElement) return null;

  const rect = targetElement.getBoundingClientRect();
  const padding = 8; // 강조 영역 주변 여백

  return (
    <div className="fixed inset-0 bg-black/50 z-[1000]">
      {/* 강조 영역 */}
      <div
        className="absolute border-2 border-white rounded shadow-[0_0_0_9999px_rgba(0,0,0,0.5)] z-[1001]"
        style={{
          top: rect.top - padding,
          left: rect.left - padding,
          width: rect.width + padding * 2,
          height: rect.height + padding * 2,
        }}
      />

      {/* 설명 모달 */}
      <div
        className="fixed bg-white p-5 rounded-lg shadow-md z-[1002] max-w-[300px]"
        style={{
          top: rect.bottom + 20,
          left: rect.left,
        }}
      >
        <h2 className="text-xl font-semibold mb-2">{step.title}</h2>
        <p className="mb-4">{step.description}</p>
        <div className="flex gap-2 justify-end">
          <button
            type="button"
            onClick={onClose}
            className="px-3 py-1.5 bg-gray-100 border-none rounded cursor-pointer hover:bg-gray-200"
          >
            종료
          </button>
          <button
            type="button"
            onClick={onPrev}
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
            onClick={onNext}
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
