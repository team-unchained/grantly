'use client';

import {
  SidebarMenuItem,
  SidebarMenuButton,
} from '@grantly/components/ui/sidebar';
import { MenuItem } from '@grantly/constants/DashboardMenu';

export const SimpleMenuItem = ({ item }: { item: MenuItem }) => {
  const Icon = item.icon;

  return (
    <SidebarMenuItem>
      <SidebarMenuButton asChild tooltip={item.title}>
        <a href={item.url} className="flex items-center gap-2 w-full">
          {Icon && <Icon />}
          <span>{item.title}</span>
        </a>
      </SidebarMenuButton>
    </SidebarMenuItem>
  );
};
