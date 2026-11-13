# Deployment verification UI

This UI is a deliberately small deployment probe, not the final operator dashboard. It verifies one real path end to end:

`Auth0 login -> access token -> Render API -> Neon membership lookup -> browser response`

No client secret belongs in the UI. Auth0's Universal Login handles credentials, while the browser receives an audience-scoped access token.

## Local run

1. In Auth0, create or select a **Single Page Application**.
2. Add `http://localhost:3000` to Allowed Callback URLs, Allowed Logout URLs, and Allowed Web Origins.
3. Copy `apps/web/.env.example` to `apps/web/.env.local` and replace its public placeholders.
4. Ensure the API allows the local origin:

   ```text
   RESOLVEOS_WEB_ALLOWED_ORIGINS=http://localhost:3000
   ```

5. Start the UI:

   ```bash
   cd apps/web
   npm ci
   npm run dev
   ```

The login can succeed before a membership exists. In that case the UI correctly shows an empty organization list.

## Vercel deployment

Import the GitHub repository into Vercel with these settings:

- Root Directory: `apps/web`
- Framework Preset: Next.js
- Build Command: `npm run build`
- Output Directory: leave blank (Vercel uses `.next`)

Do not override the Output Directory with `out`. Vercel's Next.js adapter needs the manifests generated in `.next`.

Add these environment variables for Production and Preview:

```text
NEXT_PUBLIC_AUTH0_DOMAIN=<Auth0 tenant domain>
NEXT_PUBLIC_AUTH0_CLIENT_ID=<Auth0 SPA client ID>
NEXT_PUBLIC_AUTH0_AUDIENCE=https://api.resolveos.dev
NEXT_PUBLIC_API_BASE_URL=https://resolveos.onrender.com
```

After Vercel assigns the production URL:

1. Add the exact HTTPS URL to Auth0's Allowed Callback URLs, Allowed Logout URLs, and Allowed Web Origins.
2. Set Render's `RESOLVEOS_WEB_ALLOWED_ORIGINS` to that exact URL.
3. Redeploy both services after changing environment variables.

Do not use wildcard origins and do not add an Auth0 client secret to Vercel.

## End-to-end acceptance check

1. Confirm `https://resolveos.onrender.com/actuator/health` reports `UP`.
2. Open the Vercel URL and choose **Sign in with Auth0**.
3. Complete Universal Login.
4. Confirm the authenticated subject appears.
5. Choose **Load my organizations**.
6. Confirm only organizations backed by active Neon memberships appear.
7. In a private window, confirm the API data cannot be loaded without login.

If login succeeds but no organizations appear, add a membership for the exact Auth0 `sub` using `infra/scripts/bootstrap-organization.sql`. If the browser reports a CORS error, compare the browser origin byte-for-byte with `RESOLVEOS_WEB_ALLOWED_ORIGINS`.
