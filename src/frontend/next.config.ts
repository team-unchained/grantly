import type { NextConfig } from 'next';
import dotenvFlow from 'dotenv-flow';

dotenvFlow.config({
  path: './environments',
});

const nextConfig: NextConfig = {
  /* config options here */
};

export default nextConfig;
