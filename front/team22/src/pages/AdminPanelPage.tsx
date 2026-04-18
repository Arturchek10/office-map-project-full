import React, { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import {
  Box,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  CircularProgress,
  Alert,
  Snackbar,
} from "@mui/material"
import CheckCircleIcon from "@mui/icons-material/CheckCircle"
import CancelIcon from "@mui/icons-material/Cancel"
import T1Logo from "@entities/Header/assets/T1 logo blue.svg?react"
import { getPendingUsers, confirmUser, rejectUser } from "@shared/api/Admin"
import { PendingUser } from "@shared/types/admin"

const AdminPanelPage: React.FC = () => {
  const navigate = useNavigate()
  const [users, setUsers] = useState<PendingUser[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [confirmingUsers, setConfirmingUsers] = useState<Set<number>>(new Set())
  const [rejectingUsers, setRejectingUsers] = useState<Set<number>>(new Set())
  const [snackbar, setSnackbar] = useState<{
    open: boolean
    message: string
    severity: "success" | "error" | "info" | "warning"
  }>({
    open: false,
    message: "",
    severity: "info",
  })

  useEffect(() => {
    fetchPendingUsers()
  }, [])

  const fetchPendingUsers = async () => {
    try {
      setLoading(true)
      setError(null)
      const response = await getPendingUsers(0, 20)
      setUsers(response.content)
    } catch (err) {
      setError("Ошибка при загрузке пользователей")
      console.error("Failed to fetch pending users:", err)
    } finally {
      setLoading(false)
    }
  }

  const handleConfirmUser = async (userId: number) => {
    try {
      setConfirmingUsers((prev) => new Set(prev).add(userId))
      await confirmUser(userId)
      // Удаляем пользователя из списка после успешного подтверждения
      setUsers((prev) => prev.filter((user) => user.id !== userId))

      setSnackbar({
        open: true,
        message: "✅ Пользователь успешно подтвержден!",
        severity: "success",
      })
    } catch (err) {
      setError("Ошибка при подтверждении пользователя")
      console.error("Failed to confirm user:", err)

      setSnackbar({
        open: true,
        message: "❌ Ошибка при подтверждении пользователя",
        severity: "error",
      })
    } finally {
      setConfirmingUsers((prev) => {
        const newSet = new Set(prev)
        newSet.delete(userId)
        return newSet
      })
    }
  }

  const handleRejectUser = async (userId: number) => {
    try {
      setRejectingUsers((prev) => new Set(prev).add(userId))
      await rejectUser(userId)
      // Удаляем пользователя из списка после успешного отклонения
      setUsers((prev) => prev.filter((user) => user.id !== userId))

      setSnackbar({
        open: true,
        message: "✅ Пользователь успешно отклонен!",
        severity: "success",
      })
    } catch (err) {
      setError("Ошибка при отклонении пользователя")
      console.error("Failed to reject user:", err)

      setSnackbar({
        open: true,
        message: "❌ Ошибка при отклонении пользователя",
        severity: "error",
      })
    } finally {
      setRejectingUsers((prev) => {
        const newSet = new Set(prev)
        newSet.delete(userId)
        return newSet
      })
    }
  }

  const handleGoToHome = () => {
    navigate("/")
  }

  return (
    <Box
      sx={{
        minHeight: "100vh",
        bgcolor: "#f5f5f5",
        overflowY: "auto", // ← даём прокрутку странице
        position: "relative",
      }}
    >
      {/* Header с логотипом */}
      <Box
        sx={{
          position: "absolute",
          top: "40px",
          right: "40px",
          display: "flex",
          alignItems: "center",
          gap: "10px",
        }}
      >
        <T1Logo style={{ width: 100, height: 50, color: "#2F80ED" }} />
        <Typography
          variant="h4"
          sx={{
            color: "#2F80ED",
            fontWeight: 600,
          }}
        >
          Office Map
        </Typography>
      </Box>

      {/* Основной контент */}
      <Box
        sx={{
          maxWidth: "1200px",
          margin: "0 auto 30px",
          padding: {
            xs: "120px 15px 40px 15px",
            md: "120px 20px 40px 20px",
          },
          width: "100%",
          boxSizing: "border-box", // важно!
        }}
      >
        {/* Заголовок */}
        <Typography
          variant="h3"
          component="h1"
          sx={{
            textAlign: "center",
            mb: 3,
            fontWeight: 600,
            color: "#333",
          }}
        >
          Админ панель
        </Typography>

        {/* Кнопка "На главный экран" */}
        <Box sx={{ textAlign: "center", mb: 4 }}>
          <Button
            variant="outlined"
            onClick={handleGoToHome}
            sx={{
              px: 3,
              py: 1.5,
              fontSize: "16px",
              borderColor: "#2F80ED",
              color: "#2F80ED",
              "&:hover": {
                borderColor: "#1a5bb8",
                backgroundColor: "rgba(47, 128, 237, 0.04)",
              },
            }}
          >
            На главный экран
          </Button>
        </Box>

        {/* Таблица пользователей */}
        <Paper
          elevation={2}
          sx={{
            borderRadius: 2,
            overflow: "hidden",
            pb: 3,
          }}
        >
          {error && (
            <Alert severity="error" sx={{ m: 2 }}>
              {error}
            </Alert>
          )}

          {loading ? (
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                py: 8,
              }}
            >
              <CircularProgress />
            </Box>
          ) : (
            <TableContainer>
              <Table>
                <TableHead>
                  <TableRow sx={{ bgcolor: "#f8f9fa" }}>
                    <TableCell
                      sx={{
                        fontWeight: 600,
                        fontSize: "16px",
                        color: "#333",
                      }}
                    >
                      ID
                    </TableCell>
                    <TableCell
                      sx={{
                        fontWeight: 600,
                        fontSize: "16px",
                        color: "#333",
                      }}
                    >
                      Логин
                    </TableCell>
                    <TableCell
                      sx={{
                        fontWeight: 600,
                        fontSize: "16px",
                        color: "#333",
                      }}
                    >
                      Роль
                    </TableCell>
                    <TableCell
                      sx={{
                        fontWeight: 600,
                        fontSize: "16px",
                        color: "#333",
                      }}
                    >
                      Действие
                    </TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {users.length === 0 ? (
                    <TableRow>
                      <TableCell
                        colSpan={4}
                        sx={{ textAlign: "center", py: 4 }}
                      >
                        <Typography variant="body1" color="text.secondary">
                          Нет неподтвержденных пользователей
                        </Typography>
                      </TableCell>
                    </TableRow>
                  ) : (
                    users.map((user) => (
                      <TableRow key={user.id} hover>
                        <TableCell sx={{ fontSize: "14px" }}>
                          {user.id}
                        </TableCell>
                        <TableCell sx={{ fontSize: "14px" }}>
                          {user.email}
                        </TableCell>
                        <TableCell sx={{ fontSize: "14px" }}>
                          {user.role}
                        </TableCell>
                        <TableCell>
                          <Box sx={{ display: "flex", gap: 1 }}>
                            <Button
                              variant="contained"
                              startIcon={<CheckCircleIcon />}
                              onClick={() => handleConfirmUser(user.id)}
                              disabled={
                                confirmingUsers.has(user.id) ||
                                rejectingUsers.has(user.id)
                              }
                              sx={{
                                bgcolor: "#4caf50",
                                color: "white",
                                "&:hover": {
                                  bgcolor: "#45a049",
                                },
                                "&:disabled": {
                                  bgcolor: "#ccc",
                                },
                              }}
                            >
                              {confirmingUsers.has(user.id) ? (
                                <CircularProgress size={16} color="inherit" />
                              ) : (
                                "Подтвердить"
                              )}
                            </Button>
                            <Button
                              variant="contained"
                              startIcon={<CancelIcon />}
                              onClick={() => handleRejectUser(user.id)}
                              disabled={
                                rejectingUsers.has(user.id) ||
                                confirmingUsers.has(user.id)
                              }
                              sx={{
                                bgcolor: "#f44336",
                                color: "white",
                                "&:hover": {
                                  bgcolor: "#d32f2f",
                                },
                                "&:disabled": {
                                  bgcolor: "#ccc",
                                },
                              }}
                            >
                              {rejectingUsers.has(user.id) ? (
                                <CircularProgress size={16} color="inherit" />
                              ) : (
                                "Отклонить"
                              )}
                            </Button>
                          </Box>
                        </TableCell>
                      </TableRow>
                    ))
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          )}
        </Paper>
      </Box>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          onClose={() => setSnackbar((prev) => ({ ...prev, open: false }))}
          severity={snackbar.severity}
          sx={{ width: "100%" }}
          variant="filled"
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </Box>
  )
}

export default AdminPanelPage
