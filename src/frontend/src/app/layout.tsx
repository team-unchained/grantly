import Providers from '@grantly/app/providers';
import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Grantly',
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="ko">
      <body>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
