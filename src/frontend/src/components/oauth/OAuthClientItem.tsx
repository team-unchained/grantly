'use client';

import { Button } from '@grantly/components/ui/button';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';
import { Badge } from '@grantly/components/ui/badge';
import {
  OAuthClientType,
  UpdateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';
import { DeleteOAuthClientDialog } from '@grantly/components/oauth/DeleteOAuthClientDialog';
import { UpdateOAuthClientDialog } from '@grantly/components/oauth/UpdateOAuthClientDialog';
import { Edit, Trash2, Copy, Eye, EyeOff } from 'lucide-react';
import { useState } from 'react';
import { toast } from 'sonner';
import {
  OAUTH_SCOPE_LABELS,
  OAUTH_GRANT_TYPE_LABELS,
} from '@grantly/constants/oauth';

interface OAuthClientItemProps {
  client: OAuthClientType;
  onUpdate: (clientId: string, data: UpdateOAuthClientType) => Promise<void>;
  onDelete: (clientId: string) => void;
}

export function OAuthClientItem({
  client,
  onUpdate,
  onDelete,
}: OAuthClientItemProps) {
  const [showSecret, setShowSecret] = useState(false);

  const toggleSecret = () => {
    setShowSecret((prev) => !prev);
  };

  const copyToClipboard = async (text: string, label: string) => {
    try {
      await navigator.clipboard.writeText(text);
      toast.success(`${label}이 클립보드에 복사되었습니다.`);
    } catch (error) {
      toast.error('클립보드 복사에 실패했습니다.');
    }
  };

  const handleDelete = () => {
    onDelete(client.clientId);
  };

  const handleUpdate = async (data: UpdateOAuthClientType) => {
    await onUpdate(client.clientId, data);
  };

  const handleCopyClientId = () => {
    copyToClipboard(client.clientId, 'Client ID');
  };

  const handleCopyClientSecret = () => {
    copyToClipboard(client.clientSecret, 'Client Secret');
  };

  // 스코프 라벨 가져오기
  const getScopeLabel = (scope: string) => {
    return (
      OAUTH_SCOPE_LABELS[scope as keyof typeof OAUTH_SCOPE_LABELS] || scope
    );
  };

  // 그랜트 타입 라벨 가져오기
  const getGrantTypeLabel = (grantType: string) => {
    return (
      OAUTH_GRANT_TYPE_LABELS[
        grantType as keyof typeof OAUTH_GRANT_TYPE_LABELS
      ] || grantType
    );
  };

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <CardTitle className="text-lg">{client.title}</CardTitle>
          </div>
          <div className="flex items-center gap-2">
            <UpdateOAuthClientDialog client={client} onSubmit={handleUpdate}>
              <Button variant="outline" size="sm">
                <Edit className="h-4 w-4 mr-2" />
                수정
              </Button>
            </UpdateOAuthClientDialog>
            <DeleteOAuthClientDialog client={client} onDelete={handleDelete}>
              <Button variant="outline" size="sm">
                <Trash2 className="h-4 w-4 mr-2" />
                삭제
              </Button>
            </DeleteOAuthClientDialog>
          </div>
        </div>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div>
            <div className="text-sm font-medium text-muted-foreground">
              Client ID
            </div>
            <div className="flex items-center gap-2 mt-1">
              <code className="flex-1 px-3 py-2 bg-muted rounded-md text-sm font-mono break-all">
                {client.clientId}
              </code>
              <Button variant="outline" size="sm" onClick={handleCopyClientId}>
                <Copy className="h-4 w-4" />
              </Button>
            </div>
          </div>

          <div>
            <div className="text-sm font-medium text-muted-foreground">
              Client Secret
            </div>
            <div className="flex items-center gap-2 mt-1">
              <code className="flex-1 px-3 py-2 bg-muted rounded-md text-sm font-mono break-all">
                {showSecret
                  ? client.clientSecret
                  : '•'.repeat(client.clientSecret.length)}
              </code>
              <Button variant="outline" size="sm" onClick={toggleSecret}>
                {showSecret ? (
                  <EyeOff className="h-4 w-4" />
                ) : (
                  <Eye className="h-4 w-4" />
                )}
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={handleCopyClientSecret}
              >
                <Copy className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>

        {client.redirectUris && client.redirectUris.length > 0 && (
          <div>
            <div className="text-sm font-medium text-muted-foreground">
              리다이렉트 URI
            </div>
            <div className="mt-1 space-y-1">
              {client.redirectUris.map((uri) => (
                <code
                  key={uri}
                  className="block px-3 py-2 bg-muted rounded-md text-sm font-mono break-all"
                >
                  {uri}
                </code>
              ))}
            </div>
          </div>
        )}

        {client.scopes && client.scopes.length > 0 && (
          <div>
            <div className="text-sm font-medium text-muted-foreground">
              스코프
            </div>
            <div className="flex flex-wrap gap-1 mt-1">
              {client.scopes.map((scope) => (
                <Badge key={scope} variant="outline">
                  {getScopeLabel(scope)}
                </Badge>
              ))}
            </div>
          </div>
        )}

        {client.grantType && client.grantType.length > 0 && (
          <div>
            <div className="text-sm font-medium text-muted-foreground">
              그랜트 타입
            </div>
            <div className="flex flex-wrap gap-1 mt-1">
              <Badge variant="outline">
                {getGrantTypeLabel(client.grantType)}
              </Badge>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  );
}
