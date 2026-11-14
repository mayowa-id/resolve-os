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

function authenticationMessage(message: string): string {
  if (message.includes("Service not found")) {
    return "The ResolveOS API audience is not registered in this Auth0 tenant. Create an Auth0 API with the exact identifier configured in NEXT_PUBLIC_AUTH0_AUDIENCE.";
  }
  return message;
}

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
      <header className="topbar">
        <a className="brand" href="/" aria-label="ResolveOS home">
          <span className="brand-mark" aria-hidden="true">R</span>
          <span>ResolveOS</span>
        </a>
        <div className="environment"><span aria-hidden="true" /> Verification environment</div>
      </header>

      <section className="hero">
        <div className="hero-copy">
          <span className="eyebrow">Agentic operations layer / 001</span>
          <h1>From intent to<br /><em>verified action.</em></h1>
          <p>
            ResolveOS turns complex operational intent into governed, traceable execution. This
            environment proves the identity and tenant boundary beneath that system.
          </p>
        </div>
        <div className="system-map" aria-hidden="true">
          <div className="orbit orbit-one" />
          <div className="orbit orbit-two" />
          <div className="core"><span>R</span></div>
          <span className="node node-one" />
          <span className="node node-two" />
          <span className="node node-three" />
          <span className="map-label label-intent">Intent</span>
          <span className="map-label label-policy">Policy</span>
          <span className="map-label label-action">Action</span>
        </div>
      </section>

      <section className="panel" aria-live="polite">
        <div className="panel-heading">
          <div>
            <span className="eyebrow">Identity gateway</span>
            <h2>{isAuthenticated ? "Tenant context established" : "Establish a secure session"}</h2>
          </div>
          <span className="panel-index">AUTH / 01</span>
        </div>

        <div className="signal-path" aria-label="Verification path">
          <div className="signal-step active"><span>01</span><strong>Identity</strong></div>
          <div className="signal-line" />
          <div className={`signal-step ${isAuthenticated ? "active" : ""}`}><span>02</span><strong>Policy</strong></div>
          <div className="signal-line" />
          <div className={`signal-step ${state.kind === "success" ? "active" : ""}`}><span>03</span><strong>Tenant</strong></div>
        </div>

        {isLoading ? <p>Checking your session...</p> : null}
        {error ? (
          <div className="error">
            <strong>Authentication failed</strong>
            <span>{authenticationMessage(error.message)}</span>
          </div>
        ) : null}

        {!isLoading && !isAuthenticated ? (
          <>
            <p>Continue through the managed identity gateway to verify your ResolveOS access.</p>
            <button type="button" onClick={() => void loginWithRedirect()}>
              Continue securely <span aria-hidden="true">-&gt;</span>
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
              <button className="secondary" type="button" onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}>
                Sign out
              </button>
            </div>

            <button type="button" disabled={state.kind === "loading"} onClick={() => void loadOrganizations()}>
              {state.kind === "loading" ? "Resolving tenant context..." : "Resolve tenant context"}
            </button>

            {state.kind === "error" ? <div className="error"><span>{state.message}</span></div> : null}
            {state.kind === "success" && state.organizations.length === 0 ? (
              <div className="notice">Identity verified. This account has no active ResolveOS membership yet.</div>
            ) : null}
            {state.kind === "success" && state.organizations.length > 0 ? (
              <div className="results">
                <span className="label">Verified tenant access</span>
                {state.organizations.map((organization) => (
                  <article key={organization.id}>
                    <div><h3>{organization.name}</h3><p>{organization.slug}</p></div>
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
