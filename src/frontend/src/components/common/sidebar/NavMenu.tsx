'use client';

import {
  SidebarGroup,
  SidebarGroupLabel,
  SidebarMenu,
} from '@grantly/components/ui/sidebar';
import { DashboardMenu } from '@grantly/constants/DashboardMenu';
import { CollapsibleMenuItem } from '@grantly/components/common/sidebar/CollapsibleMenuItem';
import { SimpleMenuItem } from '@grantly/components/common/sidebar/SimpleMenuItem';

export const NavMenu = () => {
  return (
    <>
      {DashboardMenu.map((group) => (
        <SidebarGroup key={group.group}>
          <SidebarGroupLabel>{group.group}</SidebarGroupLabel>
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
    </>
  );
};
