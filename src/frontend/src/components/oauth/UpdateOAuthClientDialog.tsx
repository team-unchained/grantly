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
  CreateOAuthClientType,
  UpdateOAuthClientType,
  OAuthClientType,
  CreateOAuthClientSchema,
} from '@grantly/api/oauth/oauth.schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useState, useEffect } from 'react';
import { OAuthGrantType, OAuthScope } from '@grantly/constants/oauth';

interface UpdateOAuthClientDialogProps {
  client: OAuthClientType;
  onSubmit: (data: UpdateOAuthClientType) => Promise<void>;
  children: React.ReactNode;
}

export function UpdateOAuthClientDialog({
  client,
  onSubmit,
  children,
}: UpdateOAuthClientDialogProps) {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [open, setOpen] = useState(false);

  const form = useForm<CreateOAuthClientType>({
    resolver: zodResolver(CreateOAuthClientSchema),
    mode: 'all',
    criteriaMode: 'all',
    shouldFocusError: true,
    defaultValues: {
      title: client.title,
      redirectUris: client.redirectUris || [],
      scopes: client.scopes || [OAuthScope.ALL],
      grantType: client.grantType || OAuthGrantType.AUTHORIZATION_CODE,
    },
  });

  // 클라이언트가 변경될 때 폼 값 업데이트
  useEffect(() => {
    form.reset({
      title: client.title,
      redirectUris: client.redirectUris || [],
      scopes: client.scopes || [OAuthScope.ALL],
      grantType: client.grantType || OAuthGrantType.AUTHORIZATION_CODE,
    });
  }, [client, form]);

  const handleSubmit = async (data: UpdateOAuthClientType) => {
    setIsSubmitting(true);
    try {
      await onSubmit(data);
      setOpen(false);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleOpenChange = (newOpen: boolean) => {
    setOpen(newOpen);
    if (!newOpen) {
      // 다이얼로그가 닫힐 때 폼 초기화
      form.reset();
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>OAuth 클라이언트 수정</DialogTitle>
          <DialogDescription>
            OAuth 클라이언트 정보를 수정하세요.
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
              {isSubmitting ? '수정 중...' : '수정'}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
