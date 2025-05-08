'use client';

import React from 'react';
import { Button } from '@grantly/components/ui/button';
import { useLogoutMutation } from '@grantly/api/auth/useAuthQueries';

export default function Page() {
  const { mutate: logout } = useLogoutMutation();

  return (
    <div>
      dashboard
      <Button onClick={logout}>Logout</Button>
    </div>
  );
}
