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
import { OAuthClientType } from '@grantly/api/oauth/oauth.schema';
import { useState } from 'react';

interface DeleteOAuthClientDialogProps {
  client: OAuthClientType;
  onDelete: () => void;
  children: React.ReactNode;
}

export function DeleteOAuthClientDialog({
  client,
  onDelete,
  children,
}: DeleteOAuthClientDialogProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  const handleDelete = async () => {
    setIsDeleting(true);
    try {
      await onDelete();
      setIsOpen(false);
    } finally {
      setIsDeleting(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>OAuth 클라이언트 삭제</DialogTitle>
          <DialogDescription>
            <strong>{client.title}</strong> 클라이언트를 삭제하시겠습니까?
            <br />이 작업은 되돌릴 수 없으며, 이 클라이언트를 사용하는 모든
            애플리케이션이 더 이상 인증할 수 없게 됩니다.
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => setIsOpen(false)}
            disabled={isDeleting}
          >
            취소
          </Button>
          <Button
            variant="destructive"
            onClick={handleDelete}
            disabled={isDeleting}
          >
            {isDeleting ? '삭제 중...' : '삭제'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
