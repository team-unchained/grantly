'use client';

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@grantly/components/ui/dialog';
import { Button } from '@grantly/components/ui/button';
import { OAuthClientForm } from '@grantly/components/oauth/OAuthClientForm';
import {
  CreateOAuthClientSchema,
  CreateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useState } from 'react';
import { OAuthGrantType, OAuthScope } from '@grantly/constants/oauth';

interface CreateOAuthClientDialogProps {
  onSubmit: (data: CreateOAuthClientType) => void;
  children: React.ReactNode;
}

export function CreateOAuthClientDialog({
  onSubmit,
  children,
}: CreateOAuthClientDialogProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [open, setOpen] = useState(false);

  const handleOpenChange = (newOpen: boolean) => {
    setOpen(newOpen);
    if (!newOpen) {
      // 다이얼로그가 닫힐 때 폼 초기화
      form.reset();
    }
  };

  const form = useForm<CreateOAuthClientType>({
    resolver: zodResolver(CreateOAuthClientSchema),
    mode: 'all',
    criteriaMode: 'all',
    shouldFocusError: true,
    defaultValues: {
      title: '',
      redirectUris: [],
      scopes: [OAuthScope.ALL],
      grantType: OAuthGrantType.AUTHORIZATION_CODE,
    },
  });

  const handleSubmit = async (data: CreateOAuthClientType) => {
    setIsSubmitting(true);
    try {
      await onSubmit(data);
      setOpen(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>OAuth 클라이언트 생성</DialogTitle>
          <DialogDescription>
            새로운 OAuth 클라이언트를 생성하세요. 클라이언트 이름을 입력하면
            자동으로 Client ID와 Secret이 생성됩니다.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
          <OAuthClientForm form={form} />

          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => handleOpenChange(false)}
              disabled={isSubmitting}
            >
              취소
            </Button>
            <Button
              type="submit"
              disabled={isSubmitting || !form.formState.isValid}
            >
              {isSubmitting ? '생성 중...' : '생성'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
