
export const fetchJsonSafe = async (
  response: Response
): Promise<any | null> => {
  const contentType = response.headers.get("content-type")

  // Проверяем, что content-type содержит application/json
  if (!contentType || !contentType.includes("application/json")) {
    return null
  }

  const text = await response.text()
  if (!text || text.trim() === "") {
    return null
  }

  try {
    return JSON.parse(text)
  } catch (error) {
    console.warn("Failed to parse JSON response:", error)
    return null
  }
}
