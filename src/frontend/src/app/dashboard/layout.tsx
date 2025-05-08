import React from 'react';
import { AuthGuard } from '@grantly/components/common/AuthGuard';

export default function DashboardLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return <AuthGuard>{children}</AuthGuard>;
}
