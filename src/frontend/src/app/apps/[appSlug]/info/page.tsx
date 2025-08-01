'use client';

import { updateApp } from '@grantly/api/app/appApi';
import {
  AppForm,
  AppFormData,
  AppFormSchema,
} from '@grantly/components/app/AppForm';
import { DeleteAppDialog } from '@grantly/components/app/DeleteAppDialog';
import { Header } from '@grantly/components/app/Header';
import { Button } from '@grantly/components/ui/button';
import { useAuth } from '@grantly/hooks/contexts/AuthProvider';
import { zodResolver } from '@hookform/resolvers/zod';
import { useCallback, useMemo, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';

export default function AppInfoPage() {
  const { currentApp, refetch } = useAuth();
  const breadcrumbs = useMemo(
    () => [
      { title: currentApp.name, url: `/apps/${currentApp.slug}` },
      { title: 'App Info', url: `/apps/${currentApp.slug}/info` },
    ],
    [currentApp.name, currentApp.slug]
  );

  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<AppFormData>({
    resolver: zodResolver(AppFormSchema),
    mode: 'all',
    criteriaMode: 'all',
    shouldFocusError: true,
    defaultValues: {
      name: currentApp.name,
      // TODO: 이미지 관련 버그 있어서 도메인 지정 및 버그 수정 후 주석 해제
      // imageUrl: currentApp.imageUrl,
    },
  });

  const handleSubmit = useCallback(
    async (data: AppFormData) => {
      setIsLoading(true);
      try {
        await updateApp(currentApp.slug, data);
        await refetch();
        toast.success('설정이 저장되었습니다.');
      } catch (error) {
        toast.error('설정 저장 중 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    },
    [currentApp.slug, refetch]
  );

  return (
    <>
      <Header breadcrumbs={breadcrumbs} />
      <div className="flex flex-1 flex-col gap-6 p-6">
        <div className="max-w-3xl">
          <h1 className="text-2xl font-semibold mb-6">앱 설정</h1>
          <form
            onSubmit={form.handleSubmit(handleSubmit)}
            className="space-y-6"
          >
            <AppForm form={form} />

            <div className="flex justify-between items-center">
              <DeleteAppDialog app={currentApp}>
                <Button type="button" variant="destructive">
                  앱 삭제
                </Button>
              </DeleteAppDialog>

              <Button
                type="submit"
                disabled={isLoading || !form.formState.isValid}
              >
                {isLoading ? '저장 중...' : '저장'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}
