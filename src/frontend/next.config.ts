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

export default nextConfig;
