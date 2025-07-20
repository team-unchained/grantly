// OAuth 스코프 enum
export enum OAuthScope {
  ALL = 'all',
}

// OAuth 그랜트 타입 enum
export enum OAuthGrantType {
  AUTHORIZATION_CODE = 'authorization_code',
  IMPLICIT = 'implicit',
  CLIENT_CREDENTIALS = 'client_credentials',
  REFRESH_TOKEN = 'refresh_token',
}

// 스코프 라벨 매핑
export const OAUTH_SCOPE_LABELS: Record<OAuthScope, string> = {
  [OAuthScope.ALL]: '모든 권한',
};

// 그랜트 타입 라벨 매핑
export const OAUTH_GRANT_TYPE_LABELS: Record<OAuthGrantType, string> = {
  [OAuthGrantType.AUTHORIZATION_CODE]: '인증 코드',
  [OAuthGrantType.IMPLICIT]: '암묵적 권한 부여',
  [OAuthGrantType.CLIENT_CREDENTIALS]: '클라이언트 자격 증명',
  [OAuthGrantType.REFRESH_TOKEN]: '리프레시 토큰',
};

export const ScopeOptions = Object.entries(OAUTH_SCOPE_LABELS).map(
  ([value, label]) => ({
    value,
    label,
  })
);

export const GrantTypeOptions = Object.entries(OAUTH_GRANT_TYPE_LABELS).map(
  ([value, label]) => ({
    value,
    label,
  })
);
