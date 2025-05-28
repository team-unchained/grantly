import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from '@grantly/components/ui/card';

export default function Page() {
  return (
    <div className="flex min-h-svh w-full items-center justify-center p-6 md:p-10">
      <div className="w-full max-w-sm">
        <div className="flex flex-col gap-6">
          <Card>
            <CardHeader>
              <CardTitle className="heading1 text-center">
                이메일 전송 완료!
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
              작성하신 이메일 주소로 비밀번호 재설정 링크를 보냈습니다.
              <br />
              이메일을 확인하시고 링크를 클릭하여 비밀번호를 재설정해 주세요.
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
}
