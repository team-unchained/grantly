'use client';

import * as React from 'react';

import { NavUser } from '@grantly/components/app/sidebar/NavUser';
import { AppSwitcher } from '@grantly/components/app/sidebar/AppSwitcher';
import { NavMenu } from '@grantly/components/app/sidebar/NavMenu';
import { useTourContext } from '@grantly/hooks/contexts/TourProvider';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from '@grantly/components/ui/sidebar';

export const DashBoardSideBar = () => {
  const { start, isOpen } = useTourContext();

  return (
    <>
      <Sidebar collapsible="icon">
        <SidebarHeader>
          <AppSwitcher />
        </SidebarHeader>
        <SidebarContent>
          <NavMenu />
        </SidebarContent>
        <SidebarFooter>
          <NavUser />
        </SidebarFooter>
        <SidebarRail />
      </Sidebar>

      {/* 투어 시작 버튼 (개발용) */}
      {!isOpen && (
        <button
          type="button"
          onClick={start}
          className="fixed bottom-4 right-4 z-50 px-4 py-2 bg-blue-600 text-white rounded-lg shadow-lg hover:bg-blue-700"
        >
          투어 시작
        </button>
      )}
    </>
  );
};
