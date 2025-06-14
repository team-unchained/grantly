import { AlertCircle, RefreshCw, Home, Link as LinkIcon } from 'lucide-react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

interface ErrorComponentProps {
  title: string;
  description?: string;
  refresh?: boolean;
  goHome?: boolean;
  customLink?: {
    label: string;
    href: string;
  };
}

export const ErrorComponent = ({
  title,
  description,
  refresh = false,
  goHome = false,
  customLink,
}: ErrorComponentProps) => {
  const router = useRouter();

  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <div className="flex items-center gap-2 text-destructive">
        <AlertCircle className="w-8 h-8" />
        <h2 className="text-lg font-semibold">{title}</h2>
      </div>
      {description && (
        <p className="text-sm text-muted-foreground text-center">
          {description}
        </p>
      )}
      <div className="flex gap-3">
        {refresh && (
          <button
            type="button"
            onClick={() => router.refresh()}
            className="flex items-center px-4 py-2 text-sm text-white bg-primary rounded-md hover:bg-primary/90"
          >
            <RefreshCw className="w-4 h-4 mr-2" />
            새로고침
          </button>
        )}
        {goHome && (
          <Link
            href="/apps"
            className="flex items-center px-4 py-2 text-sm bg-secondary text-secondary-foreground rounded-md hover:bg-secondary/80"
          >
            <Home className="w-4 h-4 mr-2" />
            홈으로 가기
          </Link>
        )}
        {customLink && (
          <Link
            href={customLink.href}
            className="flex items-center px-4 py-2 text-sm bg-muted text-foreground rounded-md hover:bg-muted/80"
          >
            <LinkIcon className="w-4 h-4 mr-2" />
            {customLink.label}
          </Link>
        )}
      </div>
    </div>
  );
};
