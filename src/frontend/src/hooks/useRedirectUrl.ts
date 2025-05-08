import { useSearchParams } from 'next/navigation';

export const useRedirectUrl = (fallback: string) => {
  const searchParams = useSearchParams();
  const raw = searchParams.get('redirect');

  try {
    const decoded = raw ? decodeURIComponent(raw) : fallback;
    return decoded.startsWith('/') ? decoded : fallback;
  } catch {
    return fallback;
  }
};
