import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { oauthClientKeys } from '@grantly/api/oauth/queryKey';
import {
  CreateOAuthClientType,
  UpdateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';
import { toast } from 'sonner';
import {
  getOAuthClients,
  getOAuthClient,
  createOAuthClient,
  updateOAuthClient,
  deleteOAuthClient,
} from './oauthApi';

export const useGetOAuthClientsQuery = (appSlug: string) => {
  return useQuery({
    queryKey: oauthClientKeys.list(appSlug),
    queryFn: () => getOAuthClients(appSlug),
  });
};

export const useGetOAuthClientQuery = (appSlug: string, clientId: string) => {
  return useQuery({
    queryKey: oauthClientKeys.detail(appSlug, clientId),
    queryFn: () => getOAuthClient(appSlug, clientId),
    enabled: !!clientId,
  });
};

export const useCreateOAuthClientMutation = (appSlug: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateOAuthClientType) =>
      createOAuthClient(appSlug, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: oauthClientKeys.list(appSlug),
      });
      toast.success('OAuth 클라이언트가 생성되었습니다.');
    },
    onError: () => {
      toast.error('OAuth 클라이언트 생성 중 오류가 발생했습니다.');
    },
  });
};

export const useUpdateOAuthClientMutation = (appSlug: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({
      clientId,
      data,
    }: {
      clientId: string;
      data: UpdateOAuthClientType;
    }) => updateOAuthClient(appSlug, clientId, data),
    onSuccess: (_, { clientId }) => {
      queryClient.invalidateQueries({
        queryKey: oauthClientKeys.list(appSlug),
      });
      queryClient.invalidateQueries({
        queryKey: oauthClientKeys.detail(appSlug, clientId),
      });
      toast.success('OAuth 클라이언트가 업데이트되었습니다.');
    },
    onError: () => {
      toast.error('OAuth 클라이언트 업데이트 중 오류가 발생했습니다.');
    },
  });
};

export const useDeleteOAuthClientMutation = (appSlug: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (clientId: string) =>
      deleteOAuthClient(appSlug, clientId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: oauthClientKeys.list(appSlug),
      });
      toast.success('OAuth 클라이언트가 삭제되었습니다.');
    },
    onError: () => {
      toast.error('OAuth 클라이언트 삭제 중 오류가 발생했습니다.');
    },
  });
};
