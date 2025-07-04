import { axiosInstance } from '@grantly/api/axiosInstance';
import {
  CreateOAuthClientType,
  OAuthClientType,
  UpdateOAuthClientType,
} from '@grantly/api/oauth/oauth.schema';

export const getOAuthClients = async (
  appSlug: string
): Promise<OAuthClientType[]> => {
  const response = await axiosInstance.get(
    `/api/apps/${appSlug}/oauth-clients`
  );
  return response.data;
};

export const getOAuthClient = async (
  appSlug: string,
  clientId: string
): Promise<OAuthClientType> => {
  const response = await axiosInstance.get(
    `/api/apps/${appSlug}/oauth-clients/${clientId}`
  );
  return response.data;
};

export const createOAuthClient = async (
  appSlug: string,
  data: CreateOAuthClientType
): Promise<OAuthClientType> => {
  const response = await axiosInstance.post(
    `/api/apps/${appSlug}/oauth-clients`,
    data
  );
  return response.data;
};

export const updateOAuthClient = async (
  appSlug: string,
  clientId: string,
  data: UpdateOAuthClientType
): Promise<OAuthClientType> => {
  const response = await axiosInstance.put(
    `/api/apps/${appSlug}/oauth-clients/${clientId}`,
    data
  );
  return response.data;
};

export const deleteOAuthClient = async (
  appSlug: string,
  clientId: string
): Promise<void> => {
  await axiosInstance.delete(`/api/apps/${appSlug}/oauth-clients/${clientId}`);
};
