import { withSentryConfig } from '@sentry/nextjs';
import type { NextConfig } from 'next';
import dotenvFlow from 'dotenv-flow';

// dotenv-flow 라이브러리 사용
dotenvFlow.config({
  // 환경 변수에서 ENVIRONMENT 값을 가져와 NODE_ENV로 사용
  // 설정되지 않은 경우 'development' 기본값 사용
  node_env: process.env.ENVIRONMENT || 'local',

  // 디버그 모드 (환경 변수 로드 과정 출력)
  debug: process.env.DEBUG === 'true',
  path: './environments',
});

const nextConfig: NextConfig = {
  poweredByHeader: false,
  reactStrictMode: true,
};

export default withSentryConfig(nextConfig, {
  // For all available options, see:
  // https://www.npmjs.com/package/@sentry/webpack-plugin#options

  org: 'unchained-sk',
  project: 'javascript-nextjs',

  // Only print logs for uploading source maps in CI
  silent: !process.env.CI,

  // For all available options, see:
  // https://docs.sentry.io/platforms/javascript/guides/nextjs/manual-setup/

  // Upload a larger set of source maps for prettier stack traces (increases build time)
  widenClientFileUpload: true,

  // Route browser requests to Sentry through a Next.js rewrite to circumvent ad-blockers.
  // This can increase your server load as well as your hosting bill.
  // Note: Check that the configured route will not match with your Next.js middleware, otherwise reporting of client-
  // side errors will fail.
  tunnelRoute: '/monitoring',

  // Automatically tree-shake Sentry logger statements to reduce bundle size
  disableLogger: true,

  // Enables automatic instrumentation of Vercel Cron Monitors. (Does not yet work with App Router route handlers.)
  // See the following for more information:
  // https://docs.sentry.io/product/crons/
  // https://vercel.com/docs/cron-jobs
  automaticVercelMonitors: true,
});
