'use client';

import { LoginForm } from '@grantly/components/login/LoginForm';
import React, { Suspense } from 'react';
import { useRedirectUrl } from '@grantly/hooks/useRedirectUrl';

function LoginContent() {
  const redirectUrl = useRedirectUrl('/dashboard');

  return (
    <div className="grid min-h-svh lg:grid-cols-2">
      <div className="flex flex-col gap-4 p-6 md:p-10">
        <div className="flex justify-center gap-2 md:justify-start">
          <p className="heading1">Grantly</p>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-xs">
            <LoginForm redirectUrl={redirectUrl} />
          </div>
        </div>
      </div>
      <div className="relative hidden bg-muted lg:block" />
    </div>
  );
}

export default function LoginPage() {
  return (
    <Suspense fallback={<div>Loading...</div>}>
      <LoginContent />
    </Suspense>
  );
}
