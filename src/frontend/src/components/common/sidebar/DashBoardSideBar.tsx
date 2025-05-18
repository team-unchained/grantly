'use client';

import * as React from 'react';

import { NavUser } from '@grantly/components/common/sidebar/NavUser';
import { ServiceSwitcher } from '@grantly/components/common/sidebar/ServiceSwitcher';
import { NavMenu } from '@grantly/components/common/sidebar/NavMenu';
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
        <ServiceSwitcher />
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
