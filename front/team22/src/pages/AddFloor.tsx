import Header from "@entities/Header/Header"
import {
  CssBaseline,
  Box,
  CircularProgress,
  Paper,
  Typography,
  TextField,
  Button,
  Stack,
  Snackbar,
  Alert,
} from "@mui/material"
import NavBar from "@entities/NavBar/NavBar"
import OfficesBar from "@features/OfficesBar/OfficesBar"
import { useState } from "react"
import { useUnit } from "effector-react"
import {
  $offices,
  $officesError,
  $officesLoading,
} from "@shared/api/Offices/GetOfficesList"
import { $activeOffice } from "@shared/api/Offices/GetOfficeById"
import { addFloorFx } from "@shared/api/Floors/AddFloor"
import { useNavigate } from "react-router-dom"
import { $activeFloor } from "@shared/api/Floors/AddFloor"
function AddFloor() {
  const [isOfficesBarOpen, setIsOfficesBarOpen] = useState(false)
  const toggleOfficesBar = () => {
    setIsOfficesBarOpen((prev) => !prev)
  }

  const [offices, loading, error] = useUnit([
    $offices,
    $officesLoading,
    $officesError,
  ])
  const navigator = useNavigate()
  const [name, setName] = useState("")
  const [orderNumber, setOrderNumber] = useState("")

  const activeOffice = useUnit($activeOffice)
  const activeFloor = useUnit($activeFloor)
  const [snackbar, setSnackbar] = useState<{
    open: boolean
    message: string
    severity: "success" | "error"
  }>({ open: false, message: "", severity: "success" })

  const handleSubmit = async () => {
    if (!name || !orderNumber) return

    try {
      const newFloor = {
        name,
        orderNumber: Number(orderNumber),
      }
      await addFloorFx({
        officeId: Number(activeOffice?.id),
        floor: newFloor,
      })
      setSnackbar({
        open: true,
        message: "✅ Этаж успешно создан!",
        severity: "success",
      })

      setName("")
      setOrderNumber("")
    } catch (e) {
      setSnackbar({
        open: true,
        message: (e as Error).message || "❌ Ошибка при создании этажа",
        severity: "error",
      })
    }
  }

  if (error) {
    return (
      <Box
        sx={{
          height: "100vh",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          p: 2,
        }}
      >
        <Typography variant="h3" align="center" color="error">
          Ошибка загрузки офисов: {error?.message || "Неизвестная ошибка"}
        </Typography>
      </Box>
    )
  }

  if (loading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          height: "100vh",
        }}
      >
        <CircularProgress size={80} />
      </Box>
    )
  }

  return (
    <>
      <Header officeName={activeOffice?.name} />
      <Box sx={{ display: "flex", height: "100vh" }}>
        <CssBaseline />
        <NavBar onToggleOffices={toggleOfficesBar} />
        <OfficesBar offices={offices} open={isOfficesBarOpen} />

        <Box
          sx={{
            flexGrow: 1,
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            bgcolor: "#f5f5f5",
          }}
        >
          <Button
            variant="contained"
            sx={{ position: "absolute", top: 80, right: 30 }}
            onClick={()=> navigator(`/office/${activeOffice?.id}/floor/${activeFloor?.id}`)}
          >
            Перейти к редактору
          </Button>
          <Paper
            elevation={3}
            sx={{ p: 4, borderRadius: 3, minWidth: 400, textAlign: "center" }}
          >
            <Typography variant="h6" gutterBottom>
              Создать этаж
            </Typography>
            <Stack spacing={2}>
              <TextField
                label="Название этажа"
                value={name}
                onChange={(e) => setName(e.target.value)}
                fullWidth
              />
              <TextField
                label="Номер этажа"
                type="number"
                value={orderNumber}
                onChange={(e) => setOrderNumber(e.target.value)}
                fullWidth
              />
              {activeOffice?.startFloor === null && (
                <Typography
                  variant="caption"
                  color="text.secondary"
                  sx={{ display: "block" }}
                >
                  Перед работой с редактором создайте этаж
                </Typography>
              )}

              <Button
                variant="contained"
                color="primary"
                onClick={handleSubmit}
                disabled={addFloorFx.pending.getState()}
              >
                {addFloorFx.pending.getState() ? "Создание..." : "Создать этаж"}
              </Button>
            </Stack>
          </Paper>
        </Box>
      </Box>

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      >
        <Alert
          severity={snackbar.severity}
          onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>
    </>
  )
}

export default AddFloor
