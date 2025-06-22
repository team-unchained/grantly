import { DashBoardSideBar } from '@grantly/components/app/sidebar/DashBoardSideBar';
import React from 'react';

import { SidebarInset, SidebarProvider } from '@grantly/components/ui/sidebar';
import { AuthProvider } from '@grantly/hooks/contexts/AuthProvider';
import { TourProvider } from '@grantly/hooks/contexts/TourProvider';
import { TourStep } from '@grantly/hooks/useTour';

// 대시보드 투어 단계 정의
const dashboardTourSteps: TourStep[] = [
  {
    key: 'app-switcher',
    title: '앱 전환',
    description: '여기서 다른 앱을 선택하거나 새 앱을 추가할 수 있습니다.',
  },
  {
    key: 'navigation-menu',
    title: '네비게이션 메뉴',
    description: '이곳에서 앱의 주요 기능들에 접근할 수 있습니다.',
  },
  {
    key: 'user-profile',
    title: '사용자 프로필',
    description: '계정 정보, 결제, 알림 등을 관리할 수 있습니다.',
  },
];

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AuthProvider>
      <SidebarProvider>
        <TourProvider steps={dashboardTourSteps}>
          <DashBoardSideBar />
          <SidebarInset>{children}</SidebarInset>
        </TourProvider>
      </SidebarProvider>
    </AuthProvider>
  );
}
