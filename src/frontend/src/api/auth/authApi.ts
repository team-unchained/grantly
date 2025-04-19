import {
  CreateUserRequestType,
  CreateUserResponseType,
  LoginRequestType,
} from '@grantly/api/auth/auth.schema';
import { axiosInstance } from '../axiosInstance';

export const CreateUser = async (
  params: CreateUserRequestType
): Promise<CreateUserResponseType> => {
  const response = await axiosInstance.post('/v1/auth/signup', params);
  return response.data;
};

export const Login = async (params: LoginRequestType): Promise<void> => {
  await axiosInstance.post('/v1/auth/login', params);
};

export const getCsrfToken = async (): Promise<void> => {
  await axiosInstance.get('/v1/auth/csrf-token');
};
