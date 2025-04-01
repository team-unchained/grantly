import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';
import { Button } from '@grantly/components/ui/button';
import Link from 'next/link';

export default function Page() {
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
        <div className="flex flex-col gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="heading1">Welcome to Grantly!</CardTitle>
              <CardDescription>
                You’ve successfully created your account.
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link href="/login">
                <Button>Go to Login</Button>
              </Link>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
