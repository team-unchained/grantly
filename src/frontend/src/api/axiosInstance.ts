import axios, { AxiosInstance, InternalAxiosRequestConfig } from 'axios';
import { getCsrfToken } from '@grantly/api/auth/authApi';
import { getCookie } from '@grantly/utils/cookie';

export const axiosInstance: AxiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

const isUnsafeMethod = (method: string): boolean => {
  const unsafeMethods = ['post', 'put', 'delete', 'patch'];
  return unsafeMethods.includes(method);
};

axiosInstance.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    if (isUnsafeMethod(config.method ?? '')) {
      await getCsrfToken();
      const csrfToken = getCookie('CSRF-TOKEN');

      if (csrfToken) {
        config.headers.set('X-CSRF-TOKEN', csrfToken);
      }
    }
    return config;
  }
);
