import { apiGet } from "@shared/utils/api"
import { PendingUsersResponse } from "@shared/types/admin"

export const getPendingUsers = async (
  page: number = 0,
  size: number = 20
): Promise<PendingUsersResponse> => {
  try {
    const response = await apiGet(
      `/api/v1/admin/users/pending?page=${page}&size=${size}`
    )

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(
        `HTTP error! status: ${response.status}, body: ${errorText}`
      )
    }

    const data: PendingUsersResponse = await response.json()
    return data
  } catch (error) {
    console.error("Get pending users error:", error)
    throw error
  }
}
