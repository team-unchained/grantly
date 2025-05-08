import { getMe } from '@grantly/api/user/userApi';
import { useQuery } from '@tanstack/react-query';
import { GetMe } from '@grantly/api/user/queryKey';

export const useGetMeQuery = () =>
  useQuery({
    queryKey: GetMe(),
    queryFn: () => getMe(),
  });
