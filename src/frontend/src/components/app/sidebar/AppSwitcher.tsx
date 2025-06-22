'use client';

import { ChevronsUpDown, AppWindow, Plus, Check } from 'lucide-react';
import * as React from 'react';
import Link from 'next/link';

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@grantly/components/ui/dropdown-menu';
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from '@grantly/components/ui/sidebar';
import { useAuth } from '@grantly/hooks/contexts/AuthProvider';
import { useTourContext } from '@grantly/hooks/contexts/TourProvider';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@grantly/components/ui/dialog';
import { CreateAppForm } from '@grantly/components/app/CreateAppForm';

export const AppSwitcher = () => {
  const { isMobile } = useSidebar();
  const { register } = useTourContext();
  const { apps, currentApp } = useAuth();
  const [dialogOpen, setDialogOpen] = React.useState(false);

  const registerRef = register(0);

  return (
    <SidebarMenu>
      <SidebarMenuItem ref={register(0)}>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              ref={registerRef}
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <div className="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                <AppWindow className="size-4" />
              </div>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-semibold">
                  {currentApp.name}
                </span>
              </div>
              <ChevronsUpDown className="ml-auto" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-[--radix-dropdown-menu-trigger-width] min-w-56 rounded-lg"
            align="start"
            side={isMobile ? 'bottom' : 'right'}
            sideOffset={4}
          >
            <DropdownMenuLabel className="text-xs text-muted-foreground">
              Apps
            </DropdownMenuLabel>
            {apps.map((app) => (
              <Link href={`/apps/${app.slug}`} key={app.name}>
                <DropdownMenuItem className="gap-2 p-2">
                  <div className="flex size-6 items-center justify-center rounded-sm border">
                    <AppWindow className="size-4 shrink-0" />
                  </div>
                  <span className="flex-1">{app.name}</span>
                  {currentApp.slug === app.slug && (
                    <Check className="size-4 text-primary" />
                  )}
                </DropdownMenuItem>
              </Link>
            ))}
            <DropdownMenuSeparator />
            <DropdownMenuItem
              className="gap-2 p-2"
              onClick={() => setDialogOpen(true)}
            >
              <div className="flex size-6 items-center justify-center rounded-md border bg-background">
                <Plus className="size-4" />
              </div>
              <div className="font-medium text-muted-foreground">Add App</div>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>앱 추가</DialogTitle>
          </DialogHeader>

          <CreateAppForm onClose={() => setDialogOpen(false)} />
        </DialogContent>
      </Dialog>
    </SidebarMenu>
  );
};
