import { Box } from "@mui/material"
import Office from "@entities/Office/ui/Office"
import type { TOffice } from "@entities/Office/type/office"
import { drawerWidth } from "@features/OfficesBar/config/config"
import { useEffect, useRef, useState } from "react"
import { useNavigate } from "react-router-dom"
import { fetchOfficeByIdFx } from "@shared/api/Offices/GetOfficeById"
import { getFloorByIdFx } from "@shared/store/dataFromFloor"
import { deleteOfficeFx } from "@shared/api/Offices/DeleteOffice"
import PositionedMenuOffice from "./PositionedMenuOffice"
import { Snackbar, Alert } from "@mui/material"
//
interface OfficesBarProps {
  offices: TOffice[]
  open: boolean
  activeOfficeId?: number | null
  onUserNavigation?: () => void
}

function OfficesBar({
  offices,
  open,
  activeOfficeId,
  onUserNavigation,
}: OfficesBarProps) {
  const containerRef = useRef<HTMLDivElement>(null)
  const officeRefs = useRef<{ [key: number]: HTMLDivElement | null }>({})
  const navigate = useNavigate()

  // Состояние для контекстного меню
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null)
  const [selectedOfficeId, setSelectedOfficeId] = useState<number | null>(null)
  const [snackbar, setSnackbar] = useState<{
    open: boolean
    message: string
    severity: "success" | "error"
  }>({ open: false, message: "", severity: "success" })

  const handleClick = (id: number) => {
    // Устанавливаем флаг, что переход инициирован пользователем
    if (onUserNavigation) {
      onUserNavigation()
    }

    fetchOfficeByIdFx(id)
    fetchOfficeByIdFx.done.watch(({ result }) => {
      if (result.startFloor === null) {
        navigate(`/office/${id}/createfloor`)
      } else {
        navigate(`/office/${id}/floor/${result.startFloor.id}`)
        getFloorByIdFx(result.startFloor.id)
      }
    })
  }

  // Обработчик правой кнопки мыши
  const handleContextMenu = (e: React.MouseEvent, id: number) => {
    e.preventDefault()
    setMenuPos({ x: e.clientX, y: e.clientY })
    setSelectedOfficeId(id)
  }

  // Закрытие контекстного меню
  const handleCloseMenu = () => {
    setMenuPos(null)
    setSelectedOfficeId(null)
  }

  // Удаление офиса
  const handleDelete = async () => {
    if (!selectedOfficeId) return
    try {
      await deleteOfficeFx(selectedOfficeId)
      // Store обновится автоматически через deleteOfficeFx.doneData

      // Если удаляется активный офис, перенаправляем на главную страницу
      if (selectedOfficeId === activeOfficeId) {
        navigate("/")
      }

      setSnackbar({
        open: true,
        message: "Офис удален",
        severity: "success",
      })
    } catch (e) {
      console.error("Ошибка при удалении офиса:", e)
      setSnackbar({
        open: true,
        message: "Ошибка при удалении",
        severity: "error",
      })
    } finally {
      handleCloseMenu()
    }
  }

  useEffect(() => {
    if (activeOfficeId != null) {
      const officeEl = officeRefs.current[activeOfficeId]
      if (officeEl) {
        officeEl.scrollIntoView({
          behavior: "smooth",
          block: "center",
        })
      }
    }
  }, [activeOfficeId])

  return (
    <>
      <Box
        ref={containerRef}
        sx={{
          position: "fixed",
          top: "60px",
          pt: "10px",
          pl: "10px",
          left: open ? drawerWidth : -(250 - drawerWidth),
          width: 250,
          maxHeight: "calc(100vh - 60px)",
          bgcolor: "white",
          boxShadow: 3,
          overflowY: "auto",
          transition: "left 0.3s ease",
          zIndex: 1100,
          pr: "10px",
        }}
      >
        {offices.map((office) => {
          const isActive = String(office.id) === String(activeOfficeId)
          return (
            <Box
              key={office.id}
              ref={(el: HTMLDivElement | null) => {
                officeRefs.current[office.id] = el
              }}
              sx={{
                borderRadius: "4px",
                mb: 1,
                cursor: "pointer",
                transition: "background-color 0.2s ease",
              }}
              onClick={() => handleClick(office.id)}
              onContextMenu={(e) => handleContextMenu(e, office.id)}
            >
              <Office {...office} active={isActive} />
            </Box>
          )
        })}
      </Box>

      {/* Контекстное меню */}
      <PositionedMenuOffice
        menuPos={menuPos}
        open={Boolean(menuPos)}
        onClose={handleCloseMenu}
        onDelete={handleDelete}
      />

      {/* Snackbar для уведомлений */}
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

export default OfficesBar
