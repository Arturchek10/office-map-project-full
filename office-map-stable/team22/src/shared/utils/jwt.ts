interface JwtPayload {
  sub: string
  email?: string
  name?: string
  role?: string
  exp: number
  iat: number
}

export const decodeJwt = (token: string): JwtPayload | null => {
  try {
    const parts = token.split(".")

    if (parts.length !== 3) {
      return null
    }

    const base64Url = parts[1]
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    )

    const result = JSON.parse(jsonPayload)
    return result
  } catch (error) {
    console.error("Error decoding JWT:", error)
    return null
  }
}

export const isTokenExpired = (token: string): boolean => {
  const payload = decodeJwt(token)
  if (!payload) return true

  const currentTime = Math.floor(Date.now() / 1000)
  return payload.exp < currentTime
}

export const getUserInfo = (token: string) => {
  const payload = decodeJwt(token)
  if (!payload) return null

  return {
    id: payload.sub,
    email: payload.email,
    name: payload.name,
    role: payload.role,
  }
}
