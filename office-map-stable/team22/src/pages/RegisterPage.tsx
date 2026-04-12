import {
  CssBaseline,
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Stack,
  Alert,
} from "@mui/material"
import { useNavigate } from "react-router-dom"
import { useState } from "react"
import T1logo from "@entities/Header/assets/T1 logo blue.svg?react"
import { register } from "@shared/api/Auth/Register"

function RegisterPage() {
  const navigate = useNavigate()
  const [formData, setFormData] = useState({
    login: "",
    password: "",
  })
  const [error, setError] = useState<string>("")
  const [success, setSuccess] = useState<string>("")
  const [isLoading, setIsLoading] = useState(false)

  const handleInputChange =
    (field: string) => (event: React.ChangeEvent<HTMLInputElement>) => {
      setFormData({
        ...formData,
        [field]: event.target.value,
      })
      // Очищаем ошибки при изменении полей
      if (error) setError("")
      if (success) setSuccess("")
    }

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault()

    if (!formData.login.trim() || !formData.password.trim()) {
      setError("Пожалуйста, заполните все поля")
      return
    }

    setIsLoading(true)
    setError("")
    setSuccess("")

    try {
      const response = await register(formData)
      setSuccess("Успешно. Ожидайте подтверждение")
      // Очищаем форму после успешной регистрации
      setFormData({ login: "", password: "" })
    } catch (err) {
      setError(err instanceof Error ? err.message : "Ошибка при регистрации")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <>
      <Box sx={{ display: "flex", height: "100vh" }}>
        <CssBaseline />
        <Box
          sx={{
            position: "absolute",
            top: 40,
            right: 40,
            display: "flex",
            alignItems: "center",
            gap: 1,
          }}
        >
          <T1logo style={{ width: 100, height: 50, color: "#2F80ED" }} />
          <Typography
            variant="h4"
            sx={{ fontWeight: "bold", color: "#2F80ED" }}
          >
            Office Map
          </Typography>
        </Box>

        <Box
          sx={{
            flexGrow: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            bgcolor: "#f5f5f5",
          }}
        >
          <Paper
            elevation={3}
            sx={{ p: 4, borderRadius: 3, minWidth: 400, textAlign: "center" }}
          >
            <Typography variant="h4" gutterBottom>
              Регистрация
            </Typography>

            {error && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {error}
              </Alert>
            )}

            {success && (
              <Alert severity="success" sx={{ mb: 2 }}>
                {success}
              </Alert>
            )}

            <form onSubmit={handleSubmit}>
              <Stack spacing={2}>
                <TextField
                  label="Логин"
                  fullWidth
                  value={formData.login}
                  onChange={handleInputChange("login")}
                  disabled={isLoading}
                />
                <TextField
                  label="Пароль"
                  type="password"
                  fullWidth
                  value={formData.password}
                  onChange={handleInputChange("password")}
                  disabled={isLoading}
                />

                <Button
                  variant="contained"
                  color="primary"
                  type="submit"
                  disabled={isLoading}
                  fullWidth
                >
                  {isLoading ? "Регистрация..." : "Зарегистрироваться"}
                </Button>

                <Button
                  variant="text"
                  onClick={() => navigate("/auth")}
                  disabled={isLoading}
                >
                  Уже есть аккаунт? Войти
                </Button>
              </Stack>
            </form>
          </Paper>
        </Box>
      </Box>
    </>
  )
}

export default RegisterPage
