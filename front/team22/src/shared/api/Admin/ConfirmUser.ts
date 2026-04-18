import { apiPatch } from "@shared/utils/api"

export const confirmUser = async (userId: number): Promise<void> => {
  try {
    const response = await apiPatch(`/api/v1/admin/users/confirm/${userId}`)

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(
        `HTTP error! status: ${response.status}, body: ${errorText}`
      )
    }
  } catch (error) {
    console.error("Confirm user error:", error)
    throw error
  }
}
