import { ServiceType } from './service.shcema';

export const mockServices: ServiceType[] = [
  {
    id: 1,
    name: '웹 개발 서비스',
    description: '최신 기술을 활용한 웹 애플리케이션 개발 서비스',
    imageUrl: 'https://example.com/web-dev.jpg',
  },
  {
    id: 2,
    name: '모바일 앱 개발',
    description: 'iOS와 Android 플랫폼을 위한 네이티브 앱 개발',
    imageUrl: 'https://example.com/mobile-dev.jpg',
  },
  {
    id: 3,
    name: 'UI/UX 디자인',
    description: '사용자 중심의 직관적인 인터페이스 디자인',
    imageUrl: 'https://example.com/ui-ux.jpg',
  },
];
