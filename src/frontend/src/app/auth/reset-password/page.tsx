import { ResetPasswordForm } from '@grantly/components/password/ResetPasswordForm';
import {
  Card,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';

export default async function ResetPasswordPage({
  searchParams,
}: {
  searchParams: { token?: string };
}) {
  const { token } = searchParams;

  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
        {token ? (
          <ResetPasswordForm token={token} />
        ) : (
          <Card>
            <CardHeader>
              <CardTitle className="heading1 text-center">
                잘못된 접근입니다.
              </CardTitle>
              <CardDescription className="text-sm text-muted-foreground text-center">
                비밀번호 재설정 링크가 유효하지 않거나 만료된 것 같습니다.
                <br />
                주소를 다시 한 번 확인해 주세요.
              </CardDescription>
            </CardHeader>
          </Card>
        )}
      </div>
    </div>
  );
}
