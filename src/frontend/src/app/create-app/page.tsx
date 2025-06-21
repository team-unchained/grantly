'use client';

import { CreateAppForm } from '@grantly/components/app/CreateAppForm';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from '@grantly/components/ui/dialog';

export default function AppCreatePage() {
  return (
    <Dialog open>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>앱 추가</DialogTitle>
        </DialogHeader>

        <CreateAppForm />
      </DialogContent>
    </Dialog>
  );
}
