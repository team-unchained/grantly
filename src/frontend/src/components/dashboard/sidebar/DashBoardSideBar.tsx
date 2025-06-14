'use client';

import * as React from 'react';

import { NavUser } from '@grantly/components/dashboard/sidebar/NavUser';
import { AppSwitcher } from '@grantly/components/dashboard/sidebar/AppSwitcher';
import { NavMenu } from '@grantly/components/dashboard/sidebar/NavMenu';
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarHeader,
  SidebarRail,
} from '@grantly/components/ui/sidebar';

export const DashBoardSideBar = () => {
  return (
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
  );
};
