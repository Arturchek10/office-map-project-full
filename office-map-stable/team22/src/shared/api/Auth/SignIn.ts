interface SignInRequest {
  login: string
  password: string
}

interface SignInResponse {
  token: string
  refreshToken: string
}

export const signIn = async (
  credentials: SignInRequest
): Promise<SignInResponse> => {
  // Для тестирования - мок ответ
  if (credentials.login === "test" && credentials.password === "test") {
    const mockResponse: SignInResponse = {
      token:
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
      refreshToken: "mock-refresh-token",
    }
    return mockResponse
  }

  try {
    const response = await fetch("/api/v1/auth/sign-in", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(credentials),
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(
        `HTTP error! status: ${response.status}, body: ${errorText}`
      )
    }

    const data: SignInResponse = await response.json()
    return data
  } catch (error) {
    console.error("Sign in error:", error)
    throw error
  }
}
