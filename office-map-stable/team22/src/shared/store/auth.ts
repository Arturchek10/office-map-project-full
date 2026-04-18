// import { createStore, createEvent, createEffect, sample } from "effector"
// import { useUnit } from "effector-react"
// import { signIn } from "@shared/api/Auth/SignIn"
// import { refreshToken } from "@shared/api/Auth/RefreshToken"
// import { isTokenExpired, getUserInfo } from "@shared/utils/jwt"
// import { UserInfo } from "@shared/types/user.types"


// // полное состояние авторизации.
// // в store хранится:
//   // access token
//   // refresh token
//   // пользователь
//   // авторизован ли пользователь
//   // идёт ли загрузка
//   // есть ли ошибка
// interface AuthState {
//   token: string | null
//   refreshToken: string | null
//   user: UserInfo | null
//   isAuthenticated: boolean
//   isLoading: boolean
//   error: string | null
// }

// const initialState: AuthState = {
//   isAuthenticated: false,
//   isLoading: true,
//   token: null,
//   refreshToken: null,
//   user: null,
//   error: null
// }

// // Events
// // event в Effector - это просто сигнал: "произошло действие"
// // loginRequested - пользователь нажал вход
// // logoutRequested - пользователь вышел
// // setTokensRequested - надо установить токены
// // refreshTokensRequested - обновили токены
// // clearErrorRequested - очистить ошибку

// //  event сам ничего не делает, а только запускает дальнейшую логику.
// export const initAuth = createEvent()
// export const setAuth = createEvent<{
//   token: string,
//   refreshToken: string,
//   user: UserInfo | null
// }>()
// export const logout = createEvent()

// export const loginRequested = createEvent<{ email: string; password: string }>()
// export const logoutRequested = createEvent()
// export const setTokensRequested = createEvent<{
//   token: string
//   refreshToken: string
// }>()
// export const refreshTokensRequested = createEvent<{
//   token: string
//   refreshToken: string
// }>()
// export const refreshTokensOnInitRequested = createEvent<string>()
// export const clearErrorRequested = createEvent()

// // Effects

// export const loginFx = createEffect<
//   { email: string; password: string },
//   { token: string; refreshToken: string; user: UserInfo }
// >(async (credentials) => {
//   try {
//     const response = await signIn(credentials)
//     const userInfo = getUserInfo(response.token)

//     if (!userInfo) {
//       throw new Error("Не удалось декодировать токен")
//     }

//     return {
//       token: response.token,
//       refreshToken: response.refreshToken,
//       user: userInfo,
//     }
//   } catch (error) {
//     console.error("Login effect failed:", error)
//     throw error
//   }
// })

// // Effect для автоматического обновления токенов при инициализации
// export const refreshTokensOnInitFx = createEffect<
//   string,
//   { token: string; refreshToken: string; user: UserInfo }
// >(async (refreshTokenValue) => {
//   try {
//     const response = await refreshToken(refreshTokenValue)
//     const userInfo = getUserInfo(response.token)

//     if (!userInfo) {
//       throw new Error("Не удалось декодировать обновленный токен")
//     }

//     return {
//       token: response.token,
//       refreshToken: response.refreshToken,
//       user: userInfo,
//     }
//   } catch (error) {
//     console.error("Refresh token on init failed:", error)
//     throw error
//   }
// })

// // Store
// // export const $auth = createStore<AuthState>({
// //   token: null,
// //   refreshToken: null,
// //   user: null,
// //   isAuthenticated: false,
// //   isLoading: false,
// //   error: null,
// // })

// export const $auth = createStore<AuthState>(initialState)
//   .on(initAuth, () => {
//     const token = localStorage.getItem("auth-token")
//     const refreshToken = localStorage.getItem("refresh-token")
//     const userRaw = localStorage.getItem("auth-user")

//     return {
//       isAuthenticated: !!token,
//       isLoading: false,
//       token,
//       refreshToken,
//       user: userRaw ? JSON.parse(userRaw) : null,
//       error: null
//     }
//   })
//   .on(setAuth, (_, payload) => ({
//     isAuthenticated: true,
//     isLoading: false,
//     token: payload.token,
//     refreshToken: payload.refreshToken,
//     user: payload.user,
//     error: null
//   }))
//   .on(logout, () => {
//     localStorage.removeItem("auth-token")
//     localStorage.removeItem("auth-refresh-token")
//     localStorage.removeItem("auth-user")
  
//     return {
//       isAuthenticated: false,
//       isLoading: false,
//       token: null,
//       refreshToken: null,
//       user: null,
//       error: null
//     }
//   })

// // Computed stores
// export const $token = $auth.map((state) => state.token)
// export const $user = $auth.map((state) => state.user)
// export const $isAuthenticated = $auth.map((state) => state.isAuthenticated)
// export const $isLoading = $auth.map((state) => state.isLoading)
// export const $error = $auth.map((state) => state.error)

// // Logic

// // Когда вызывается loginRequested, запускается loginFx.
// sample({
//   clock: loginRequested,
//   target: loginFx,
// })
// // Когда надо обновить токен при запуске - запускается refresh effect.
// sample({
//   clock: refreshTokensOnInitRequested,
//   target: refreshTokensOnInitFx,
// })

// // Когда effect в процессе, store обновляется:

// // isLoading: true
// // error: null

// // UI может показать спиннер.
// sample({
//   clock: refreshTokensOnInitFx.pending,
//   source: $auth,
//   fn: (state) => ({
//     ...state,
//     isLoading: true,
//     error: null,
//   }),
//   target: $auth,
// })


// sample({
//   clock: loginFx.pending,
//   source: $auth,
//   fn: (state) => ({
//     ...state,
//     isLoading: true,
//     error: null,
//   }),
//   target: $auth,
// })

// // Если логин успешен:

// // токены кладутся в store
// // пользователь кладётся в store
// // isAuthenticated = true
// sample({
//   clock: loginFx.doneData,
//   fn: (data) => ({
//     token: data.token,
//     refreshToken: data.refreshToken,
//     user: data.user,
//     isAuthenticated: true,
//     isLoading: false,
//     error: null,
//   }),
//   target: $auth,
// })

// sample({
//   clock: refreshTokensOnInitFx.doneData,
//   fn: (data) => ({
//     token: data.token,
//     refreshToken: data.refreshToken,
//     user: data.user,
//     isAuthenticated: true,
//     isLoading: false,
//     error: null,
//   }),
//   target: $auth,
// })

// // Если логин не удался:

// // загрузка выключается
// // ошибка записывается
// sample({
//   clock: loginFx.failData,
//   source: $auth,
//   fn: (state, error) => ({
//     ...state,
//     isLoading: false,
//     error: error.message || "Ошибка входа",
//   }),
//   target: $auth,
// })

// sample({
//   clock: refreshTokensOnInitFx.failData,
//   source: $auth,
//   fn: (state, error) => ({
//     ...state,
//     isLoading: false,
//     error: null, // Не показываем ошибку при неудачном обновлении токенов
//   }),
//   target: $auth,
// })

// // Полная очистка auth state.
// sample({
//   clock: logoutRequested,
//   fn: () => ({
//     token: null,
//     refreshToken: null,
//     user: null,
//     isAuthenticated: false,
//     isLoading: false,
//     error: null,
//   }),
//   target: $auth,
// })

// // Оба события делают почти одно и то же:

// // записывают токены
// // декодируют пользователя из access token
// // включают isAuthenticated

// // То есть это ручная установка состояния авторизации
// sample({
//   clock: setTokensRequested,
//   fn: ({ token, refreshToken }) => {
//     const userInfo = getUserInfo(token)
//     return {
//       token,
//       refreshToken,
//       user: userInfo,
//       isAuthenticated: true,
//       isLoading: false,
//       error: null,
//     }
//   },
//   target: $auth,
// })

// sample({
//   clock: refreshTokensRequested,
//   fn: ({ token, refreshToken }) => {
//     const userInfo = getUserInfo(token)
//     return {
//       token,
//       refreshToken,
//       user: userInfo,
//       isAuthenticated: true,
//       isLoading: false,
//       error: null,
//     }
//   },
//   target: $auth,
// })

// sample({
//   clock: clearErrorRequested,
//   source: $auth,
//   fn: (state) => ({
//     ...state,
//     error: null,
//   }),
//   target: $auth,
// })

// // LocalStorage persistence

// // следит за store.

// // Если пользователь авторизован:
// //   токены и user сохраняются в localStorage

// // Если нет:
// //   всё удаляется

// // То есть после перезагрузки страницы сессия не пропадает.
// $auth.watch((state) => {
//   if (state.token && state.refreshToken && state.user) {
//     localStorage.setItem("auth-token", state.token)
//     localStorage.setItem("auth-refresh-token", state.refreshToken)
//     localStorage.setItem("auth-user", JSON.stringify(state.user))
//   } else {
//     localStorage.removeItem("auth-token")
//     localStorage.removeItem("auth-refresh-token")
//     localStorage.removeItem("auth-user")
//   }
// })

// // Initialize from localStorage

// // логика восстановления сессии
// const initializeFromStorage = () => {
//   const savedToken = localStorage.getItem("auth-token")
//   const savedRefreshToken = localStorage.getItem("auth-refresh-token")
//   const savedUser = localStorage.getItem("auth-user")

//   if (savedToken && savedRefreshToken && savedUser) {
//     // Проверяем, что access token не истек
//     if (!isTokenExpired(savedToken)) {
//       setTokensRequested({ token: savedToken, refreshToken: savedRefreshToken })
//     } else {
//       // Если access token истек, но есть refresh token,
//       // пробуем обновить токены автоматически
//       if (!isTokenExpired(savedRefreshToken)) {
//         refreshTokensOnInitRequested(savedRefreshToken)
//       } else {
//         // Если и refresh token истек, очищаем localStorage
//         localStorage.removeItem("auth-token")
//         localStorage.removeItem("auth-refresh-token")
//         localStorage.removeItem("auth-user")
//       }
//     }
//   }
// }

// // Call initialization
// initializeFromStorage()

// // React hooks for compatibility
// export const useAuthStore = () => {
//   const auth = useUnit($auth)

//   return {
//     ...auth,
//     login: (email: string, password: string) => {
//       loginRequested({ email, password })
//     },
//     logout: () => logoutRequested(),
//     setTokens: (token: string, refreshToken: string) =>
//       setTokensRequested({ token, refreshToken }),
//     refreshTokens: (token: string, refreshToken: string) =>
//       refreshTokensRequested({ token, refreshToken }),
//     refreshTokensOnInit: (refreshToken: string) =>
//       refreshTokensOnInitRequested(refreshToken),
//     clearError: () => clearErrorRequested(),
//   }
// }

// // Хук для проверки валидности токена при инициализации
// export const initializeAuth = () => {
//   const savedToken = localStorage.getItem("auth-token")
//   if (savedToken && isTokenExpired(savedToken)) {
//     localStorage.removeItem("auth-token")
//     localStorage.removeItem("auth-refresh-token")
//     localStorage.removeItem("auth-user")
//   }
// }



// версия 2

// import { createStore, createEvent, createEffect, sample } from "effector"
// import { useUnit } from "effector-react"
// import { signIn } from "@shared/api/Auth/SignIn"
// import { refreshToken } from "@shared/api/Auth/RefreshToken"
// import { isTokenExpired, getUserInfo } from "@shared/utils/jwt"
// import { UserInfo } from "@shared/types/user.types"

// interface AuthState {
//   token: string | null
//   refreshToken: string | null
//   user: UserInfo | null
//   isAuthenticated: boolean
//   isLoading: boolean
//   error: string | null
// }

// const initialState: AuthState = {
//   token: null,
//   refreshToken: null,
//   user: null,
//   isAuthenticated: false,
//   isLoading: true,
//   error: null,
// }

// export const initAuth = createEvent()
// export const loginRequested = createEvent<{ email: string; password: string }>()
// export const logoutRequested = createEvent()
// export const clearErrorRequested = createEvent()

// // эффект авторизации
// export const loginFx = createEffect<
//   { email: string; password: string },
//   { token: string; refreshToken: string; user: UserInfo }
// >(async (credentials) => {
//   const response = await signIn(credentials)
//   const user = getUserInfo(response.token)

//   if (!user) {
//     throw new Error("Не удалось декодировать токен")
//   }

//   return {
//     token: response.token,
//     refreshToken: response.refreshToken,
//     user,
//   }
// })


// export const restoreSessionFx = createEffect<void, AuthState>(async () => {
//   const savedToken = localStorage.getItem("auth-token")
//   const savedRefreshToken = localStorage.getItem("auth-refresh-token")
//   const savedUser = localStorage.getItem("auth-user")

//   if (!savedToken || !savedRefreshToken || !savedUser) {
//     return {
//       ...initialState,
//       isLoading: false,
//     }
//   }

//   const parseUser = (raw: string): UserInfo | null => {
//     try {
//       return JSON.parse(raw) as UserInfo
//     } catch {
//       return null
//     }
//   }

//   const parsedUser = parseUser(savedUser)

//   if (!parsedUser) {
//     localStorage.removeItem("auth-token")
//     localStorage.removeItem("auth-refresh-token")
//     localStorage.removeItem("auth-user")

//     return {
//       ...initialState,
//       isLoading: false,
//     }
//   }

//   if (!isTokenExpired(savedToken)) {
//     return {
//       token: savedToken,
//       refreshToken: savedRefreshToken,
//       user: parsedUser,
//       isAuthenticated: true,
//       isLoading: false,
//       error: null,
//     }
//   }

//   if (!isTokenExpired(savedRefreshToken)) {
//     const response = await refreshToken(savedRefreshToken)
//     const user = getUserInfo(response.token)

//     if (!user) {
//       throw new Error("Не удалось декодировать обновленный токен")
//     }

//     return {
//       token: response.token,
//       refreshToken: response.refreshToken,
//       user,
//       isAuthenticated: true,
//       isLoading: false,
//       error: null,
//     }
//   }

//   localStorage.removeItem("auth-token")
//   localStorage.removeItem("auth-refresh-token")
//   localStorage.removeItem("auth-user")

//   return {
//     ...initialState,
//     isLoading: false,
//   }
// })

// export const $auth = createStore<AuthState>(initialState)
//   .on(loginFx.pending, (state) => ({
//     ...state,
//     isLoading: true,
//     error: null,
//   }))
//   .on(loginFx.doneData, (_, data) => ({
//     token: data.token,
//     refreshToken: data.refreshToken,
//     user: data.user,
//     isAuthenticated: true,
//     isLoading: false,
//     error: null,
//   }))
//   .on(loginFx.failData, (state, error) => ({
//     ...state,
//     isLoading: false,
//     error: error.message || "Ошибка входа",
//   }))
//   .on(restoreSessionFx.pending, (state) => ({
//     ...state,
//     isLoading: true,
//   }))
//   .on(restoreSessionFx.doneData, (_, authState) => authState)
//   .on(restoreSessionFx.failData, () => ({
//     ...initialState,
//     isLoading: false,
//   }))
//   .on(logoutRequested, () => ({
//     ...initialState,
//     isLoading: false,
//   }))
//   .on(clearErrorRequested, (state) => ({
//     ...state,
//     error: null,
//   }))

// sample({
//   clock: loginRequested,
//   target: loginFx,
// })

// sample({
//   clock: initAuth,
//   target: restoreSessionFx,
// })

// $auth.watch((state) => {
//   if (state.token && state.refreshToken && state.user) {
//     localStorage.setItem("auth-token", state.token)
//     localStorage.setItem("auth-refresh-token", state.refreshToken)
//     localStorage.setItem("auth-user", JSON.stringify(state.user))
//   } else {
//     localStorage.removeItem("auth-token")
//     localStorage.removeItem("auth-refresh-token")
//     localStorage.removeItem("auth-user")
//   }
// })

// export const useAuthStore = () => {
//   const auth = useUnit($auth)

//   return {
//     ...auth,
//     login: (email: string, password: string) => {
//       loginRequested({ email, password })
//     },
//     logout: () => logoutRequested(),
//     clearError: () => clearErrorRequested(),
//   }
// }

// версия 3

import { createStore, createEvent, createEffect, sample } from "effector"
import { useUnit } from "effector-react"
import { signIn } from "@shared/api/Auth/SignIn"
import { refreshToken } from "@shared/api/Auth/RefreshToken"
import { isTokenExpired, getUserInfo } from "@shared/utils/jwt"
import { UserInfo } from "@shared/types/user.types"

interface AuthState {
  token: string | null
  refreshToken: string | null
  user: UserInfo | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
}

const initialState: AuthState = {
  token: null,
  refreshToken: null,
  user: null,
  isAuthenticated: false,
  isLoading: true,
  error: null,
}

// EVENTS
export const initAuth = createEvent()
export const loginRequested = createEvent<{ email: string; password: string }>()
export const logoutRequested = createEvent()
export const clearErrorRequested = createEvent()

// ключевой event для регистрации и восстановления
export const sessionRestored = createEvent<{
  token: string
  refreshToken: string
  user: UserInfo
}>()

// EFFECTS
export const loginFx = createEffect<
  { email: string; password: string },
  { token: string; refreshToken: string; user: UserInfo }
>(async (credentials) => {
  const response = await signIn(credentials)
  const user = getUserInfo(response.token)

  if (!user) throw new Error("Не удалось декодировать токен")

  return {
    token: response.token,
    refreshToken: response.refreshToken,
    user,
  }
})

export const restoreSessionFx = createEffect<void, AuthState>(async () => {
  const savedToken = localStorage.getItem("auth-token")
  const savedRefreshToken = localStorage.getItem("auth-refresh-token")
  const savedUser = localStorage.getItem("auth-user")

  if (!savedToken || !savedRefreshToken || !savedUser) {
    return { ...initialState, isLoading: false }
  }

  const parseUser = (raw: string): UserInfo | null => {
    try {
      return JSON.parse(raw) as UserInfo
    } catch {
      return null
    }
  }

  const user = parseUser(savedUser)

  if (!user) {
    localStorage.clear()
    return { ...initialState, isLoading: false }
  }

  // access token валиден
  if (!isTokenExpired(savedToken)) {
    return {
      token: savedToken,
      refreshToken: savedRefreshToken,
      user,
      isAuthenticated: true,
      isLoading: false,
      error: null,
    }
  }

  // refresh token жив
  if (!isTokenExpired(savedRefreshToken)) {
    const response = await refreshToken(savedRefreshToken)
    const newUser = getUserInfo(response.token)

    if (!newUser) throw new Error("Ошибка декодирования токена")

    return {
      token: response.token,
      refreshToken: response.refreshToken,
      user: newUser,
      isAuthenticated: true,
      isLoading: false,
      error: null,
    }
  }

  localStorage.clear()

  return { ...initialState, isLoading: false }
})

// STORE
export const $auth = createStore<AuthState>(initialState)
  .on(loginFx.pending, (state, pending) => ({
    ...state,
    isLoading: pending,
    error: pending ? null : state.error,
  }))
  .on(loginFx.doneData, (_, data) => ({
    token: data.token,
    refreshToken: data.refreshToken,
    user: data.user,
    isAuthenticated: true,
    isLoading: false,
    error: null,
  }))
  .on(loginFx.failData, (state, error) => ({
    ...state,
    isLoading: false,
    error: error.message || "Ошибка входа",
  }))
  .on(restoreSessionFx.pending, (state, pending) => ({
    ...state,
    isLoading: pending,
  }))
  .on(restoreSessionFx.doneData, (_, state) => state)
  .on(restoreSessionFx.failData, () => ({
    ...initialState,
    isLoading: false,
  }))
  .on(sessionRestored, (_, payload) => ({
    token: payload.token,
    refreshToken: payload.refreshToken,
    user: payload.user,
    isAuthenticated: true,
    isLoading: false,
    error: null,
  }))
  .on(logoutRequested, () => ({
    ...initialState,
    isLoading: false,
  }))
  .on(clearErrorRequested, (state) => ({
    ...state,
    error: null,
  }))

// LOGIC
sample({
  clock: loginRequested,
  target: loginFx,
})

sample({
  clock: initAuth,
  target: restoreSessionFx,
})

// LOCAL STORAGE SYNC
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

// HOOK
export const useAuthStore = () => {
  const auth = useUnit($auth)

  return {
    ...auth,
    login: (email: string, password: string) =>
      loginRequested({ email, password }),
    logout: () => logoutRequested(),
    clearError: () => clearErrorRequested(),
  }
}