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

import { useRequestPasswordResetMutation } from '@grantly/api/auth/useAuthQueries';
import { FormInputField } from '@grantly/components/common/FormInputField';
import {
  RequestPasswordResetType,
  RequestPasswordResetSchema,
} from '@grantly/api/auth/auth.schema';
import { useRouter } from 'next/navigation';

export const ForgotPasswordFormCard = () => {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RequestPasswordResetType>({
    resolver: zodResolver(RequestPasswordResetSchema),
  });

  const { mutate: RequestPasswordReset, isPending } =
    useRequestPasswordResetMutation({
      onSuccess: () => {
        router.replace('/auth/forgot-password/done');
      },
    });

  const onSubmit = (data: RequestPasswordResetType) => {
    RequestPasswordReset(data);
  };

  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle className="heading1">Forgot Password</CardTitle>
          <CardDescription>
            가입하신 이메일 주소를 입력해 주세요. 비밀번호 재설정 링크를
            보내드립니다.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)}>
            <div className="flex flex-col gap-6">
              {/* Email */}
              <FormInputField
                id="email"
                label="Email"
                type="email"
                error={errors.email?.message}
                register={register}
              />
              <Button type="submit" className="w-full" disabled={isPending}>
                {isPending ? 'Sending' : 'Send'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
};
