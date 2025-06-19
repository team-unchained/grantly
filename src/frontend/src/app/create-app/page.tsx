'use client';

import { createApp } from '@grantly/api/app/appApi';
import {
  AppForm,
  AppFormData,
  AppFormSchema,
} from '@grantly/components/app/AppForm';
import { Button } from '@grantly/components/ui/button';
import { zodResolver } from '@hookform/resolvers/zod';
import { useRouter } from 'next/navigation';
import { useCallback, useState } from 'react';
import { useForm } from 'react-hook-form';
import { toast } from 'sonner';

export default function AppCreatePage() {
  const router = useRouter();

  const [isLoading, setIsLoading] = useState(false);

  const form = useForm<AppFormData>({
    resolver: zodResolver(AppFormSchema),
    mode: 'all',
    criteriaMode: 'all',
    shouldFocusError: true,
    defaultValues: {
      name: '',
      imageUrl: '',
    },
  });

  const handleSubmit = useCallback(
    async (data: AppFormData) => {
      setIsLoading(true);
      try {
        const app = await createApp(data);
        toast.success('저장되었습니다.');
        router.replace(`/apps/${app.id}`);
      } catch (error) {
        toast.error('저장 중 오류가 발생했습니다.');
      } finally {
        setIsLoading(false);
      }
    },
    [router]
  );

  return (
    <div className="flex flex-1 flex-col gap-6 p-6">
      <div className="max-w-3xl">
        <h1 className="text-2xl font-semibold mb-6">앱 설정</h1>
        <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
          <AppForm form={form} />

          <div className="flex justify-between items-center">
            <Button
              type="submit"
              disabled={isLoading || !form.formState.isDirty}
            >
              {isLoading ? '저장 중...' : '저장'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
