export const oauthClientKeys = {
  all: ['oauth-clients'] as const,
  lists: () => [...oauthClientKeys.all, 'list'] as const,
  list: (appSlug: string) => [...oauthClientKeys.lists(), appSlug] as const,
  details: () => [...oauthClientKeys.all, 'detail'] as const,
  detail: (appSlug: string, clientId: string) =>
    [...oauthClientKeys.details(), appSlug, clientId] as const,
};
