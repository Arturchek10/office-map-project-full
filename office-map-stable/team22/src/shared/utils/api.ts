import {
  $auth,
  logoutRequested,
  refreshTokensRequested,
} from "@shared/store/auth"
import { refreshToken } from "@shared/api/Auth/RefreshToken"

let isRefreshing = false
let failedQueue: Array<{
  resolve: (value: Response) => void
  reject: (error: Error) => void
}> = []

const processQueue = (error: Error | null, token: string | null = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error)
    } else {
      resolve(new Response())
    }
  })
  failedQueue = []
}

export const createApiRequest = async (
  url: string,
  options: RequestInit = {}
): Promise<Response> => {
  const authState = $auth.getState()
  const token = authState.token

  const headers: Record<string, string> = {
    ...(options.headers as Record<string, string>),
  }

  if (!(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json"
  }

  if (token) {
    headers["Authorization"] = `Bearer ${token}`
  }

  const fetchWithCheck = async (): Promise<Response> => {
    const res = await fetch(url, { ...options, headers })
    if (!res.ok) {
      const text = await res.text().catch(() => "")
      throw new Error(`HTTP ${res.status}: ${text}`)
    }
    return res
  }

  try {
    return await fetchWithCheck()
  } catch (err: any) {
    // Если 401 и есть refreshToken
    if (err.message.includes("HTTP 401") && authState.refreshToken) {
      const errorData = await fetch(url, { ...options, headers }).then((r) =>
        r.json().catch(() => ({}))
      )

      if (errorData.message === "Token expired") {
        if (isRefreshing) {
          return new Promise((resolve, reject) => {
            failedQueue.push({ resolve, reject })
          })
        }

        isRefreshing = true
        try {
          const newTokens = await refreshToken(authState.refreshToken)
          refreshTokensRequested({
            token: newTokens.token,
            refreshToken: newTokens.refreshToken,
          })

          headers["Authorization"] = `Bearer ${newTokens.token}`
          if (!(options.body instanceof FormData)) {
            headers["Content-Type"] = "application/json"
          }

          const retryResponse = await fetchWithCheck()
          processQueue(null, newTokens.token)
          isRefreshing = false
          return retryResponse
        } catch (refreshError) {
          processQueue(refreshError as Error)
          isRefreshing = false
          logoutRequested()
          throw new Error("Token refresh failed")
        }
      }
    }

    throw err
  }
}

// --- вспомогательные функции ---
export const apiGet = (url: string, options?: RequestInit) =>
  createApiRequest(url, { ...options, method: "GET" })

export const apiPost = (url: string, data?: any, options?: RequestInit) =>
  createApiRequest(url, {
    ...options,
    method: "POST",
    body: data ? JSON.stringify(data) : undefined,
  })

export const apiPut = (url: string, data?: any, options?: RequestInit) =>
  createApiRequest(url, {
    ...options,
    method: "PUT",
    body: data ? JSON.stringify(data) : undefined,
  })

export const apiPatch = (url: string, data?: any, options?: RequestInit) =>
  createApiRequest(url, {
    ...options,
    method: "PATCH",
    body: data ? JSON.stringify(data) : undefined,
  })

export const apiPatchFormData = (
  url: string,
  formData: FormData,
  options?: RequestInit
) =>
  createApiRequest(url, {
    ...options,
    method: "PATCH",
    body: formData,
    headers: { ...options?.headers },
  })

export const apiDelete = (url: string, options?: RequestInit) =>
  createApiRequest(url, { ...options, method: "DELETE" })

export const apiPostFormData = (
  url: string,
  formData: FormData,
  options?: RequestInit
) =>
  createApiRequest(url, {
    ...options,
    method: "POST",
    body: formData,
    headers: { ...options?.headers },
  })
