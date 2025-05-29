'use client';

import { useRouter } from 'next/navigation';
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

import { useCreateUserMutation } from '@grantly/api/auth/useAuthQueries';
import { FormInputField } from '@grantly/components/common/FormInputField';
import {
  CreateUserRequestType,
  SignUpSchema,
} from '@grantly/api/auth/auth.schema';

export function SignUpFormCard() {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateUserRequestType>({
    resolver: zodResolver(SignUpSchema),
  });

  const { mutate: createUser, isPending } = useCreateUserMutation({
    onSuccess: () => {
      router.replace('/auth/sign-up/welcome');
    },
  });

  const onSubmit = (data: CreateUserRequestType) => {
    createUser(data);
  };

  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle className="heading1">Sign Up</CardTitle>
          <CardDescription>
            Sign up now and enjoy seamless access to our platform.
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
              {/* userName */}
              <FormInputField
                id="name"
                label="User Name"
                error={errors.name?.message}
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
                {isPending ? 'Signing up...' : 'Sign Up'}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
