import { axiosInstance } from '@grantly/api/axiosInstance';
import { UserType } from '@grantly/api/user/user.shcema';

export const getMe = async (): Promise<UserType> => {
  const response = await axiosInstance.get('/v1/users/me');
  return response.data;
};
