import { AxiosError } from 'axios';
import { useMutation, UseMutationOptions } from '@tanstack/react-query';
import { CreateUser } from '@grantly/api/auth/authApi';
import { toast } from 'sonner';
import {
  CreateUserRequestType,
  CreateUserResponseType,
} from '@grantly/api/auth/auth.schema';

export const useCreateUserMutation = (
  options?: UseMutationOptions<
    CreateUserResponseType,
    AxiosError,
    CreateUserRequestType,
    unknown
  >
) =>
  useMutation({
    ...options,
    mutationFn: (params: CreateUserRequestType) => CreateUser(params),
    onError: (error) => {
      const status = error.response?.status;
      if (status === 409) {
        toast.error('이미 가입된 이메일입니다.');
      } else if (status === 422) {
        toast.error('입력한 정보를 다시 확인해주세요.');
      } else {
        toast.error('회원가입 중 알 수 없는 오류가 발생했습니다.');
      }
    },
  });
