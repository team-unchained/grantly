'use client';

import { AppType } from '@grantly/api/app/app.shcema';
import { useGetAppsQuery } from '@grantly/api/app/useAppQueries';
import { UserType } from '@grantly/api/user/user.shcema';
import { useGetMeQuery } from '@grantly/api/user/useUserQueries';
import { ErrorComponent } from '@grantly/components/common/Error';
import { Skeleton } from '@grantly/components/ui/skeleton';
import { useParams, usePathname, useRouter } from 'next/navigation';
import {
  createContext,
  ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
} from 'react';
import { toast } from 'sonner';

interface AuthContextType {
  user: UserType;
  apps: AppType[];
  currentApp: AppType;
  refetch: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();
  const { data: user, isLoading: isUserMeLoading } = useGetMeQuery();
  const {
    data: apps,
    refetch: refetchApps,
    isLoading: isAppsLoading,
  } = useGetAppsQuery();

  const isLoading = isUserMeLoading || isAppsLoading;
  const appSlug = String(useParams().appSlug);
  const currentApp = apps?.find((app) => app.slug === appSlug);

  const refetch = useCallback(() => {
    refetchApps();
  }, [refetchApps]);

  // Login 확인
  useEffect(() => {
    if (isLoading) return;

    if (!user) {
      toast.info('로그인이 필요한 페이지입니다.');
      router.replace(`/auth/login?redirect=${encodeURIComponent(pathname)}`);
      return;
    }

    // 앱이 하나도 없는 경우
    if (!apps || apps.length === 0) {
      toast.info('앱이 없습니다. 새로운 앱을 만들어보세요!');
      router.replace('/apps/create');
      return;
    }

    // appSlug가 없는 경우
    if (!appSlug) {
      router.replace(`/apps/${apps[0].slug}`);
    }
  }, [isLoading, pathname, router, user, currentApp, apps, appSlug]);

  const value = useMemo(
    () => ({
      // FIXME: 타입 추론 오류 해결
      user: user!,
      apps: apps ?? [],
      currentApp: currentApp!,
      refetch,
    }),
    [user, apps, currentApp, refetch]
  );

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Skeleton className="w-8 h-8 rounded-full" />
      </div>
    );
  }

  if (appSlug && !value.currentApp) {
    return <ErrorComponent title="앱을 찾을 수 없습니다." refresh goHome />;
  }

  if (!value.user || !value.currentApp) {
    // User 가 없으면 로그인 화면으로 가니까 그냥 로딩만 노출해도 된다.
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
