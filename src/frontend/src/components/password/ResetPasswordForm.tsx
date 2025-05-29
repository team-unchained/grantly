'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';

import { Button } from '@grantly/components/ui/button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';

import { FormInputField } from '@grantly/components/common/FormInputField';
import {
  ResetPasswordFormType,
  ResetPasswordSchema,
} from '@grantly/api/auth/auth.schema';
import { useResetPasswordMutation } from '@grantly/api/auth/useAuthQueries';
import { toast } from 'sonner';
import { useRouter } from 'next/navigation';

export const ResetPasswordForm = ({ token }: { token: string }) => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ResetPasswordFormType>({
    resolver: zodResolver(ResetPasswordSchema),
  });

  const { mutate: ResetPassword, isPending } = useResetPasswordMutation({
    onSuccess: () => {
      toast.info('비밀번호가 성공적으로 재설정되었습니다.');
      router.replace('/auth/login');
    },
  });

  const onSubmit = (data: ResetPasswordFormType) => {
    ResetPassword({ token, password: data.password });
  };

  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle className="heading1">Reset Password</CardTitle>
          <CardDescription>
            비밀번호 재설정을 위해 새 비밀번호를 입력해 주세요.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-6">
              {/* Password */}
              <FormInputField
                id="password"
                label="Password"
                type="password"
                error={errors.password?.message}
                register={register}
              />
              {/* Comfirm Password */}
              <FormInputField
                id="confirmPassword"
                label="Confirm Password"
                type="password"
                error={errors.confirmPassword?.message}
                register={register}
              />
              <Button type="submit" className="w-full" disabled={isPending}>
                {isPending ? 'Resetting...' : 'Reset Password'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
