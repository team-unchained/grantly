'use client';

import { CollapsibleMenuItem } from '@grantly/components/app/sidebar/CollapsibleMenuItem';
import { SimpleMenuItem } from '@grantly/components/app/sidebar/SimpleMenuItem';
import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
} from '@grantly/components/ui/sidebar';
import { createDashboardMenu } from '@grantly/constants/DashboardMenu';
import { useAuth } from '@grantly/hooks/contexts/AuthProvider';
import { useTourContext } from '@grantly/hooks/contexts/TourProvider';
import { useMemo } from 'react';

export const NavMenu = () => {
  const { currentApp } = useAuth();
  const { register } = useTourContext();
  const menus = useMemo(
    () => createDashboardMenu({ appId: currentApp.id }),
    [currentApp.id]
  );

  return (
    <div ref={register(1)}>
      {menus.map((group) => (
        <SidebarGroup key={group.title}>
          <SidebarGroupLabel>{group.title}</SidebarGroupLabel>
          <SidebarMenu>
            {group.items.map((item) => {
              if (item.items && item.items.length > 0) {
                return <CollapsibleMenuItem key={item.title} item={item} />;
              }
              return <SimpleMenuItem key={item.title} item={item} />;
            })}
          </SidebarMenu>
        </SidebarGroup>
      ))}
    </div>
  );
};
