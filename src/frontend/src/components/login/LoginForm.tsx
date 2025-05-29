'use client';

import Link from 'next/link';
import { Button } from '@grantly/components/ui/button';
import { FormInputField } from '@grantly/components/common/FormInputField';
import { useRouter } from 'next/navigation';
import { useForm } from 'react-hook-form';
import { LoginRequestType, LoginSchema } from '@grantly/api/auth/auth.schema';
import { zodResolver } from '@hookform/resolvers/zod';
import { useLoginMutation } from '@grantly/api/auth/useAuthQueries';

export const LoginForm = ({ redirectUrl }: { redirectUrl: string }) => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginRequestType>({
    resolver: zodResolver(LoginSchema),
  });

  const { mutate: login, isPending } = useLoginMutation({
    onSuccess: () => {
      router.replace(redirectUrl);
    },
  });

  const onSubmit = (data: LoginRequestType) => {
    login(data);
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex flex-col items-center gap-2 text-center">
        <h1 className="text-3xl font-bold">Login</h1>
      </div>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div className="grid gap-6">
          {/* Email */}
          <FormInputField
            id="email"
            label="Email"
            type="email"
            error={errors.email?.message}
            register={register}
          />
          {/* Password */}
          <FormInputField
            id="password"
            label="Password"
            type="password"
            error={errors.password?.message}
            register={register}
          />
          <Button type="submit" className="w-full" disabled={isPending}>
            {isPending ? 'Loading...' : 'Login'}
          </Button>
        </div>
      </form>
      <div className="text-center text-sm">
        <Link
          href="/auth/forgot-password"
          className="underline underline-offset-4"
        >
          비밀번호 찾기
        </Link>
        &nbsp;|&nbsp;
        <Link href="/auth/sign-up" className="underline underline-offset-4">
          회원 가입
        </Link>
      </div>
    </div>
  );
};
