'use client';

import {
  SidebarMenuItem,
  SidebarMenuButton,
} from '@grantly/components/ui/sidebar';
import { MenuItem } from '@grantly/constants/DashboardMenu';
import Link from 'next/link';

export const SimpleMenuItem = ({ item }: { item: MenuItem }) => {
  return (
    <SidebarMenuItem>
      <SidebarMenuButton asChild tooltip={item.title}>
        <Link href={item.url} className="flex items-center gap-2 w-full">
          {item.icon && <item.icon />}
          <span>{item.title}</span>
        </Link>
      </SidebarMenuButton>
    </SidebarMenuItem>
  );
};
