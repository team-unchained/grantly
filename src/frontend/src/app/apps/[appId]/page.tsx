'use client';

import { Header } from '@grantly/components/dashboard/Header';
import { useAuth } from '@grantly/hooks/contexts/AuthProvider';
import { useMemo } from 'react';

export default function AppPage() {
  const { currentApp } = useAuth();

  const breadcrumbs = useMemo(
    () => [{ title: currentApp.name, url: `/apps/${currentApp.id}` }],
    [currentApp.name, currentApp.id]
  );

  return (
    <>
      <Header breadcrumbs={breadcrumbs} />
      <div className="flex flex-1 flex-col gap-4 p-4 pt-0">
        <div className="grid auto-rows-min gap-4 md:grid-cols-3">
          <div className="aspect-video rounded-xl bg-muted/50">
            Overview PAGE
          </div>
          <div className="aspect-video rounded-xl bg-muted/50" />
          <div className="aspect-video rounded-xl bg-muted/50" />
        </div>
        <div className="min-h-screen flex-1 rounded-xl bg-muted/50 md:min-h-min" />
      </div>
    </>
  );
}
