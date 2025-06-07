import { useQuery } from '@tanstack/react-query';
import { getServices } from './serviceApi';
import { GetServices } from './queryKey';

export const useGetServicesQuery = () =>
  useQuery({
    queryKey: GetServices(),
    queryFn: () => getServices(),
  });
