import React from 'react';
import { AuthGuard } from '@grantly/components/common/AuthGuard';
import { Header } from '@grantly/components/dashboard/Header';
import { DashBoardSideBar } from '@grantly/components/dashboard/sidebar/DashBoardSideBar';

import { SidebarInset, SidebarProvider } from '@grantly/components/ui/sidebar';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AuthGuard>
      <SidebarProvider>
        <DashBoardSideBar />
        <SidebarInset>
          <Header />
          {children}
        </SidebarInset>
      </SidebarProvider>
    </AuthGuard>
  );
}
