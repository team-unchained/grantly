import {
  CreateUserRequestType,
  CreateUserResponseType,
} from '@grantly/api/auth/auth.schema';
import { axiosInstance } from '../axiosInstance';

export const CreateUser = async (
  params: CreateUserRequestType
): Promise<CreateUserResponseType> => {
  const response = await axiosInstance.post('v1/auth/signup', params);
  return response.data;
};
