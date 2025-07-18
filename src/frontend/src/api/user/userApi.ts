import { axiosInstance } from '@grantly/api/axiosInstance';
import { UserType } from '@grantly/api/user/user.shcema';

export const getMe = async (): Promise<UserType> => {
  const response = await axiosInstance.get('/admin/v1/members/me');
  return response.data;
};
