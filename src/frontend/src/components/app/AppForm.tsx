'use client';

import { AppSchema } from '@grantly/api/app/app.shcema';
import { Input } from '@grantly/components/ui/input';
import { Label } from '@grantly/components/ui/label';
import Image from 'next/image';
import { useState } from 'react';
import { UseFormReturn } from 'react-hook-form';
import { toast } from 'sonner';
import * as z from 'zod';

// AppSchema에서 id를 제외한 스키마를 사용
export const AppFormSchema = AppSchema.omit({ id: true });

export type AppFormData = z.infer<typeof AppFormSchema>;

interface AppFormProps {
  form: UseFormReturn<AppFormData>;
}

export function AppForm({ form }: AppFormProps) {
  const [isUploading, setIsUploading] = useState(false);
  const {
    register,
    setValue,
    watch,
    formState: { errors },
  } = form;

  const imageUrl = watch('imageUrl');

  const handleImageUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.size > 2 * 1024 * 1024) {
      toast.error('파일 크기는 2MB 이하여야 합니다');
      return;
    }

    setIsUploading(true);
    try {
      // TODO: 이미지 업로드 API 구현
      const reader = new FileReader();
      reader.onloadend = () => {
        setValue('imageUrl', reader.result as string, { shouldDirty: true });
      };
      reader.readAsDataURL(file);
      toast.success('이미지가 업로드되었습니다.');
    } catch (error) {
      toast.error('이미지 업로드 중 오류가 발생했습니다.');
    } finally {
      setIsUploading(false);
    }
  };

  return (
    <div className="space-y-4">
      <div>
        <Label htmlFor="logo">앱 로고</Label>
        <div className="mt-2 flex items-center gap-4">
          <div className="relative h-24 w-24 overflow-hidden rounded-lg border border-input">
            {imageUrl ? (
              <Image
                src={imageUrl}
                alt="앱 로고"
                fill
                className="object-cover"
              />
            ) : (
              <div className="flex h-full w-full items-center justify-center bg-muted text-muted-foreground">
                로고 없음
              </div>
            )}
          </div>
          <div className="flex flex-col gap-2">
            <Input
              id="logo"
              type="file"
              accept="image/*"
              onChange={handleImageUpload}
              className="hidden"
            />
            <button
              type="button"
              onClick={() => document.getElementById('logo')?.click()}
              disabled={isUploading}
              className="inline-flex items-center justify-center whitespace-nowrap rounded-md text-sm font-medium ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 border border-input bg-background hover:bg-accent hover:text-accent-foreground h-10 px-4 py-2"
            >
              {isUploading ? '업로드 중...' : '이미지 업로드'}
            </button>
            <p className="text-sm text-muted-foreground">
              PNG, JPG, GIF 파일 (최대 2MB)
            </p>
          </div>
        </div>
      </div>

      <div>
        <Label htmlFor="name">앱 이름</Label>
        <Input id="name" {...register('name')} className="mt-1" />
        {errors.name && (
          <p className="mt-1 text-sm text-destructive">{errors.name.message}</p>
        )}
      </div>

      <div>
        <Label htmlFor="description">설명</Label>
        <textarea
          id="description"
          {...register('description')}
          className="mt-1 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          rows={4}
          placeholder="앱에 대한 설명을 입력하세요"
        />
        {errors.description && (
          <p className="mt-1 text-sm text-destructive">
            {errors.description.message}
          </p>
        )}
      </div>
    </div>
  );
}
