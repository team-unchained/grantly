import { z } from 'zod';

export const OAuthClientSchema = z.object({
  id: z.string(),
  title: z.string(),
  clientId: z.string(),
  clientSecret: z.string(),
  redirectUris: z.array(z.string()).optional(),
  scopes: z.array(z.string()).optional(),
  createdAt: z.string(),
  updatedAt: z.string(),
});

export const CreateOAuthClientSchema = z.object({
  title: z.string().min(1, '제목을 입력해주세요'),
  redirectUris: z.array(z.string().url('올바른 URL을 입력해주세요')).optional(),
  scopes: z.array(z.string()).optional(),
});

export const UpdateOAuthClientSchema = CreateOAuthClientSchema.partial();

export type OAuthClientType = z.infer<typeof OAuthClientSchema>;
export type CreateOAuthClientType = z.infer<typeof CreateOAuthClientSchema>;
export type UpdateOAuthClientType = z.infer<typeof UpdateOAuthClientSchema>;
