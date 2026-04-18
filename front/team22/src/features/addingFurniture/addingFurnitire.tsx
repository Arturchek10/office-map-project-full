import { Button, Paper, Snackbar, Alert, Box, Typography } from "@mui/material"
import Furniture from "./Furniture/Furniture"
import { MarkerTypes } from "@shared/types/marker"
import { useUnit } from "effector-react"
import { useState } from "react"
import {
  addFurnitureFx,
  FurnitureData,
} from "@shared/api/Furniture/AddFurniture"
import { updateFurnitureUIFx } from "@shared/api/Furniture/UpdateFurnitureUI"
import { updateFurniturePositionFx } from "@shared/api/Furniture/UpdateFurniturePosition"
import {
  $furnitureCatalog,
  setFurnitureCatalog,
} from "@shared/api/Furniture/GetFurnitureCatalog"
import PositionedMenuFurniture from "./PositionedMenuFurniture"
import { deleteFurnitureFx } from "@shared/api/Furniture/DeleteFurniture"
import CreateFurnitureForm from "./CreateFurnitureForm"

interface AddingFurnitureProps {
  addFurniture: (item: { name: string; photoUrl: string }) => void
  onSelectLayer: (layers: MarkerTypes[]) => void
  open: boolean
  setOpen: (open: boolean) => void
  setEditable: (editable: boolean) => void
  furnitureOnMap: {
    id: number
    name: string
    photo: string
    position: {
      position_x: number
      position_y: number
    }
    width: number
    height: number
    angle: number
  }[]
  serverFurnitureIds: Set<number>
  currentFloorId?: number
}

function AddingFurniture({
  addFurniture,
  onSelectLayer,
  open,
  setOpen,
  setEditable,
  furnitureOnMap,
  serverFurnitureIds,
  currentFloorId,
}: AddingFurnitureProps) {
  const furnitureCatalog = useUnit($furnitureCatalog)

  const [snackbar, setSnackbar] = useState<{
    open: boolean
    message: string
    severity: "success" | "error"
  }>({ open: false, message: "", severity: "success" })

  const [loading, setLoading] = useState(false)
  const [createFormOpen, setCreateFormOpen] = useState(false)

  // 🔹 для контекстного меню
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null)
  const [selectedId, setSelectedId] = useState<number | null>(null)

  const handleContextMenu = (e: React.MouseEvent, id: number) => {
    e.preventDefault()
    setMenuPos({ x: e.clientX, y: e.clientY })
    setSelectedId(id)
  }

  const handleCloseMenu = () => {
    setMenuPos(null)
    setSelectedId(null)
  }

  const handleDelete = async () => {
    if (!selectedId) return
    try {
      // Проверяем, является ли мебель серверной
      if (serverFurnitureIds.has(selectedId)) {
        // Удаляем с сервера
        await deleteFurnitureFx(selectedId)
        console.log("Серверная мебель удалена с сервера:", selectedId)
      } else {
        console.log("Локальная мебель удалена:", selectedId)
      }

      // Удаляем из каталога
      setFurnitureCatalog(furnitureCatalog.filter((f) => f.id !== selectedId))
      setSnackbar({
        open: true,
        message: "Мебель удалена",
        severity: "success",
      })
    } catch (e) {
      console.error("Ошибка при удалении мебели:", e)
      setSnackbar({
        open: true,
        message: "Ошибка при удалении",
        severity: "error",
      })
    } finally {
      handleCloseMenu()
    }
  }

  const handleSave = async () => {
    try {
      if (!currentFloorId) throw new Error("Этаж не выбран")
      setLoading(true)

      for (const item of furnitureOnMap) {
        // Проверяем, что у мебели есть photoUrl
        if (!item.photo) {
          console.error("У мебели отсутствует photoUrl:", item)
          continue
        }

        // Используем координаты относительно изображения
        const position_x = item.position.position_x
        const position_y = item.position.position_y

        // Проверяем, является ли мебель серверной
        if (serverFurnitureIds.has(item.id)) {
          console.log("Обновляем позицию серверной мебели:", item.id)

          // Обновляем позицию серверной мебели
          await updateFurniturePositionFx({
            furnitureId: item.id,
            data: { position_x, position_y },
          })

          // Обновляем UI серверной мебели (размер и угол)
          const normalizedAngle = ((item.angle % 360) + 360) % 360
          await updateFurnitureUIFx({
            furnitureId: item.id,
            data: {
              angle: normalizedAngle,
              sizeFactor: item.width,
            },
          })
        } else {
          console.log("Сохраняем новую мебель:", item)

          // Первый запрос: разместить мебель на этаже
          const furnitureData: FurnitureData = {
            name: item.name,
            position: { position_x, position_y },
            photoUrl: item.photo,
          }

          const furnitureResponse = await addFurnitureFx({
            floorId: currentFloorId,
            data: furnitureData,
          })

          const sizeFactor = item.width

          console.log("Отправляем sizeFactor:", {
            furnitureId: furnitureResponse.id,
            itemWidth: item.width,
            calculatedSizeFactor: sizeFactor,
            item: item,
          })

          const normalizedAngle = ((item.angle % 360) + 360) % 360
          await updateFurnitureUIFx({
            furnitureId: furnitureResponse.id,
            data: {
              angle: normalizedAngle,
              sizeFactor: sizeFactor,
            },
          })
        }
      }
      setOpen(false)
      onSelectLayer(["workspace", "room", "emergency", "utility"])
      setEditable(false)

      setSnackbar({
        open: true,
        message: "Вся мебель успешно сохранена",
        severity: "success",
      })
    } catch (e) {
      console.error("Ошибка при сохранении мебели:", e)
      setSnackbar({
        open: true,
        message: "Ошибка при сохранении мебели",
        severity: "error",
      })
    } finally {
      setLoading(false)
    }
  }

  const handlePanelClick = (e: React.MouseEvent) => {
    e.stopPropagation()
  }

  return (
    <>
      <Paper
        onClick={handlePanelClick}
        sx={{
          position: "fixed",
          top: 120,
          right: open ? 0 : -220,
          width: 200,
          maxHeight: "calc(100vh - 120px)",
          bgcolor: "white",
          boxShadow: 3,
          zIndex: 1100,
          transition: "right 0.3s ease",
          overflowY: "auto",
        }}
      >
        <Box sx={{ p: 2, borderBottom: 1, borderColor: "divider" }}>
          <Typography variant="h6" sx={{ mb: 1 }}>
            Каталог мебели
          </Typography>
          <Button
            variant="contained"
            size="small"
            onClick={() => setCreateFormOpen(true)}
            fullWidth
          >
            Новая мебель
          </Button>
        </Box>

        <div style={{ padding: 10 }}>
          {furnitureCatalog.map((item) => (
            <div
              key={item.id}
              onContextMenu={(e) => handleContextMenu(e, item.id)} // 🔹 ПКМ для меню
            >
              <Furniture {...item} onClick={() => addFurniture(item)} />
            </div>
          ))}
        </div>

        <div
          style={{
            position: "sticky",
            bottom: 0,
            background: "white",
            padding: 8,
          }}
        >
          <Button
            fullWidth
            variant="contained"
            onClick={handleSave}
            disabled={loading}
          >
            {loading ? "Сохраняем..." : "Сохранить"}
          </Button>
        </div>
      </Paper>

      {/* 🔹 Контекстное меню */}
      <PositionedMenuFurniture
        menuPos={menuPos}
        open={Boolean(menuPos)}
        onClose={handleCloseMenu}
        onDelete={handleDelete}
      />

      <Snackbar
        open={snackbar.open}
        autoHideDuration={4000}
        onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
        anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
      >
        <Alert
          severity={snackbar.severity}
          onClose={() => setSnackbar((s) => ({ ...s, open: false }))}
          sx={{ width: "100%" }}
        >
          {snackbar.message}
        </Alert>
      </Snackbar>

      <CreateFurnitureForm
        open={createFormOpen}
        onClose={() => setCreateFormOpen(false)}
      />
    </>
  )
}

export default AddingFurniture
