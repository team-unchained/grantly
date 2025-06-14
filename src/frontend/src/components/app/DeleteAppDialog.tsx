import { deleteApp } from '@grantly/api/app/appApi';
import { AppType } from '@grantly/api/app/app.shcema';
import { Button } from '@grantly/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@grantly/components/ui/dialog';
import { Input } from '@grantly/components/ui/input';
import { toast } from 'sonner';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { AlertCircle } from 'lucide-react';

interface DeleteAppDialogProps {
  app: AppType;
  children: React.ReactNode;
}

export function DeleteAppDialog({ app, children }: DeleteAppDialogProps) {
  const router = useRouter();
  const [deleteConfirmText, setDeleteConfirmText] = useState('');
  const [isDeleting, setIsDeleting] = useState(false);
  const [isOpen, setIsOpen] = useState(false);

  const handleDelete = async () => {
    if (deleteConfirmText !== app.name) {
      toast.error('앱 이름이 일치하지 않습니다.');
      return;
    }

    setIsDeleting(true);
    try {
      await deleteApp(app.id);
      toast.success('앱이 삭제되었습니다.');
      router.push('/apps');
    } catch (error) {
      toast.error('앱 삭제 중 오류가 발생했습니다.');
    } finally {
      setIsDeleting(false);
      setIsOpen(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>{children}</DialogTrigger>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <div className="flex items-center gap-2 text-destructive">
            <AlertCircle className="h-5 w-5" />
            <DialogTitle>앱 삭제</DialogTitle>
          </div>
          <DialogDescription className="space-y-2 pt-2">
            <p>이 작업은 되돌릴 수 없습니다.</p>
            <p className="text-sm text-muted-foreground">
              앱을 삭제하려면{' '}
              <span className="rounded-md border-l-4 border-destructive bg-destructive/5 px-2 py-1 font-medium text-destructive">
                {app.name}
              </span>
              을 정확히 입력해주세요.
            </p>
          </DialogDescription>
        </DialogHeader>
        <div className="py-4">
          <Input
            placeholder="앱 이름을 입력하세요"
            value={deleteConfirmText}
            onChange={(e) => setDeleteConfirmText(e.target.value)}
            className="border-destructive/50 focus-visible:ring-destructive/50"
          />
        </div>
        <DialogFooter className="gap-2">
          <Button
            variant="outline"
            onClick={() => setIsOpen(false)}
            disabled={isDeleting}
            className="flex-1 sm:flex-none"
          >
            취소
          </Button>
          <Button
            variant="destructive"
            onClick={handleDelete}
            disabled={isDeleting || deleteConfirmText !== app.name}
            className="flex-1 sm:flex-none"
          >
            {isDeleting ? '삭제 중...' : '삭제'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
