import { Info, LayoutDashboard, LucideIcon } from 'lucide-react';

export type MenuItem = {
  title: string;
  url: string;
  icon?: LucideIcon;
  items?: MenuItem[];
};

export type MenuGroup = {
  title: string;
  items: MenuItem[];
};

type CreateMenuParams = {
  appId: number;
};

export const createDashboardMenu = ({
  appId,
}: CreateMenuParams): MenuGroup[] => [
  {
    title: 'App',
    items: [
      {
        title: 'Overview',
        url: `/apps/${appId}`,
        icon: LayoutDashboard,
      },
      {
        title: 'App Info',
        url: `/apps/${appId}/info`,
        icon: Info,
      },
    ],
  },
];
