import { axiosInstance } from '@grantly/api/axiosInstance';
import { ServiceType } from './service.shcema';
import { mockServices } from './serviceMock';

// Mock API 사용 여부
const useMock = true;

// 서비스 목록 조회
export const getServices = async (): Promise<ServiceType[]> => {
  if (useMock) {
    return Promise.resolve(mockServices);
  }
  const response = await axiosInstance.get('/v1/services');
  return response.data;
};

// 서비스 상세 조회
export const getService = async (id: number): Promise<ServiceType> => {
  if (useMock) {
    const service = mockServices.find((s) => s.id === id);
    if (!service) {
      throw new Error('Service not found');
    }
    return Promise.resolve(service);
  }
  const response = await axiosInstance.get(`/v1/services/${id}`);
  return response.data;
};

// 서비스 추가
export const createService = async (
  service: Omit<ServiceType, 'id'>
): Promise<ServiceType> => {
  if (useMock) {
    const newService = {
      ...service,
      id: Math.max(...mockServices.map((s) => s.id)) + 1,
    };
    mockServices.push(newService);
    return Promise.resolve(newService);
  }
  const response = await axiosInstance.post('/v1/services', service);
  return response.data;
};

// 서비스 수정
export const updateService = async (
  id: number,
  service: Partial<Omit<ServiceType, 'id'>>
): Promise<ServiceType> => {
  if (useMock) {
    const index = mockServices.findIndex((s) => s.id === id);
    if (index === -1) {
      throw new Error('Service not found');
    }
    mockServices[index] = { ...mockServices[index], ...service };
    return Promise.resolve(mockServices[index]);
  }
  const response = await axiosInstance.patch(`/v1/services/${id}`, service);
  return response.data;
};

// 서비스 삭제
export const deleteService = async (id: number): Promise<void> => {
  if (useMock) {
    const index = mockServices.findIndex((s) => s.id === id);
    if (index === -1) {
      throw new Error('Service not found');
    }
    mockServices.splice(index, 1);
    return Promise.resolve();
  }
  return axiosInstance.delete(`/v1/services/${id}`);
};
