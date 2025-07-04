import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { oauthClientKeys } from '@grantly/api/oauth/queryKey';
import {
  CreateOAuthClientType,
  UpdateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';
import { toast } from 'sonner';
import { deleteOAuthClient } from './oauthApi';

// Mock 데이터 생성 함수
const generateMockOAuthClients = () => [
  {
    id: '1',
    title: '웹 애플리케이션',
    clientId: 'web_app_client_123',
    clientSecret: 'web_app_secret_456',
    redirectUris: ['https://example.com/callback'],
    scopes: ['all'],
    isActive: true,
    createdAt: '2024-01-01T00:00:00Z',
    updatedAt: '2024-01-01T00:00:00Z',
  },
  {
    id: '2',
    title: '모바일 앱',
    clientId: 'mobile_app_client_789',
    clientSecret: 'mobile_app_secret_012',
    redirectUris: ['com.example.app://callback'],
    scopes: ['all'],
    isActive: true,
    createdAt: '2024-01-02T00:00:00Z',
    updatedAt: '2024-01-02T00:00:00Z',
  },
];

// Mock 데이터를 사용하는 쿼리 훅
export const useGetOAuthClientsQuery = (appSlug: string) => {
  return useQuery({
    queryKey: oauthClientKeys.list(appSlug),
    queryFn: async () => {
      // 실제 API가 준비되면 이 부분을 실제 API 호출로 교체
      // return getOAuthClients(appSlug);

      // Mock 데이터 반환
      // eslint-disable-next-line no-promise-executor-return
      await new Promise((resolve) => setTimeout(resolve, 500)); // 로딩 시뮬레이션
      return generateMockOAuthClients();
    },
  });
};

export const useGetOAuthClientQuery = (appSlug: string, clientId: string) => {
  return useQuery({
    queryKey: oauthClientKeys.detail(appSlug, clientId),
    queryFn: async () => {
      // 실제 API가 준비되면 이 부분을 실제 API 호출로 교체
      // return getOAuthClient(appSlug, clientId);

      // Mock 데이터 반환
      // eslint-disable-next-line no-promise-executor-return
      await new Promise((resolve) => setTimeout(resolve, 300));
      const mockClients = generateMockOAuthClients();
      const client = mockClients.find((c) => c.id === clientId);
      if (!client) {
        throw new Error('OAuth client not found');
      }
      return client;
    },
    enabled: !!clientId,
  });
};

export const useCreateOAuthClientMutation = (appSlug: string) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: CreateOAuthClientType) => {
      // 실제 API가 준비되면 이 부분을 실제 API 호출로 교체
      // return createOAuthClient(appSlug, data);

      // Mock 응답 생성
      // eslint-disable-next-line no-promise-executor-return
      await new Promise((resolve) => setTimeout(resolve, 1000));
      const newClient = {
        id: Date.now().toString(),
        title: data.title,
        clientId: `${data.title.toLowerCase().replace(/\s+/g, '_')}_client_${Math.random().toString(36).substr(2, 9)}`,
        clientSecret: `secret_${Math.random().toString(36).substr(2, 15)}`,
        redirectUris: data.redirectUris || [],
        scopes: data.scopes || ['read'],
        isActive: true,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      return newClient;
    },
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
    }) => {
      // 실제 API가 준비되면 이 부분을 실제 API 호출로 교체
      // return updateOAuthClient(appSlug, clientId, data);

      // Mock 응답 생성
      // eslint-disable-next-line no-promise-executor-return
      await new Promise((resolve) => setTimeout(resolve, 800));
      const mockClients = generateMockOAuthClients();
      const existingClient = mockClients.find((c) => c.id === clientId);
      if (!existingClient) {
        throw new Error('OAuth client not found');
      }

      return {
        ...existingClient,
        ...data,
        updatedAt: new Date().toISOString(),
      };
    },
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
