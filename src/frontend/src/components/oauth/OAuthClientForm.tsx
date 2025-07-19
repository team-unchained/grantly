'use client';

import {
  CreateOAuthClientType,
  UpdateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';
import { Button } from '@grantly/components/ui/button';
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';
import { Input } from '@grantly/components/ui/input';
import { Label } from '@grantly/components/ui/label';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@grantly/components/ui/select';
import {
  GrantTypeOptions,
  OAuthGrantType,
  OAuthScope,
  ScopeOptions,
} from '@grantly/constants/oauth';
import { Plus, X } from 'lucide-react';
import { FieldValues, UseFormReturn, useFieldArray } from 'react-hook-form';

type EditOAuthClientType = (CreateOAuthClientType | UpdateOAuthClientType) &
  FieldValues;

interface OAuthClientFormProps {
  form: UseFormReturn<EditOAuthClientType>;
}

export function OAuthClientForm({ form }: OAuthClientFormProps) {
  const {
    control,
    register,
    setValue,
    formState: { errors },
  } = form;

  // useFieldArray로 배열 필드 관리
  const {
    fields: redirectUriFields,
    append: appendRedirectUri,
    remove: removeRedirectUri,
  } = useFieldArray<EditOAuthClientType>({
    control,
    name: 'redirectUris',
  });

  const {
    fields: scopeFields,
    append: appendScope,
    remove: removeScope,
  } = useFieldArray<EditOAuthClientType>({
    control,
    name: 'scopes',
  });

  return (
    <form className="space-y-6">
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
          <div className="space-y-2">
            {redirectUriFields.map((field, index) => (
              <div key={field.id} className="flex gap-2">
                <input
                  type="url"
                  {...register(`redirectUris.${index}`)}
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
              onClick={() => appendRedirectUri('')}
              className="w-full"
            >
              <Plus className="h-4 w-4 mr-2" />
              리다이렉트 URI 추가
            </Button>
          </div>
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
          <div className="space-y-2">
            {scopeFields.map((field, index) => (
              <div key={field.id} className="flex gap-2">
                <Select
                  value={form.getValues(`scopes.${index}`) ?? ''}
                  onValueChange={(value) =>
                    setValue(`scopes.${index}`, value as OAuthScope)
                  }
                >
                  <SelectTrigger className="flex-1">
                    <SelectValue placeholder="스코프를 선택하세요" />
                  </SelectTrigger>
                  <SelectContent>
                    {ScopeOptions.map((option) => (
                      <SelectItem key={option.value} value={option.value}>
                        {option.label}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
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
              onClick={() => appendScope(ScopeOptions[0].value)}
              className="w-full"
            >
              <Plus className="h-4 w-4 mr-2" />
              스코프 추가
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>그랜트 타입</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <p className="text-sm text-muted-foreground">
            이 클라이언트가 지원할 OAuth 그랜트 타입을 설정하세요.
          </p>
          <div className="space-y-2">
            <Select
              value={form.getValues('grantType') || ''}
              onValueChange={(value) =>
                setValue('grantType', value as OAuthGrantType)
              }
            >
              <SelectTrigger>
                <SelectValue placeholder="그랜트 타입을 선택하세요" />
              </SelectTrigger>
              <SelectContent>
                {GrantTypeOptions.map((option) => (
                  <SelectItem key={option.value} value={option.value}>
                    {option.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            {errors.grantType && (
              <p className="text-sm text-red-500">{errors.grantType.message}</p>
            )}
          </div>
        </CardContent>
      </Card>
    </form>
  );
}
