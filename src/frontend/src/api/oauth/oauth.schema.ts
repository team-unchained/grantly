import { OAuthGrantType, OAuthScope } from '@grantly/constants/oauth';
import { z } from 'zod';

export const OAuthClientSchema = z.object({
  appId: z.number(),
  title: z.string(),
  clientId: z.string(),
  clientSecret: z.string(),
  redirectUris: z.array(z.string()),
  scopes: z.array(z.nativeEnum(OAuthScope)),
  grantType: z.nativeEnum(OAuthGrantType),
});

export const CreateOAuthClientSchema = z.object({
  title: z.string().min(1, '제목을 입력해주세요'),
  redirectUris: z
    .array(z.string().url('올바른 URL을 입력해주세요'))
    .default([]),
  scopes: z.array(z.nativeEnum(OAuthScope)).default([OAuthScope.ALL]),
  grantType: z
    .nativeEnum(OAuthGrantType)
    .default(OAuthGrantType.AUTHORIZATION_CODE),
});

export const UpdateOAuthClientSchema = CreateOAuthClientSchema;

export type OAuthClientType = z.infer<typeof OAuthClientSchema>;
export type CreateOAuthClientType = z.infer<typeof CreateOAuthClientSchema>;
export type UpdateOAuthClientType = z.infer<typeof UpdateOAuthClientSchema>;
