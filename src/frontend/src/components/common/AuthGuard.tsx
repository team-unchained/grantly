'use client';

import { usePathname, useRouter } from 'next/navigation';
import { useEffect } from 'react';
import { useGetMeQuery } from '@grantly/api/user/useUserQueries';
import axios from 'axios';
import { toast } from 'sonner';

export const AuthGuard = ({ children }: { children: React.ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();
  const { error, isLoading } = useGetMeQuery();

  useEffect(() => {
    if (isLoading || !error) return;
    if (!axios.isAxiosError(error)) return;

    if (error.response?.status === 401) {
      toast.info('로그인이 필요한 페이지입니다.');
      router.replace(`/auth/login?redirect=${encodeURIComponent(pathname)}`);
    }
  }, [error, isLoading, pathname, router]);

  if (isLoading || error) return null;
  // eslint-disable-next-line react/jsx-no-useless-fragment
  return <>{children}</>;
};
