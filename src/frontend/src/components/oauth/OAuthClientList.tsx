'use client';

import { AppType } from '@grantly/api/app/app.shcema';
import { UpdateOAuthClientType } from '@grantly/api/oauth/oauth.schema';
import { useGetOAuthClientsQuery } from '@grantly/api/oauth/useOAuthQueries';
import { OAuthClientItem } from '@grantly/components/oauth/OAuthClientItem';
import { Card, CardContent } from '@grantly/components/ui/card';
import { Skeleton } from '@grantly/components/ui/skeleton';

interface OAuthClientListProps {
  app: AppType;
  onUpdate: (clientId: string, data: UpdateOAuthClientType) => Promise<void>;
  onDelete: (clientId: string) => void;
}

export function OAuthClientList({
  app,
  onUpdate,
  onDelete,
}: OAuthClientListProps) {
  const { data: clients, isLoading, error } = useGetOAuthClientsQuery(app.slug);

  if (error) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-12">
          <div className="text-center">
            <h2 className="text-lg font-semibold text-red-600">
              오류가 발생했습니다
            </h2>
            <p className="text-muted-foreground">
              OAuth 클라이언트 목록을 불러오는 중 오류가 발생했습니다.
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  if (clients === undefined || isLoading) {
    return (
      <div className="space-y-4">
        {[1, 2].map((i) => (
          <div key={i} className="border rounded-lg p-6">
            <div className="flex items-center justify-between mb-4">
              <Skeleton className="h-6 w-48" />
              <div className="flex gap-2">
                <Skeleton className="h-8 w-16" />
                <Skeleton className="h-8 w-16" />
              </div>
            </div>
            <div className="space-y-3">
              <Skeleton className="h-4 w-full" />
              <Skeleton className="h-4 w-3/4" />
            </div>
          </div>
        ))}
      </div>
    );
  }

  if (clients.length === 0) {
    return (
      <Card>
        <CardContent className="flex flex-col items-center justify-center py-12">
          <div className="text-center">
            <h3 className="text-lg font-semibold mb-2">
              OAuth 클라이언트가 없습니다
            </h3>
            <p className="text-muted-foreground">
              첫 번째 OAuth 클라이언트를 생성해보세요.
            </p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <div className="space-y-4">
      {clients.map((client) => (
        <OAuthClientItem
          key={client.id}
          client={client}
          onUpdate={onUpdate}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
}
