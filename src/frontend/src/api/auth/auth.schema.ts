import { z } from 'zod';

export const signUpSchema = z.object({
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

export type CreateUserRequestType = z.infer<typeof signUpSchema>;

export const UserSchema = z.object({
  id: z.number().int(),
  email: z.string().email(),
  name: z.string(),
  createdAt: z.string().datetime(),
  modifiedAt: z.string().datetime(),
});

export type UserType = z.infer<typeof UserSchema>;

export type CreateUserResponseType = {
  user: UserType;
};
