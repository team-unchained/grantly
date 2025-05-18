'use client';

import { useSearchParams } from 'next/navigation';
import { useMemo } from 'react';

export const useRedirectUrl = (fallback: string) => {
  const searchParams = useSearchParams();
  const redirectRaw = searchParams.get('redirect');

  return useMemo(() => {
    try {
      const decoded = redirectRaw ? decodeURIComponent(redirectRaw) : fallback;
      return decoded.startsWith('/') ? decoded : fallback;
    } catch {
      return fallback;
    }
  }, [redirectRaw, fallback]);
};
