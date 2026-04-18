export interface PendingUser {
  id: number
  email: string
  role: string
}

export interface PendingUsersResponse {
  content: PendingUser[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
