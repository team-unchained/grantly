export const AppKey = ['app'];

export const GetApps = () => [...AppKey];

export const GetApp = (appSlu: string) => [...AppKey, appSlu];
