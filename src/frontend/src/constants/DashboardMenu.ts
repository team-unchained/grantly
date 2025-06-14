import {
  Boxes,
  FolderCog,
  Info,
  KeyRound,
  LayoutDashboard,
  LucideIcon,
  PlugZap,
  Settings,
} from 'lucide-react';

export type MenuItem = {
  title: string;
  url: string;
  icon?: LucideIcon;
  items?: MenuItem[];
};

export type MenuGroup = {
  group: string;
  items: MenuItem[];
};

export const DashboardMenu: MenuGroup[] = [
  {
    group: 'App',
    items: [
      { title: 'Overview', url: '/dashboard/overview', icon: LayoutDashboard },
      { title: 'App Info', url: '#', icon: Info },
      { title: 'Branding', url: '#', icon: LayoutDashboard },
    ],
  },
  {
    group: 'Clients',
    items: [
      {
        title: 'Client Management',
        url: '#',
        icon: Boxes,
        items: [
          { title: 'Create Client', url: '#' },
          { title: 'Client List', url: '#' },
        ],
      },
      {
        title: 'A Client',
        url: '#',
        icon: FolderCog,
        items: [
          { title: 'Users', url: '#' },
          { title: 'Sessions', url: '#' },
          { title: 'Signup Form', url: '#' },
          { title: 'Terms & Consent', url: '#' },
          { title: 'Client Settings', url: '#' },
        ],
      },
    ],
  },
  {
    group: 'Integrations',
    items: [
      {
        title: 'Webhooks & SSO',
        url: '#',
        icon: PlugZap,
        items: [
          { title: 'Webhooks', url: '#' },
          { title: 'OAuth Redirects', url: '#' },
          { title: 'SSO Settings', url: '#' },
        ],
      },
    ],
  },
  {
    group: 'Configuration',
    items: [
      {
        title: 'API & Keys',
        url: '#',
        icon: KeyRound,
        items: [
          { title: 'API Keys', url: '#' },
          { title: 'Audit Logs', url: '#' },
        ],
      },
      {
        title: 'Settings',
        url: '#',
        icon: Settings,
        items: [
          { title: 'Team Settings', url: '#' },
          { title: 'Danger Zone', url: '#' },
        ],
      },
    ],
  },
];
