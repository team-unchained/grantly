import { useQuery } from '@tanstack/react-query';
import { getApps } from './appApi';
import { GetApps } from './queryKey';

export const useGetAppsQuery = () =>
  useQuery({
    queryKey: GetApps(),
    queryFn: () => getApps(),
  });
