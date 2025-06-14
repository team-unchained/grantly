import { axiosInstance } from '@grantly/api/axiosInstance';
import { AppType } from './app.shcema';
import { mockApps } from './appMock';

// Mock API 사용 여부
const useMock = true;

// 서비스 목록 조회
export const getApps = async (): Promise<AppType[]> => {
  if (useMock) {
    return Promise.resolve(mockApps);
  }
  const response = await axiosInstance.get('/v1/apps');
  return response.data;
};

// 서비스 상세 조회
export const getApp = async (id: number): Promise<AppType> => {
  if (useMock) {
    const app = mockApps.find((s) => s.id === id);
    if (!app) {
      throw new Error('App not found');
    }
    return Promise.resolve(app);
  }
  const response = await axiosInstance.get(`/v1/apps/${id}`);
  return response.data;
};

// 서비스 추가
export const createApp = async (app: Omit<AppType, 'id'>): Promise<AppType> => {
  if (useMock) {
    const newApp = {
      ...app,
      id: Math.max(...mockApps.map((s) => s.id)) + 1,
    };
    mockApps.push(newApp);
    return Promise.resolve(newApp);
  }
  const response = await axiosInstance.post('/v1/apps', app);
  return response.data;
};

// 서비스 수정
export const updateApp = async (
  id: number,
  app: Partial<Omit<AppType, 'id'>>
): Promise<AppType> => {
  if (useMock) {
    const index = mockApps.findIndex((s) => s.id === id);
    if (index === -1) {
      throw new Error('App not found');
    }
    mockApps[index] = { ...mockApps[index], ...app };
    return Promise.resolve(mockApps[index]);
  }
  const response = await axiosInstance.patch(`/v1/apps/${id}`, app);
  return response.data;
};

// 서비스 삭제
export const deleteApp = async (id: number): Promise<void> => {
  if (useMock) {
    const index = mockApps.findIndex((s) => s.id === id);
    if (index === -1) {
      throw new Error('App not found');
    }
    mockApps.splice(index, 1);
    return Promise.resolve();
  }
  return axiosInstance.delete(`/v1/apps/${id}`);
};
