"use client";

import { Auth0Provider } from "@auth0/auth0-react";
import type { ReactNode } from "react";
import { publicConfig } from "../lib/config";

export function Providers({ children }: Readonly<{ children: ReactNode }>) {
  return (
    <Auth0Provider
      domain={publicConfig.auth0Domain}
      clientId={publicConfig.auth0ClientId}
      authorizationParams={{
        audience: publicConfig.auth0Audience,
        redirect_uri: typeof window === "undefined" ? undefined : window.location.origin,
      }}
      cacheLocation="memory"
    >
      {children}
    </Auth0Provider>
  );
}
