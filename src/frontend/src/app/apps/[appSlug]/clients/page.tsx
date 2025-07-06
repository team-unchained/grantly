'use client';

import {
  CreateOAuthClientType,
  UpdateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';
import {
  useCreateOAuthClientMutation,
  useDeleteOAuthClientMutation,
  useUpdateOAuthClientMutation,
} from '@grantly/api/oauth/useOAuthQueries';
import { Header } from '@grantly/components/app/Header';
import { CreateOAuthClientDialog } from '@grantly/components/oauth/CreateOAuthClientDialog';
import { OAuthClientList } from '@grantly/components/oauth/OAuthClientList';
import { Button } from '@grantly/components/ui/button';
import { useAuth } from '@grantly/hooks/contexts/AuthProvider';
import { Plus } from 'lucide-react';
import { useCallback, useMemo } from 'react';

export default function ClientsPage() {
  const { currentApp } = useAuth();

  const breadcrumbs = useMemo(
    () => [
      { title: currentApp.name, url: `/apps/${currentApp.slug}` },
      { title: 'OAuth 클라이언트', url: `/apps/${currentApp.slug}/clients` },
    ],
    [currentApp.name, currentApp.slug]
  );

  const createMutation = useCreateOAuthClientMutation(currentApp.slug);
  const handleCreate = useCallback(
    async (data: CreateOAuthClientType) => {
      await createMutation.mutateAsync(data);
    },
    [createMutation]
  );

  const updateMutation = useUpdateOAuthClientMutation(currentApp.slug);
  const handleUpdate = useCallback(
    async (clientId: string, data: UpdateOAuthClientType) => {
      await updateMutation.mutateAsync({ clientId, data });
    },
    [updateMutation]
  );

  const deleteMutation = useDeleteOAuthClientMutation(currentApp.slug);
  const handleDelete = useCallback(
    async (clientId: string) => {
      await deleteMutation.mutateAsync(clientId);
    },
    [deleteMutation]
  );

  return (
    <>
      <Header breadcrumbs={breadcrumbs} />
      <div className="flex flex-1 flex-col gap-6 p-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-2xl font-semibold">OAuth 클라이언트</h1>
            <p className="text-muted-foreground">
              앱에서 사용할 OAuth 클라이언트를 관리하세요.
            </p>
          </div>
          <CreateOAuthClientDialog onSubmit={handleCreate}>
            <Button>
              <Plus className="h-4 w-4 mr-2" />
              OAuth 클라이언트 생성
            </Button>
          </CreateOAuthClientDialog>
        </div>

        <OAuthClientList
          app={currentApp}
          onUpdate={handleUpdate}
          onDelete={handleDelete}
        />
      </div>
    </>
  );
}
