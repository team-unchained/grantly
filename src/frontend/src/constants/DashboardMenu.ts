import { Info, LayoutDashboard, LucideIcon, Key } from 'lucide-react';

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
  appSlug: string;
};

export const createDashboardMenu = ({
  appSlug,
}: CreateMenuParams): MenuGroup[] => [
  {
    title: 'App',
    items: [
      {
        title: 'Overview',
        url: `/apps/${appSlug}`,
        icon: LayoutDashboard,
      },
      {
        title: 'Client',
        url: `/apps/${appSlug}/clients`,
        icon: Key,
      },
      {
        title: 'App Info',
        url: `/apps/${appSlug}/info`,
        icon: Info,
      },
    ],
  },
];
