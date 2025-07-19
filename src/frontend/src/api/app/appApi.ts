import { axiosInstance } from '@grantly/api/axiosInstance';
import { AppType } from './app.shcema';

// 서비스 목록 조회
export const getApps = async (): Promise<AppType[]> => {
  const response = await axiosInstance.get('/admin/v1/apps');
  return response.data;
};

// 서비스 상세 조회
export const getApp = async (slug: string): Promise<AppType> => {
  const response = await axiosInstance.get(`/admin/v1/apps/${slug}`);
  return response.data;
};

// 서비스 추가
export const createApp = async (
  app: Omit<AppType, 'slug'>
): Promise<AppType> => {
  const response = await axiosInstance.post('/admin/v1/apps', app);
  return response.data;
};

// 서비스 수정
export const updateApp = async (
  slug: string,
  app: Partial<Omit<AppType, 'id'>>
): Promise<AppType> => {
  const response = await axiosInstance.put(`/admin/v1/apps/${slug}`, app);
  return response.data;
};

// 서비스 삭제
export const deleteApp = async (slug: string): Promise<void> => {
  return axiosInstance.delete(`/admin/v1/apps/${slug}`);
};
