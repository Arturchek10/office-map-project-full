import { createStore, createEvent, createEffect, sample } from "effector"
import { useUnit } from "effector-react"
import { signIn } from "@shared/api/Auth/SignIn"
import { refreshToken } from "@shared/api/Auth/RefreshToken"
import { decodeJwt, isTokenExpired, getUserInfo } from "@shared/utils/jwt"

interface User {
  id: string
  email?: string
  name?: string
  role?: string
}

interface AuthState {
  token: string | null
  refreshToken: string | null
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
}

// Events
export const loginRequested = createEvent<{ login: string; password: string }>()
export const logoutRequested = createEvent()
export const setTokensRequested = createEvent<{
  token: string
  refreshToken: string
}>()
export const refreshTokensRequested = createEvent<{
  token: string
  refreshToken: string
}>()
export const refreshTokensOnInitRequested = createEvent<string>()
export const clearErrorRequested = createEvent()

// Effects
export const loginFx = createEffect<
  { login: string; password: string },
  { token: string; refreshToken: string; user: User }
>(async (credentials) => {
  try {
    const response = await signIn(credentials)
    const userInfo = getUserInfo(response.token)

    if (!userInfo) {
      throw new Error("Не удалось декодировать токен")
    }

    return {
      token: response.token,
      refreshToken: response.refreshToken,
      user: userInfo,
    }
  } catch (error) {
    console.error("Login effect failed:", error)
    throw error
  }
})

// Effect для автоматического обновления токенов при инициализации
export const refreshTokensOnInitFx = createEffect<
  string,
  { token: string; refreshToken: string; user: User }
>(async (refreshTokenValue) => {
  try {
    const response = await refreshToken(refreshTokenValue)
    const userInfo = getUserInfo(response.token)

    if (!userInfo) {
      throw new Error("Не удалось декодировать обновленный токен")
    }

    return {
      token: response.token,
      refreshToken: response.refreshToken,
      user: userInfo,
    }
  } catch (error) {
    console.error("Refresh token on init failed:", error)
    throw error
  }
})

// Store
export const $auth = createStore<AuthState>({
  token: null,
  refreshToken: null,
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
})

// Computed stores
export const $token = $auth.map((state) => state.token)
export const $user = $auth.map((state) => state.user)
export const $isAuthenticated = $auth.map((state) => state.isAuthenticated)
export const $isLoading = $auth.map((state) => state.isLoading)
export const $error = $auth.map((state) => state.error)

// Logic
sample({
  clock: loginRequested,
  target: loginFx,
})

sample({
  clock: refreshTokensOnInitRequested,
  target: refreshTokensOnInitFx,
})

sample({
  clock: refreshTokensOnInitFx.pending,
  source: $auth,
  fn: (state) => ({
    ...state,
    isLoading: true,
    error: null,
  }),
  target: $auth,
})

sample({
  clock: loginFx.pending,
  source: $auth,
  fn: (state) => ({
    ...state,
    isLoading: true,
    error: null,
  }),
  target: $auth,
})

sample({
  clock: loginFx.doneData,
  fn: (data) => ({
    token: data.token,
    refreshToken: data.refreshToken,
    user: data.user,
    isAuthenticated: true,
    isLoading: false,
    error: null,
  }),
  target: $auth,
})

sample({
  clock: refreshTokensOnInitFx.doneData,
  fn: (data) => ({
    token: data.token,
    refreshToken: data.refreshToken,
    user: data.user,
    isAuthenticated: true,
    isLoading: false,
    error: null,
  }),
  target: $auth,
})

sample({
  clock: loginFx.failData,
  source: $auth,
  fn: (state, error) => ({
    ...state,
    isLoading: false,
    error: error.message || "Ошибка входа",
  }),
  target: $auth,
})

sample({
  clock: refreshTokensOnInitFx.failData,
  source: $auth,
  fn: (state, error) => ({
    ...state,
    isLoading: false,
    error: null, // Не показываем ошибку при неудачном обновлении токенов
  }),
  target: $auth,
})

sample({
  clock: logoutRequested,
  fn: () => ({
    token: null,
    refreshToken: null,
    user: null,
    isAuthenticated: false,
    isLoading: false,
    error: null,
  }),
  target: $auth,
})

sample({
  clock: setTokensRequested,
  fn: ({ token, refreshToken }) => {
    const userInfo = getUserInfo(token)
    return {
      token,
      refreshToken,
      user: userInfo,
      isAuthenticated: true,
      isLoading: false,
      error: null,
    }
  },
  target: $auth,
})

sample({
  clock: refreshTokensRequested,
  fn: ({ token, refreshToken }) => {
    const userInfo = getUserInfo(token)
    return {
      token,
      refreshToken,
      user: userInfo,
      isAuthenticated: true,
      isLoading: false,
      error: null,
    }
  },
  target: $auth,
})

sample({
  clock: clearErrorRequested,
  source: $auth,
  fn: (state) => ({
    ...state,
    error: null,
  }),
  target: $auth,
})

// LocalStorage persistence
$auth.watch((state) => {
  if (state.token && state.refreshToken && state.user) {
    localStorage.setItem("auth-token", state.token)
    localStorage.setItem("auth-refresh-token", state.refreshToken)
    localStorage.setItem("auth-user", JSON.stringify(state.user))
  } else {
    localStorage.removeItem("auth-token")
    localStorage.removeItem("auth-refresh-token")
    localStorage.removeItem("auth-user")
  }
})

// Initialize from localStorage
const initializeFromStorage = () => {
  const savedToken = localStorage.getItem("auth-token")
  const savedRefreshToken = localStorage.getItem("auth-refresh-token")
  const savedUser = localStorage.getItem("auth-user")

  if (savedToken && savedRefreshToken && savedUser) {
    // Проверяем, что access token не истек
    if (!isTokenExpired(savedToken)) {
      const user = JSON.parse(savedUser)
      setTokensRequested({ token: savedToken, refreshToken: savedRefreshToken })
    } else {
      // Если access token истек, но есть refresh token,
      // пробуем обновить токены автоматически
      if (!isTokenExpired(savedRefreshToken)) {
        refreshTokensOnInitRequested(savedRefreshToken)
      } else {
        // Если и refresh token истек, очищаем localStorage
        localStorage.removeItem("auth-token")
        localStorage.removeItem("auth-refresh-token")
        localStorage.removeItem("auth-user")
      }
    }
  }
}

// Call initialization
initializeFromStorage()

// React hooks for compatibility
export const useAuthStore = () => {
  const auth = useUnit($auth)

  return {
    ...auth,
    login: (login: string, password: string) => {
      loginRequested({ login, password })
    },
    logout: () => logoutRequested(),
    setTokens: (token: string, refreshToken: string) =>
      setTokensRequested({ token, refreshToken }),
    refreshTokens: (token: string, refreshToken: string) =>
      refreshTokensRequested({ token, refreshToken }),
    refreshTokensOnInit: (refreshToken: string) =>
      refreshTokensOnInitRequested(refreshToken),
    clearError: () => clearErrorRequested(),
  }
}

// Хук для проверки валидности токена при инициализации
export const initializeAuth = () => {
  const savedToken = localStorage.getItem("auth-token")
  if (savedToken && isTokenExpired(savedToken)) {
    localStorage.removeItem("auth-token")
    localStorage.removeItem("auth-refresh-token")
    localStorage.removeItem("auth-user")
  }
}
