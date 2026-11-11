function required(name: string, value: string | undefined): string {
  if (!value) {
    throw new Error(`Missing required public configuration: ${name}`);
  }
  return value.replace(/\/$/, "");
}

export const publicConfig = {
  auth0Domain: required("NEXT_PUBLIC_AUTH0_DOMAIN", process.env.NEXT_PUBLIC_AUTH0_DOMAIN),
  auth0ClientId: required("NEXT_PUBLIC_AUTH0_CLIENT_ID", process.env.NEXT_PUBLIC_AUTH0_CLIENT_ID),
  auth0Audience: required("NEXT_PUBLIC_AUTH0_AUDIENCE", process.env.NEXT_PUBLIC_AUTH0_AUDIENCE),
  apiBaseUrl: required("NEXT_PUBLIC_API_BASE_URL", process.env.NEXT_PUBLIC_API_BASE_URL),
};
