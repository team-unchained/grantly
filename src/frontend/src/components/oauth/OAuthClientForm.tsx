'use client';

import { Button } from '@grantly/components/ui/button';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';
import { Input } from '@grantly/components/ui/input';
import { Label } from '@grantly/components/ui/label';
import { CreateOAuthClientType } from '@grantly/api/oauth/oauth.schema';
import { UseFormReturn } from 'react-hook-form';
import { Plus, X } from 'lucide-react';

interface OAuthClientFormProps {
  form: UseFormReturn<CreateOAuthClientType>;
  isEditing?: boolean;
}

export function OAuthClientForm({ form }: OAuthClientFormProps) {
  const {
    watch,
    setValue,
    register,
    formState: { errors },
  } = form;
  const redirectUris = watch('redirectUris') || [];
  const scopes = watch('scopes') || [];

  const addRedirectUri = () => {
    setValue('redirectUris', [...redirectUris, '']);
  };

  const removeRedirectUri = (index: number) => {
    setValue(
      'redirectUris',
      redirectUris.filter((_, i) => i !== index)
    );
  };

  const updateRedirectUri = (index: number, value: string) => {
    const newUris = [...redirectUris];
    newUris[index] = value;
    setValue('redirectUris', newUris);
  };

  const addScope = () => {
    setValue('scopes', [...scopes, '']);
  };

  const removeScope = (index: number) => {
    setValue(
      'scopes',
      scopes.filter((_, i) => i !== index)
    );
  };

  const updateScope = (index: number, value: string) => {
    const newScopes = [...scopes];
    newScopes[index] = value;
    setValue('scopes', newScopes);
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>기본 정보</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid gap-2">
            <Label htmlFor="title">클라이언트 이름</Label>
            <Input
              id="title"
              placeholder="예: 웹 애플리케이션, 모바일 앱"
              {...register('title')}
            />
            {errors.title && (
              <p className="text-sm text-red-500">{errors.title.message}</p>
            )}
            <p className="text-sm text-muted-foreground">
              OAuth 클라이언트를 식별할 수 있는 이름을 입력하세요.
            </p>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>리다이렉트 URI</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground">
            OAuth 인증 후 사용자를 리다이렉트할 URI를 등록하세요.
          </p>

          {redirectUris.map((uri, index) => (
            <div key={uri} className="flex gap-2">
              <input
                type="url"
                value={uri}
                onChange={(e) => updateRedirectUri(index, e.target.value)}
                placeholder="https://example.com/callback"
                className="flex-1 px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring"
              />
              <Button
                type="button"
                variant="outline"
                size="icon"
                onClick={() => removeRedirectUri(index)}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          ))}

          <Button
            type="button"
            variant="outline"
            onClick={addRedirectUri}
            className="w-full"
          >
            <Plus className="h-4 w-4 mr-2" />
            리다이렉트 URI 추가
          </Button>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>스코프</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground">
            이 클라이언트가 요청할 수 있는 권한 범위를 설정하세요.
          </p>

          {scopes.map((scope, index) => (
            <div key={scope} className="flex gap-2">
              <input
                type="text"
                value={scope}
                onChange={(e) => updateScope(index, e.target.value)}
                placeholder="read, write, admin"
                className="flex-1 px-3 py-2 border border-input rounded-md focus:outline-none focus:ring-2 focus:ring-ring"
              />
              <Button
                type="button"
                variant="outline"
                size="icon"
                onClick={() => removeScope(index)}
              >
                <X className="h-4 w-4" />
              </Button>
            </div>
          ))}

          <Button
            type="button"
            variant="outline"
            onClick={addScope}
            className="w-full"
          >
            <Plus className="h-4 w-4 mr-2" />
            스코프 추가
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
