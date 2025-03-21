import axios, { AxiosInstance } from 'axios';
import { CreateUserRequestType } from '@grantly/api/auth/auth.schema';

const authInstance: AxiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true,
});

export const SignUp = async (params: CreateUserRequestType) => {
  const response = await authInstance.post('v1/auth/signup', params);
  return response.data;
};
