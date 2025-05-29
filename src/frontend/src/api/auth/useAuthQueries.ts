import { AxiosError } from 'axios';
import { useMutation, UseMutationOptions } from '@tanstack/react-query';
import {
  CreateUser,
  Login,
  Logout,
  requestPasswordReset,
  resetPassword,
} from '@grantly/api/auth/authApi';
import { toast } from 'sonner';
import {
  CreateUserRequestType,
  CreateUserResponseType,
  RequestPasswordResetType,
  LoginRequestType,
  ResetPasswordRequestType,
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

export const useLoginMutation = (
  options?: UseMutationOptions<void, AxiosError, LoginRequestType, unknown>
) =>
  useMutation({
    ...options,
    mutationFn: (params: LoginRequestType) => Login(params),
    onError: (error) => {
      const status = error.response?.status;
      if (status === 401) {
        toast.error('입력한 정보를 다시 확인해주세요.');
      } else {
        toast.error('로그인 중 알 수 없는 오류가 발생했습니다.');
      }
    },
  });

export const useLogoutMutation = (
  options?: UseMutationOptions<void, AxiosError, unknown, unknown>
) =>
  useMutation({
    ...options,
    mutationFn: () => Logout(),
  });

export const useRequestPasswordResetMutation = (
  options?: UseMutationOptions<
    void,
    AxiosError,
    RequestPasswordResetType,
    unknown
  >
) =>
  useMutation({
    ...options,
    mutationFn: (params: RequestPasswordResetType) =>
      requestPasswordReset(params),
  });

export const useResetPasswordMutation = (
  options?: UseMutationOptions<
    void,
    AxiosError,
    ResetPasswordRequestType,
    unknown
  >
) =>
  useMutation({
    ...options,
    mutationFn: (params: ResetPasswordRequestType) => resetPassword(params),
  });
