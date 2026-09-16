/**
 * Cloudflare Worker — update-channel proxy for the "الطقس" (Weather) app.
 *
 * WHY THIS EXISTS
 * ----------------
 * The Android app never talks to github.com directly. Instead it only ever
 * calls this worker's own address. This worker fetches the real files from
 * the private GitHub Release *server-side* and streams them back — the
 * GitHub owner/repo name is configured only here, on Cloudflare, and is
 * never present anywhere in the compiled APK or in any network response the
 * app receives. Inspecting the app (decompiling it, sniffing its traffic)
 * only ever reveals this worker's own domain.
 *
 * ROUTES
 * ------
 *   GET /download      -> streams the latest weather.apk
 *   GET /version.json  -> streams the latest version.json
 *
 * DEPLOYING (free, no CLI needed, ~2 minutes)
 * --------------------------------------------
 * 1. Go to https://dash.cloudflare.com -> Workers & Pages -> Create -> Create Worker.
 * 2. Give it any name (you already chose "weather-apk-download").
 * 3. Click "Edit code", delete the sample code, paste this whole file, click "Deploy".
 * 4. Confirm GITHUB_OWNER / GITHUB_REPO below match your actual repository.
 * 5. That's it — the free tier (100,000 requests/day) is more than enough
 *    for an update-check endpoint.
 */

const GITHUB_OWNER = "ossamasal2012";
const GITHUB_REPO = "weather";
const RELEASE_TAG = "latest";

export default {
  async fetch(request) {
    const url = new URL(request.url);

    if (request.method !== "GET" && request.method !== "HEAD") {
      return new Response("Method not allowed", { status: 405 });
    }

    if (url.pathname === "/download") {
      return proxyAsset("weather.apk", "application/vnd.android.package-archive", "weather.apk");
    }

    if (url.pathname === "/version.json" || url.pathname === "/version") {
      return proxyAsset("version.json", "application/json; charset=utf-8", null);
    }

    return new Response("Not found", { status: 404 });
  },
};

async function proxyAsset(assetName, contentType, downloadFilename) {
  const origin = `https://github.com/${GITHUB_OWNER}/${GITHUB_REPO}/releases/download/${RELEASE_TAG}/${assetName}`;

  let upstream;
  try {
    upstream = await fetch(origin, {
      redirect: "follow",
      cf: { cacheTtl: 0, cacheEverything: false },
    });
  } catch (err) {
    return new Response("Upstream fetch error", { status: 502 });
  }

  if (!upstream.ok || !upstream.body) {
    return new Response("Upstream asset not available", { status: 502 });
  }

  const headers = new Headers();
  headers.set("Content-Type", contentType);
  // The "latest" tag is reused/overwritten on every release, so this
  // response must never be cached — neither by Cloudflare's edge nor by the
  // app itself — or the update channel could keep serving a stale build.
  headers.set("Cache-Control", "no-store, no-cache, must-revalidate");
  headers.set("Access-Control-Allow-Origin", "*");
  const upstreamLength = upstream.headers.get("content-length");
  if (upstreamLength) headers.set("Content-Length", upstreamLength);
  if (downloadFilename) {
    headers.set("Content-Disposition", `attachment; filename="${downloadFilename}"`);
  }

  return new Response(upstream.body, { status: 200, headers });
}
