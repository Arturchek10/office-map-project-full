import { apiDelete } from "@shared/utils/api"

export const rejectUser = async (userId: number): Promise<void> => {
  try {
    const response = await apiDelete(`/api/v1/admin/users/decline/${userId}`)

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(
        `HTTP error! status: ${response.status}, body: ${errorText}`
      )
    }
  } catch (error) {
    console.error("Reject user error:", error)
    throw error
  }
}
