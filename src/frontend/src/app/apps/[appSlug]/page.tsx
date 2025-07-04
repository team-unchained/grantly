'use client';

import { Header } from '@grantly/components/app/Header';
import { useAuth } from '@grantly/hooks/contexts/AuthProvider';
import { useTourContext } from '@grantly/hooks/contexts/TourProvider';
import { useMemo } from 'react';

export default function AppPage() {
  const { currentApp } = useAuth();
  const { register } = useTourContext();

  const breadcrumbs = useMemo(
    () => [{ title: currentApp.name, url: `/apps/${currentApp.slug}` }],
    [currentApp.name, currentApp.slug]
  );

  return (
    <>
      <Header breadcrumbs={breadcrumbs} />
      <div className="flex flex-1 flex-col gap-4 p-4 pt-0" ref={register(3)}>
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
