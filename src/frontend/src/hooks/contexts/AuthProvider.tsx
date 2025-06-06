'use client';

import { UserType } from '@grantly/api/user/user.shcema';
import { useGetMeQuery } from '@grantly/api/user/useUserQueries';
import { Skeleton } from '@grantly/components/ui/skeleton';
import { usePathname, useRouter } from 'next/navigation';
import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useMemo,
} from 'react';
import { toast } from 'sonner';

interface AuthContextType {
  user: UserType | undefined;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const router = useRouter();
  const pathname = usePathname();
  const { data: user, isLoading: isUserMeLoading } = useGetMeQuery();

  const isLoading = isUserMeLoading;

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
      user,
    }),
    [user]
  );

  if (isLoading) {
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
