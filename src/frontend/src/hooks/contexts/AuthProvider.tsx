'use client';

import {
  createContext,
  useContext,
  ReactNode,
  useMemo,
  useEffect,
} from 'react';
import { usePathname, useRouter } from 'next/navigation';
import { useGetMeQuery } from '@grantly/api/user/useUserQueries';
import { useGetAppsQuery } from '@grantly/api/app/useAppQueries';
import { Skeleton } from '@grantly/components/ui/skeleton';
import { toast } from 'sonner';
import { UserType } from '@grantly/api/user/user.shcema';
import { AppType } from '@grantly/api/app/app.shcema';
import { SentryService } from '@grantly/utils/sentry-service';

interface AuthContextType {
  user: UserType;
  apps: AppType[];
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();
  const { data: user, isLoading: isUserMeLoading } = useGetMeQuery();
  const { data: apps, isLoading: isAppsLoading } = useGetAppsQuery();

  const isLoading = isUserMeLoading || isAppsLoading;

  // Login 확인
  useEffect(() => {
    if (isLoading) return;
    if (!user) {
      toast.info('로그인이 필요한 페이지입니다.');
      router.replace(`/auth/login?redirect=${encodeURIComponent(pathname)}`);
    }
  }, [user, isLoading, pathname, router]);

  const value = useMemo(
    () => ({
      // FIXME: 타입 추론 오류 해결
      user: user!,
      apps: apps ?? [],
    }),
    [user, apps]
  );

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Skeleton className="w-8 h-8 rounded-full" />
      </div>
    );
  }

  if (!value.user) {
    SentryService.captureException(
      new Error('AuthProvider: user or apps is undefined'),
      {
        user,
        apps,
      }
    );
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Skeleton className="w-8 h-8 rounded-full" />
      </div>
    );
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
