'use client';

import { AppSchema } from '@grantly/api/app/app.shcema';
import { Button } from '@grantly/components/ui/button';
import { Input } from '@grantly/components/ui/input';
import { Label } from '@grantly/components/ui/label';
import Image from 'next/image';
import { useRef, useState } from 'react';
import { UseFormReturn } from 'react-hook-form';
import { toast } from 'sonner';
import * as z from 'zod';
import { ImageCropModal } from './ImageCropModal';

// AppSchema에서 id를 제외한 스키마를 사용
export const AppFormSchema = AppSchema.omit({ slug: true });

export type AppFormData = z.infer<typeof AppFormSchema>;

interface AppFormProps {
  form: UseFormReturn<AppFormData>;
}

export function AppForm({ form }: AppFormProps) {
  const [showCropModal, setShowCropModal] = useState(false);
  const [tempImageUrl, setTempImageUrl] = useState<string>('');
  const fileInputRef = useRef<HTMLInputElement>(null);
  const {
    register,
    setValue,
    watch,
    formState: { errors },
  } = form;

  const imageUrl = watch('imageUrl');

  const handleImageUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    if (file.size > 2 * 1024 * 1024) {
      toast.error('파일 크기는 2MB 이하여야 합니다');
      return;
    }

    const reader = new FileReader();
    reader.onloadend = () => {
      setTempImageUrl(reader.result as string);
      setShowCropModal(true);
    };
    reader.readAsDataURL(file);
  };

  const handleCropComplete = (croppedImageUrl: string) => {
    setValue('imageUrl', croppedImageUrl, { shouldDirty: true });
    setShowCropModal(false);
    setTempImageUrl('');
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
    toast.success('이미지가 업로드되었습니다.');
  };

  const handleCropCancel = () => {
    setShowCropModal(false);
    setTempImageUrl('');
    if (fileInputRef.current) {
      fileInputRef.current.value = '';
    }
  };

  return (
    <div className="space-y-4">
      {showCropModal && tempImageUrl && (
        <ImageCropModal
          srcImage={tempImageUrl}
          onCropComplete={handleCropComplete}
          onCancel={handleCropCancel}
        />
      )}

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
              type="file"
              accept="image/*"
              onChange={handleImageUpload}
              ref={fileInputRef}
              className="hidden"
            />
            <Button
              type="button"
              onClick={() => fileInputRef.current?.click()}
              variant="outline"
            >
              이미지 업로드
            </Button>
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
    </div>
  );
}
