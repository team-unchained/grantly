import { Header } from '@grantly/components/dashboard/Header';
import { DashBoardSideBar } from '@grantly/components/dashboard/sidebar/DashBoardSideBar';
import React from 'react';

import { SidebarInset, SidebarProvider } from '@grantly/components/ui/sidebar';
import { AuthProvider } from '@grantly/hooks/contexts/AuthProvider';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <AuthProvider>
      <SidebarProvider>
        <DashBoardSideBar />
        <SidebarInset>
          <Header />
          {children}
        </SidebarInset>
      </SidebarProvider>
    </AuthProvider>
  );
}
