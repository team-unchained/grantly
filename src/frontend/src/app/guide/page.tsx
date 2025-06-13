'use client';

import React from 'react';
import { useTour, TourStep } from '@grantly/hooks/useTour';
import { TourOverlay } from '@grantly/components/common/TourOverlay';

// 투어 시나리오 예시
const steps: TourStep[] = [
  {
    key: 'service',
    title: '서비스 전환',
    description: '여기서 다른 서비스를 선택할 수 있습니다.',
  },
  {
    key: 'menu1',
    title: '첫 번째 메뉴',
    description: '이곳은 주요 메뉴 중 하나입니다.',
  },
  {
    key: 'menu2',
    title: '두 번째 메뉴',
    description: '이곳은 두 번째 주요 메뉴입니다.',
  },
  {
    key: 'user',
    title: '사용자 정보',
    description: '여기서 계정 정보를 확인할 수 있습니다.',
  },
];

// 테스트용 간단한 사이드바 컴포넌트
const TestSidebar = ({
  tourRefs,
}: {
  tourRefs?: {
    service?: React.RefCallback<HTMLElement>;
    menu1?: React.RefCallback<HTMLElement>;
    menu2?: React.RefCallback<HTMLElement>;
    user?: React.RefCallback<HTMLElement>;
  };
}) => {
  return (
    <div className="w-60 h-screen bg-gray-50 py-5 border-r border-gray-200">
      {/* 서비스 전환 */}
      <div ref={tourRefs?.service} className="px-5 mb-5">
        <div className="p-3 bg-white rounded-lg shadow-sm">서비스 전환</div>
      </div>

      {/* 메뉴 그룹 */}
      <div className="px-5 mb-5">
        <div className="text-xs text-gray-500 mb-2 px-4">메뉴</div>
        <div
          ref={tourRefs?.menu1}
          className="p-3 bg-white rounded-lg shadow-sm mb-2"
        >
          첫 번째 메뉴
        </div>
        <div
          ref={tourRefs?.menu2}
          className="p-3 bg-white rounded-lg shadow-sm"
        >
          두 번째 메뉴
        </div>
      </div>

      {/* 사용자 정보 */}
      <div className="px-5 mt-auto">
        <div ref={tourRefs?.user} className="p-3 bg-white rounded-lg shadow-sm">
          사용자 정보
        </div>
      </div>
    </div>
  );
};

export default function GuidePage() {
  // 각 타겟 ref 등록
  const tour = useTour(steps);

  return (
    <div className="flex">
      <TestSidebar
        tourRefs={{
          service: tour.register(0),
          menu1: tour.register(1),
          menu2: tour.register(2),
          user: tour.register(3),
        }}
      />
      <div className="flex-1 p-8">
        <h1 className="text-2xl font-bold">가이드 투어 테스트</h1>
        <button
          type="button"
          onClick={tour.start}
          className="mt-6 px-5 py-2.5 text-base bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
          disabled={tour.isOpen}
        >
          투어 시작
        </button>
      </div>
      <TourOverlay
        isOpen={tour.isOpen}
        step={tour.step}
        targetRef={tour.targetRef}
        onClose={tour.end}
        onNext={tour.next}
        onPrev={tour.prev}
        isFirst={tour.current === 0}
        isLast={tour.current === steps.length - 1}
      />
    </div>
  );
}
