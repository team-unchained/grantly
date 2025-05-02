'use client';

import { ReactNode } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'sonner';
import { usePathname } from 'next/navigation';
import { AuthGuard } from '@grantly/components/common/AuthGuard';
import { GuestRoute } from '@grantly/constants/routes';

export const queryClient = new QueryClient();

export default function Providers({ children }: { children: ReactNode }) {
  const pathname = usePathname();

  const isGuestRoute = GuestRoute.some((route) => pathname.startsWith(route));
  return (
    <QueryClientProvider client={queryClient}>
      <Toaster richColors />
      {isGuestRoute ? children : <AuthGuard>{children}</AuthGuard>}
    </QueryClientProvider>
  );
}
