'use client';

import * as React from 'react';

import { NavUser } from '@grantly/components/app/sidebar/NavUser';
import { AppSwitcher } from '@grantly/components/app/sidebar/AppSwitcher';
import { NavMenu } from '@grantly/components/app/sidebar/NavMenu';
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
