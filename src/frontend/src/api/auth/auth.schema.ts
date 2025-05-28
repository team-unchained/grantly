import { z } from 'zod';
import { UserType } from '@grantly/api/user/user.shcema';

export const SignUpSchema = z.object({
  email: z
    .string()
    .min(1, { message: '이메일은 필수값입니다.' })
    .max(100, { message: '이메일은 100자 이내여야 합니다.' })
    .email({ message: '이메일 형식이 올바르지 않습니다.' }),

  name: z
    .string()
    .min(2, { message: '이름은 필수값입니다.' })
    .max(50, { message: '이름은 50자 이내여야 합니다.' }),

  password: z
    .string()
    .min(8, { message: '비밀번호는 최소 8자 이상이어야 합니다.' })
    .max(100, { message: '비밀번호는 100자 이내여야 합니다.' })
    .regex(/(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&+=/^#\-_])/, {
      message: '비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.',
    }),
});

export type CreateUserRequestType = z.infer<typeof SignUpSchema>;

export type CreateUserResponseType = {
  user: UserType;
};

export const LoginSchema = z.object({
  email: z.string().min(1),
  password: z.string().min(1),
});

export type LoginRequestType = z.infer<typeof LoginSchema>;

export const RequestPasswordResetSchema = z.object({
  email: z
    .string()
    .min(1, { message: '이메일은 필수값입니다.' })
    .max(100, { message: '이메일은 100자 이내여야 합니다.' })
    .email({ message: '이메일 형식이 올바르지 않습니다.' }),
});
export type RequestPasswordResetType = z.infer<
  typeof RequestPasswordResetSchema
>;

export const ResetPasswordSchema = z
  .object({
    password: z
      .string()
      .min(8, { message: '비밀번호는 최소 8자 이상이어야 합니다.' })
      .max(100, { message: '비밀번호는 100자 이내여야 합니다.' })
      .regex(/(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&+=/^#\-_])/, {
        message: '비밀번호는 영문, 숫자, 특수문자를 포함해야 합니다.',
      }),
    confirmPassword: z
      .string()
      .min(1, { message: '비밀번호 확인은 필수입니다.' }),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: '비밀번호가 일치하지 않습니다.',
    path: ['confirmPassword'],
  });

export type ResetPasswordFormType = z.infer<typeof ResetPasswordSchema>;

export type ResetPasswordRequestType = {
  token: string;
  password: string;
};
