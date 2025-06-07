import * as Sentry from '@sentry/nextjs';

export class SentryService {
  static captureException(error: Error, context: Record<string, unknown>) {
    Sentry.captureException(error, {
      extra: context,
    });
  }
}
