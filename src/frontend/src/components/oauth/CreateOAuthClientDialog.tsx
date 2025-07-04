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
  OAuthClientType,
  CreateOAuthClientSchema,
} from '@grantly/api/oauth/oauth.schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useState, useEffect } from 'react';
import { Plus } from 'lucide-react';

interface CreateOAuthClientDialogProps {
  client?: OAuthClientType;
  onSubmit: (data: CreateOAuthClientType) => void;
  children?: React.ReactNode;
  open?: boolean;
  onOpenChange?: (open: boolean) => void;
}

export function CreateOAuthClientDialog({
  client,
  onSubmit,
  children,
  open: controlledOpen,
  onOpenChange,
}: CreateOAuthClientDialogProps) {
  const [internalOpen, setInternalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const isOpen = controlledOpen !== undefined ? controlledOpen : internalOpen;
  const handleOpenChange = (open: boolean) => {
    if (onOpenChange) {
      onOpenChange(open);
    } else {
      setInternalOpen(open);
    }
    if (!open) {
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
      title: client?.title || '',
      redirectUris: client?.redirectUris || [],
      scopes: client?.scopes || [],
    },
  });

  // 클라이언트가 변경될 때 폼 값 업데이트
  useEffect(() => {
    if (client) {
      form.reset({
        title: client.title,
        redirectUris: client.redirectUris || [],
        scopes: client.scopes || [],
      });
    } else {
      form.reset({
        title: '',
        redirectUris: [],
        scopes: [],
      });
    }
  }, [client, form]);

  const handleSubmit = async (data: CreateOAuthClientType) => {
    setIsSubmitting(true);
    try {
      await onSubmit(data);
      handleOpenChange(false);
      form.reset();
    } finally {
      setIsSubmitting(false);
    }
  };

  const isEditing = !!client;

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        {children || (
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            OAuth 클라이언트 생성
          </Button>
        )}
      </DialogTrigger>
      <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle>
            {isEditing ? 'OAuth 클라이언트 수정' : 'OAuth 클라이언트 생성'}
          </DialogTitle>
          <DialogDescription>
            {isEditing
              ? 'OAuth 클라이언트 정보를 수정하세요.'
              : '새로운 OAuth 클라이언트를 생성하세요. 클라이언트 이름을 입력하면 자동으로 Client ID와 Secret이 생성됩니다.'}
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-6">
          <OAuthClientForm form={form} isEditing={isEditing} />

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
              {(() => {
                if (isSubmitting) {
                  return isEditing ? '수정 중...' : '생성 중...';
                }
                return isEditing ? '수정' : '생성';
              })()}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
