"use client";

import { useAuth0 } from "@auth0/auth0-react";
import { useCallback, useState } from "react";
import { publicConfig } from "../lib/config";

type Organization = {
  id: string;
  slug: string;
  name: string;
  status: string;
  createdAt: string;
};

type LoadState =
  | { kind: "idle" }
  | { kind: "loading" }
  | { kind: "success"; organizations: Organization[] }
  | { kind: "error"; message: string };

export function VerificationConsole() {
  const { error, getAccessTokenSilently, isAuthenticated, isLoading, loginWithRedirect, logout, user } =
    useAuth0();
  const [state, setState] = useState<LoadState>({ kind: "idle" });

  const loadOrganizations = useCallback(async () => {
    setState({ kind: "loading" });
    try {
      const accessToken = await getAccessTokenSilently({
        authorizationParams: { audience: publicConfig.auth0Audience },
      });
      const response = await fetch(`${publicConfig.apiBaseUrl}/api/v1/me/organizations`, {
        headers: { Authorization: `Bearer ${accessToken}` },
      });
      if (!response.ok) {
        throw new Error(`ResolveOS API returned ${response.status}`);
      }
      setState({ kind: "success", organizations: (await response.json()) as Organization[] });
    } catch (loadError) {
      setState({
        kind: "error",
        message: loadError instanceof Error ? loadError.message : "Unable to load organizations",
      });
    }
  }, [getAccessTokenSilently]);

  return (
    <main className="shell">
      <section className="hero">
        <span className="eyebrow">ResolveOS · verification slice</span>
        <h1>Prove the platform path before building more.</h1>
        <p>
          This console verifies Auth0 authentication, Render API reachability, Neon persistence, and
          tenant-safe membership resolution.
        </p>
      </section>

      <section className="panel" aria-live="polite">
        {isLoading ? <p>Checking your session…</p> : null}
        {error ? <div className="error">Authentication failed: {error.message}</div> : null}

        {!isLoading && !isAuthenticated ? (
          <>
            <h2>Start the end-to-end check</h2>
            <p>Auth0 will return an access token for the ResolveOS API audience.</p>
            <button type="button" onClick={() => void loginWithRedirect()}>
              Sign in with Auth0
            </button>
          </>
        ) : null}

        {isAuthenticated ? (
          <>
            <div className="account">
              <div>
                <span className="label">Authenticated identity</span>
                <strong>{user?.name ?? user?.email ?? user?.sub}</strong>
              </div>
              <button
                className="secondary"
                type="button"
                onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
              >
                Sign out
              </button>
            </div>

            <button type="button" disabled={state.kind === "loading"} onClick={() => void loadOrganizations()}>
              {state.kind === "loading" ? "Calling ResolveOS…" : "Load my organizations"}
            </button>

            {state.kind === "error" ? <div className="error">{state.message}</div> : null}
            {state.kind === "success" && state.organizations.length === 0 ? (
              <div className="notice">Authentication works, but this Auth0 subject has no active membership yet.</div>
            ) : null}
            {state.kind === "success" && state.organizations.length > 0 ? (
              <div className="results">
                <span className="label">Verified tenant access</span>
                {state.organizations.map((organization) => (
                  <article key={organization.id}>
                    <div>
                      <h3>{organization.name}</h3>
                      <p>{organization.slug}</p>
                    </div>
                    <span className="status">{organization.status}</span>
                  </article>
                ))}
              </div>
            ) : null}
          </>
        ) : null}
      </section>
    </main>
  );
}
